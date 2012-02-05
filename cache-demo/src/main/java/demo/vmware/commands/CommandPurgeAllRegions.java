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
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;

import com.gemstone.gemfire.cache.Region;

/**
 * Purge all data in all regions
 * 
 * @author freemanj
 * 
 */
public class CommandPurgeAllRegions implements ICommand {

    final static Logger LOG = Logger.getLogger(CommandPurgeAllRegions.class);

    @Override
    public String commandDescription() {
        return "Purge all data from cache: WARNING!";
    }

    @Override
    public String usageDescription() {
        return "<cmd>";
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        List<String> messages = new ArrayList<String>();
        Map<String, GemfireTemplate> allRegionTemplates = CommandRegionUtils.getAllGemfireTemplates(mainContext);
        for (String key : allRegionTemplates.keySet()) {
            GemfireTemplate oneTemplate = allRegionTemplates.get(key);
            // this throws unsupported operation error
            // oneTemplate.getRegion().clear();
            removeAllFromRegion(oneTemplate.getRegion());
            messages.add("Region: " + oneTemplate.getRegion().getName() + " contains " + oneTemplate.getRegion().size()
                    + " elements");
        }
        return new CommandResult(null, messages);
    }

    /**
     * Iterates across aregion deleting everything
     * 
     * @param region
     */
    private void removeAllFromRegion(Region<?, ?> region) {
        Set regionKeys = region.keySet();
        Object regionKeyArray[] = regionKeys.toArray();
        for (Object oneKey : regionKeyArray) {
            region.remove(oneKey);
        }

    }

    @Override
    public int numberOfParameters() {
        return 0;
    }
}
