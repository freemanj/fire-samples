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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;

import com.gemstone.gemfire.cache.GemFireCache;
import com.gemstone.gemfire.cache.Region;

/**
 * Logs the size of each region we have a template for
 * <ul>
 * <li>On server nodes this shows the size of the region</li>
 * <li>On client nodes this shows the size of the local cache on the client</li>
 * </ul>
 * 
 * @author freemanj
 */
public class CommandGetCounts implements ICommand {

    final static Logger LOG = Logger.getLogger(CommandGetCounts.class);

    @Override
    public String commandDescription() {
        return "Return Cache sizes for all regions. Clients with local caches return the local cache size";
    }

    @Override
    public String usageDescription() {
        return "<cmd>";
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        // This will list all of the root regions. Not that it does not use templates like CommandGetAllRegions so this
        // may list more regions
        // we use the template API in CommandGetAllRegions so this may show regions we can't query
        GemFireCache foo = mainContext.getBean("cache", GemFireCache.class);
        Set<Region<?, ?>> allRegions = foo.rootRegions();

        List<String> regionMessages = new ArrayList<String>();
        for (Region oneRegion : allRegions) {
            regionMessages.add("Region: " + oneRegion.getName() + " contains " + oneRegion.size() + " elements");
        }
        return new CommandResult(null, regionMessages);
    }

    @Override
    public int numberOfParameters() {
        return 0;
    }

}
