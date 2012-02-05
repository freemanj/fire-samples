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

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;

/**
 * Retrieves all data from all caches and shows the first n objects of each type
 * 
 * @author freemanj
 * 
 */
public class CommandGetOneRegion implements ICommand {

    final static Logger LOG = Logger.getLogger(CommandLoadRegionFromDatabase.class);

    @Override
    public String commandDescription() {
        return "Retrieve entire <cacheName> and show the first <rowCount> entries (requires template)";
    }

    @Override
    public String usageDescription() {
        return "<cmd> <cacheName> <showRowCount>";
    }

    @Override
    public int numberOfParameters() {
        return 2;
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        String targetRegionName = parameters.get(0);
        int targetRegionCount = Integer.parseInt(parameters.get(1));
        GemfireTemplate oneTemplate = CommandRegionUtils.findTemplateForRegionName(mainContext, targetRegionName);
        return fetchOneRegion(targetRegionName, targetRegionCount, oneTemplate);
    }

    /**
     * Fetches an entire region tied to a template
     * <p>
     * Exists to support unit testing override
     * 
     * @param targetRegionName
     * @param targetRegionCount
     * @param oneTemplate
     */
    private CommandResult fetchOneRegion(String targetRegionName, int targetRegionCount, GemfireTemplate oneTemplate) {
        return CommandRegionUtils.fetchOneRegion(targetRegionName, targetRegionCount, oneTemplate);
    }

}
