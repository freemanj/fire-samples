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
package demo.vmware.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import demo.vmware.util.CacheBasedPropertiesBinder;

/**
 * sets a property in the property region.
 * <p>
 * You have to put the fully qualified property name if you want to enable writers and loaders in only one region
 * 
 * @author freemanj
 * 
 */
public class CommandSetProperty implements ICommand {

    @Autowired
    private CacheBasedPropertiesBinder propsBinder;

    @Override
    public String commandDescription() {
        return "Set a property in the property region. Ex 'cacheWriterEnable|cacheLoaderEnable' 'true|false'";
    }

    @Override
    public String usageDescription() {
        return "<cmd> <property-name> <new-value>";
    }

    @Override
    public int numberOfParameters() {
        return 2;
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        String propName = parameters.get(0);
        String newValue = parameters.get(1);
        // we assume we don't need to do key construction and that any region name is already in the key
        // where we use key, key.region
        if (CacheBasedPropertiesBinder.CACHE_LOADER_ENABLE_PROPERTY.equals(propName)) {
            return new CommandResult(propsBinder.setCacheLoaderEnabled("", newValue), "Cache Loader enable set to "
                    + newValue);
        } else if (CacheBasedPropertiesBinder.CACHE_WRITER_ENABLE_PROPERTY.equals(propName)) {
            return new CommandResult(propsBinder.setCacheWriterEnabled("", newValue), "Cache Writer enable set to "
                    + newValue);
        } else {
            return new CommandResult(null, "Invalid property specified " + propName);
        }
    }

    /**
     * setter for autowiring
     * 
     * @param propsBinder
     */
    public void setPropsBinder(CacheBasedPropertiesBinder propsBinder) {
        this.propsBinder = propsBinder;
    }
}
