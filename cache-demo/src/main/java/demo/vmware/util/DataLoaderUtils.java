package demo.vmware.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.Region;

import demo.vmware.commands.CommandResult;
import demo.vmware.commands.CommandTimer;

/**
 * Utility class to support loading of the cache from the DB. This is a very inefficient way of loading files and
 * databases using auto-mapping. Real applications would do their own mapping or custom loading.
 * <p>
 * <b> NOTE: This class only supports unique primary keys in the DB. This is a demo limitation and not a Gemfire
 * limitation.</b>
 * <p>
 * Uses Spring Batch plumbing.
 * 
 * @author freemanj
 * 
 */
public class DataLoaderUtils {

    /** auto injected by spring */
    private final Cache cache;

    private static final Logger LOG = Logger.getLogger(DataLoaderUtils.class);

    /** FIXME: this should be spring injected */
    private static final int PUT_ALL_CHUNK_SIZE = 100;

    /**
     * The data source used to retrieve this region from the database. Needs to be spring wired Could be annotated with
     * the (at)Resource annotation but I didn't want this to be mandatory if someone wanted to reuse this class where
     * there was no database
     */
    private DataSource dataSource;

    /**
     * a map from the region names to prototype objects for that region
     */
    private final Map<String, Map<String, Object>> regionMappings;

    /**
     * Constructor
     * 
     * @param regionMappings
     */
    public DataLoaderUtils(Map<String, Map<String, Object>> regionMappings, Cache cache) {
        this.regionMappings = regionMappings;
        this.cache = cache;
    }

    /**
     * Extracts the key from the createdObject to be used for insertion into the cache
     * 
     * @param createdObject
     *            the object that was created as part of the load
     * @param assumptivePKField
     *            the assumptive pk key field. This value is ignored if the class implements IAutoCacheKey
     * @return the key this object should be stored under in the cache.
     */
    private Object extractPK(Object createdObject, String assumptivePkMethodName) {
        Object key = null;
        try {
            Method getMethod;
            getMethod = createdObject.getClass().getMethod(assumptivePkMethodName);
            key = getMethod.invoke(createdObject);
            return key;
        } catch (SecurityException e) {
            LOG.error("couldn't extract pk", e);
        } catch (NoSuchMethodException e) {
            LOG.error("couldn't extract pk", e);
        } catch (IllegalArgumentException e) {
            LOG.error("couldn't extract pk", e);
        } catch (IllegalAccessException e) {
            LOG.error("couldn't extract pk", e);
        } catch (InvocationTargetException e) {
            LOG.error("couldn't extract pk", e);
        }
        if (key == null) {
            LOG.warn("couldn't extract key");
            return null;
        } else {
            return key;
        }
    }

    /**
     * Looks up the assumptive pk method name for this region
     * 
     * @param regionName
     * @return
     */
    String getAssumptivePkMethodName(String regionName) {
        return (String) regionMappings.get(regionName).get("cacheKeyMethod");
    }

    /**
     * Look up the database primary key field for where clauses. Note we only support a single primary column in this
     * demonstration
     * 
     * @param regionName
     * @return
     */
    public String getDatabasePrimaryKeyField(String regionName) {
        return (String) regionMappings.get(regionName).get("databasePrimaryKeyField");
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * looks up the default model implementation class for this region
     * 
     * @param regionName
     * @return
     */
    public Object getMappedProtopype(String regionName) {
        return regionMappings.get(regionName).get("modelClass");
    }

    /**
     * Looks up the db table associated with this region
     * 
     * @param regionName
     * @return
     */
    public String getMappedTableName(String regionName) {
        return (String) regionMappings.get(regionName).get("databaseTable");
    }

    /**
     * Loads all of the regions we have region mappings for TODO remove need for main context - could get rid of Gemfire
     * template usage
     * 
     * @return
     */
    public CommandResult loadAllRegionFromDB() {
        ExecutorService taskExecutor = Executors.newFixedThreadPool(regionMappings.size());
        Collection tasks = new ArrayList<RegionLoader>();
        CommandTimer timer = new CommandTimer();
        for (String key : regionMappings.keySet()) {
            LOG.debug("queing up region " + key + " for load from database");
            tasks.add(new RegionLoader(key, this));
        }
        try {
            // invokeAll() returns when all tasks are complete
            List<Future<?>> futures = taskExecutor.invokeAll(tasks);
            taskExecutor.shutdown();
            LOG.debug("Loaded " + futures.size() + " regions in threads");
            // the futures hold the results at this point futures.get(X).get();
        } catch (InterruptedException e) {
            LOG.error("Unable to load all regions", e);
        }
        timer.stop();
        return new CommandResult(null, "Loading all regions took " + timer.getTimeDiffInSeconds() + " seconds");
    }

    /**
     * Load a region from a table in the database
     * 
     * @param regionName
     *            region name we wish to fill
     * @param whereClause
     *            an optional where clause. null or empty string mean get all. Do not add the word "WHERE"
     * @return CommandResult containing count of number retrieved and messages for the user
     */
    public CommandResult loadRegionFromDB(String regionName, String whereClause) {
        if (getDataSource() == null) {
            LOG.error("No database data source configured");
            return new CommandResult(0, "Command not configured:  No jdbc datasource specified");
        }
        String assumptivePkMethodName = getAssumptivePkMethodName(regionName);

        Region region = cache.getRegion(regionName);

        JdbcCursorItemReader<Object> itemReader = openItemReaderToDatabase(regionName, whereClause);

        int counter = 0;
        Object readObject = "some dummy loop primer";
        /* so we can do large block putAll */
        Map<Object, Object> readSet = new HashMap<Object, Object>();
        while (readObject != null) {
            try {
                readObject = itemReader.read();
                if (readObject != null) {
                    LOG.debug("read object from DB: " + readObject);
                    Object key = extractPK(readObject, assumptivePkMethodName);
                    readSet.put(key, readObject);
                    counter++;
                    if (readSet.size() > PUT_ALL_CHUNK_SIZE) {
                        region.putAll(readSet);
                        readSet.clear();
                    }
                }
            } catch (Exception e) {
                LOG.error("Unable to load a row. Aborting", e);
                // push whatever we have. should we do this?
                region.putAll(readSet);
                readSet.clear();
                itemReader.close();
                // returning from a catch block is bad.
                return new CommandResult(0, "Failed reading object form database");
            }
        }
        region.putAll(readSet);
        readSet.clear();
        itemReader.close(); // could have done some nested finally block magic
        return new CommandResult(counter, "Read in " + counter + " rows from the database");
    }

    private JdbcCursorItemReader<Object> openItemReaderToDatabase(String regionName, String whereClause) {
        // we constructor injected a mapping from region to class just for this
        Object prototype = getMappedProtopype(regionName);
        String tableName = getMappedTableName(regionName);
        if (prototype == null) {
            throw new IllegalArgumentException("Command not configured: No prototype specified for regionName");
        }

        String selectSQL = "SELECT * from " + tableName;
        if (whereClause != null && whereClause.length() > 0) {
            selectSQL += " WHERE " + whereClause;
        }
        JdbcCursorItemReader<Object> itemReader = new JdbcCursorItemReader<Object>();
        itemReader.setDataSource(getDataSource());
        itemReader.setSql(selectSQL);
        // Column values are mapped based on matching the column name as obtained from result set metadata to public
        // setters for the corresponding properties. The names are matched either directly or by transforming a name
        // separating the parts with underscores to the same name using "camel" case.
        //
        // To facilitate mapping between columns and fields that don't have matching names, try using column aliases in
        // the SQL statement like "select fname as first_name from customer".
        // this is not the highest performing row mapper. custom row mappers will increas performance
        @SuppressWarnings({ "rawtypes", "unchecked" })
        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(prototype.getClass());
        itemReader.setRowMapper(rowMapper);
        ExecutionContext executionContext = new ExecutionContext();
        itemReader.open(executionContext);
        return itemReader;
    }

    /**
     * returns the first object matching the where clause.
     * <p>
     * TODO this should return a list even if it contains a single object
     * 
     * @param regionName
     *            target region.
     * @param whereClause
     *            optional where clause without the "WHERE"
     * @return null if object not found
     */
    public Object loadObjectFromDB(String regionName, String whereClause) {
        JdbcCursorItemReader<Object> itemReader = openItemReaderToDatabase(regionName, whereClause);
        Object readObject;
        try {
            readObject = itemReader.read();
        } catch (Exception e) {
            LOG.error("Received excepton trying to load", e);
            readObject = null;
        } finally {
            itemReader.close();
        }
        return readObject;
    }

    /**
     * Load a region from a delimited resource with the column names in the first row.
     * <p>
     * Uses Spring Batch plumbing
     * 
     * @param template
     *            GemfireTemplate for the target region
     * @param prototype
     *            prototype object that will be "cloned" when creating objects
     * @param fileName
     *            name of data file
     * @return CommandResult containing messages for the user
     */
    public CommandResult loadRegionFromResource(String regionName, String fileName) {
        Region region = cache.getRegion(regionName);

        // we constructor injected a mapping from region to class just for this
        Object prototype = getMappedProtopype(regionName);
        String assumptivePkMessage = getAssumptivePkMethodName(regionName);

        FileSystemResource resourceInFilesystem = new FileSystemResource(fileName);
        if (!resourceInFilesystem.exists()) {
            LOG.error("can't find file " + fileName);
            return new CommandResult(0, "can't find file " + fileName);
        }

        // set up the Spring Batch subsystem to load our CSV files
        BeanWrapperFieldSetMapper<Object> fieldSetMapper = new BeanWrapperFieldSetMapper<Object>();
        fieldSetMapper.setTargetType(prototype.getClass());

        // create a subclass of the standard DelimitedLineHandler so we can intercept the header line
        // to set up our metadata
        SkippedDelimitedLineTokenizer tokenizer = new SkippedDelimitedLineTokenizer(
                DelimitedLineTokenizer.DELIMITER_COMMA);
        DefaultLineMapper<Object> mapper = new DefaultLineMapper<Object>();
        mapper.setFieldSetMapper(fieldSetMapper);
        // DelimitedLineTokenizer defaults to comma as it's delimiter
        mapper.setLineTokenizer(tokenizer);

        // used to run the reader. we need it here so we can auto-build metadata in skipped line handler
        ExecutionContext executionContext = new ExecutionContext();

        FlatFileItemReader<Object> itemReader = new FlatFileItemReader<Object>();
        itemReader.setLineMapper(mapper);
        // spring batch 2.1? removed the native implementation that made first line headers
        itemReader.setLinesToSkip(1);
        itemReader.setSkippedLinesCallback(tokenizer);
        itemReader.setResource(resourceInFilesystem);
        itemReader.open(executionContext);

        String returnMessage;
        Boolean success = Boolean.FALSE;
        // Assume that the number read is small enough to fit in memory because we want to do a putAll
        // and we don't want to write chunking code
        Map<Object, Object> builtObjects = new HashMap<Object, Object>();
        Object createdObject;
        int counter = 0;
        try {
            CommandTimer timer = new CommandTimer();
            createdObject = itemReader.read();
            while (createdObject != null) {
                Object key = extractPK(createdObject, assumptivePkMessage);
                builtObjects.put(key, createdObject);
                counter++;
                createdObject = itemReader.read();
            }
            region.putAll(builtObjects);
            timer.stop();
            success = Boolean.TRUE;
            returnMessage = "Loaded " + builtObjects.size() + " rows in " + timer.getTimeDiffInSeconds() + " secs";
        } catch (UnexpectedInputException e) {
            LOG.error("UnexpectedInputException while reading", e);
            success = Boolean.FALSE;
            returnMessage = "Failed to get input.";
        } catch (ParseException e) {
            LOG.error("ParseException while reading", e);
            success = Boolean.FALSE;
            returnMessage = "Failed to parse.";
        } catch (Exception e) {
            LOG.error("Exception while reading", e);
            success = Boolean.FALSE;
            returnMessage = "Failed to load.";
        }
        return new CommandResult(counter, returnMessage);
    }

    /**
     * for spring wiring
     * 
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Callable so we can multi-thread
     * 
     * @author freemanj
     * 
     */
    private class RegionLoader implements Callable<Integer> {

        private final String regionName;
        private final DataLoaderUtils utils;

        public RegionLoader(String regionName, DataLoaderUtils utils) {
            this.regionName = regionName;
            this.utils = utils;
        }

        /** returns the number loaded */
        @Override
        public Integer call() {
            CommandResult results = utils.loadRegionFromDB(regionName, null);
            LOG.info("load returned: " + results);
            return (Integer) results.getResults().get(0);
        }

    }

    /**
     * Adds meta data to execution context -- built on assumption skipped line of CSV has attribute names
     */
    public class SkippedDelimitedLineTokenizer extends DelimitedLineTokenizer implements LineCallbackHandler {

        String headerLine = null;
        String headerNames[] = null;

        public SkippedDelimitedLineTokenizer(char delimiterComma) {
            super(delimiterComma);
        }

        @Override
        public void handleLine(String line) {
            // called whenever line is skipped. should only skip 1 line or the last line skipped needs to be headers
            headerLine = line;
        }

        @Override
        public FieldSet tokenize(final String line) {
            // overide this method so we can make sure we have names setup.
            // in batch 1.1.x they had built in functionality for this
            if (!hasNames()) {
                headerNames = super.tokenize(headerLine).getValues();
                super.setNames(headerNames);
            }
            return super.tokenize(line);
        }

    }

}
