package demo.vmware.datasync;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.gemstone.gemfire.cache.CacheWriterException;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheWriterAdapter;

import demo.vmware.util.CacheBasedPropertiesBinder;
import demo.vmware.util.DataLoaderUtils;

/**
 * This write-through cache implementation is currently broken. It supports INSERT but not UPDATE
 * <p>
 * This class is not efficient and is purly a demo.
 * 
 * @author freemanj
 * 
 */
public class CacheWriterAutoMapped extends CacheWriterAdapter<Object, Object> {

    private static final Logger LOG = Logger.getLogger(CacheWriterAutoMapped.class);

    /** could autowire this since there "will be only one" */
    private CacheBasedPropertiesBinder propertiesBinder;
    /** may not be a good candidate for autowire if more than one db */
    private DataSource dataSource;
    /** provide access to the automap configs */
    private DataLoaderUtils dataLoaderUtilities;

    @Override
    public void beforeCreate(EntryEvent<Object, Object> arg0) throws CacheWriterException {
        String regionName = arg0.getRegion().getName();
        if (propertiesBinder.isCacheWriterEnabled(regionName)) {
            insertOrUpdate(arg0);
            LOG.debug("about to start creating row in db in region " + regionName);
        } else {
            LOG.debug("beforeCreate() cache writing disabled for region " + regionName);
        }
    }

    @Override
    public void beforeDestroy(EntryEvent<Object, Object> arg0) throws CacheWriterException {
        String regionName = arg0.getRegion().getName();
        if (propertiesBinder.isCacheWriterEnabled(regionName)) {
            LOG.debug("about to start creating row in db in region " + regionName);
        } else {
            LOG.debug("beforeDestroy() cache writing disabled for region " + regionName);
        }
    }

    @Override
    public void beforeUpdate(EntryEvent<Object, Object> arg0) throws CacheWriterException {
        String regionName = arg0.getRegion().getName();
        if (propertiesBinder.isCacheWriterEnabled(regionName)) {
            insertOrUpdate(arg0);
            LOG.debug("updated row in db in region " + regionName);

        } else {
            LOG.debug("beforeUpdate() cache writing disabled for region " + regionName);
        }
    }

    /** for spring wiring */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** for spring wiring */
    public void setPropertiesBinder(CacheBasedPropertiesBinder propertiesBinder) {
        this.propertiesBinder = propertiesBinder;
    }

    /** for spring wiring */
    public void setDataLoaderUtilities(DataLoaderUtils utils) {
        this.dataLoaderUtilities = utils;
    }

    /**
     * Future behavior: attempts to do an insert and then an update if the insert fails.
     * 
     * @param arg0
     */
    void insertOrUpdate(EntryEvent<Object, Object> arg0) {
        String regionName = arg0.getRegion().getName();
        // don't know if this guy is as fuzzy as the inbound batch code
        // this fails if the row already exists in the database
        SimpleJdbcInsert insert = new SimpleJdbcInsert(new JdbcTemplate(dataSource)).withTableName(dataLoaderUtilities
                .getMappedTableName(regionName));
        SqlParameterSource sqlArgsSource = new BeanPropertyItemSqlParameterSourceProvider()
                .createSqlParameterSource(arg0.getNewValue());
        try {
            insert.execute(sqlArgsSource);
        } catch (Exception e) {
            LOG.warn("failed because of ", e);
            // should try doing an update instead

        }
    }

}
