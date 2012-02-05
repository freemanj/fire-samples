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

/**
 * Sample class that retrieves a cache miss from another data source and inserts it into the cache to be returned.
 * Normally each region would have it's own loader class.
 */
public class DummyCacheLoader implements CacheLoader, Declarable {

    Logger LOG = Logger.getLogger(DummyCacheLoader.class);

    /** could autowire this since there "will be only one" */
    private CacheBasedPropertiesBinder propertiesBinder;

    @Override
    public void close() {
        LOG.debug("close()");
    }

    @Override
    public void init(Properties arg0) {
        LOG.debug("init() " + arg0);
    }

    @Override
    public Object load(LoaderHelper arg0) throws CacheLoaderException {
        LOG.debug("load() " + arg0 + " CacheLoaderEmable="
                + propertiesBinder.isCacheLoaderEnabled(arg0.getRegion().getName()));
        return null;
    }

    /** setter for spring */
    public void setPropertiesBinder(CacheBasedPropertiesBinder propertiesBinder) {
        this.propertiesBinder = propertiesBinder;
    }
}
