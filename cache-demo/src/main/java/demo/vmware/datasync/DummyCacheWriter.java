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

import org.apache.log4j.Logger;

import com.gemstone.gemfire.cache.CacheWriter;
import com.gemstone.gemfire.cache.CacheWriterException;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.RegionEvent;

import demo.vmware.util.CacheBasedPropertiesBinder;

/**
 * 
 * Pretends to be a cache writer but just logs. CacheWriter let us do cache write-through. You use the WAN gateway
 * listener to write-behind.
 * <p>
 * Could extend CacheWriterAdapter to reduce the number of implemented methods
 * <p>
 * Methods can throw CacheWriterException to abort an operation.
 * 
 */
public class DummyCacheWriter implements CacheWriter<Object, Object> {

    private static final Logger LOG = Logger.getLogger(DummyCacheWriter.class);

    /** could autowire this since there "will be only one" */
    private CacheBasedPropertiesBinder propertiesBinder;

    @Override
    public void beforeCreate(EntryEvent<Object, Object> arg0) throws CacheWriterException {
        LOG.debug("beforeCreate() " + messageLog(arg0) + " CacheWriterEnable="
                + propertiesBinder.isCacheWriterEnabled(arg0.getRegion().getName()));

    }

    @Override
    public void beforeDestroy(EntryEvent<Object, Object> arg0) throws CacheWriterException {
        LOG.debug("beforeDestroy() " + messageLog(arg0) + " CacheWriterEnable="
                + propertiesBinder.isCacheWriterEnabled(arg0.getRegion().getName()));
    }

    @Override
    public void beforeRegionClear(RegionEvent arg0) throws CacheWriterException {
        LOG.debug("beforeRegionClear: " + arg0.getRegion().getName());
    }

    @Override
    public void beforeRegionDestroy(RegionEvent arg0) throws CacheWriterException {
        LOG.debug("beforeRegionDestroy: " + arg0.getRegion().getName());
    }

    @Override
    public void beforeUpdate(EntryEvent<Object, Object> arg0) throws CacheWriterException {
        LOG.debug("beforeUpdate() " + messageLog(arg0) + " CacheWriterEnable="
                + propertiesBinder.isCacheWriterEnabled(arg0.getRegion().getName()));
    }

    @Override
    public void close() {
        LOG.debug("close()");
    }

    /**
     * Creates a string that describes the passed in event.
     * 
     * @param event
     * @return
     */
    private String messageLog(EntryEvent<Object, Object> event) {
        Object key = event.getKey();
        Object value = event.getNewValue();

        if (event.getOperation().isUpdate()) {
            return "[" + key + "] from [" + event.getOldValue() + "] to [" + event.getNewValue() + "]";
        }
        return "[" + key + "=" + value + "]";
    }

    /** setter for spring */
    public void setPropertiesBinder(CacheBasedPropertiesBinder propertiesBinder) {
        this.propertiesBinder = propertiesBinder;
    }

}
