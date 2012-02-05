/*
 * Copyright 2011 VMWare.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo.vmware.datasync;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.gemstone.gemfire.cache.CacheLoader;
import com.gemstone.gemfire.cache.CacheLoaderException;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.LoaderHelper;

import demo.vmware.util.CacheBasedPropertiesBinder;
import demo.vmware.util.DataLoaderUtils;

/**
 * Sample class that retrieves a cache miss from another data source and inserts it into the cache to be returned.
 * Normally each region would have it's own loader class.
 * <p>
 * This class is not efficient and is purly a demo.
 */
public class CacheLoaderAutoMapped implements CacheLoader, Declarable {

    Logger LOG = Logger.getLogger(CacheLoaderAutoMapped.class);

    /** could autowire this since there "will be only one" */
    private CacheBasedPropertiesBinder propertiesBinder;
    /** provide access to the automap configs */
    private DataLoaderUtils dataLoaderUtilities;

    @Override
    public void close() {
        LOG.debug("close()");
    }

    @Override
    public void init(Properties arg0) {
        LOG.debug("init() " + arg0);
    }

    @Override
    public Object load(LoaderHelper loadSupport) throws CacheLoaderException {
        String regionName = loadSupport.getRegion().getName();
        Object key = loadSupport.getKey();
        Object foundObject = null;
        if (propertiesBinder.isCacheLoaderEnabled(regionName)) {
            LOG.debug("load() " + loadSupport + " CacheLoaderEnable=true");
            String whereClause = "";
            // FIXME we don't escape the where clause. Think SQL injection risk from our own team.
            // could add date or other support
            if (key instanceof String) {
                whereClause = "" + dataLoaderUtilities.getDatabasePrimaryKeyField(regionName) + " = '" + key + "' ";
            } else {
                whereClause = "field = " + key + " ";
            }
            foundObject = dataLoaderUtilities.loadObjectFromDB(regionName, whereClause);
            LOG.debug("loaded " + foundObject);
        } else {
            foundObject = null;
            LOG.debug("load() " + loadSupport + " Not Loading");
        }
        return foundObject;
    }

    /** setter for spring */
    public void setDataLoaderUtilities(DataLoaderUtils dataLoaderUtilities) {
        this.dataLoaderUtilities = dataLoaderUtilities;
    }

    /** setter for spring */
    public void setPropertiesBinder(CacheBasedPropertiesBinder propertiesBinder) {
        this.propertiesBinder = propertiesBinder;
    }

}
