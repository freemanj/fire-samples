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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;

/**
 * Retrieve a single object by key from the specified cache
 * 
 * @author freemanj
 * 
 */
public class CommandGetOneObject implements ICommand {

    final static Logger LOG = Logger.getLogger(CommandGetOneObject.class);

    @Override
    public String commandDescription() {
        return "Retrieve from <cacheName> the object specified by key <key>";
    }

    @Override
    public String usageDescription() {
        return "<cmd> <cacheName> <key>";
    }

    @Override
    public int numberOfParameters() {
        return 2;
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        List<String> messages = new ArrayList<String>();

        String targetRegionName = parameters.get(0);
        String targetKey = parameters.get(1);
        Map<String, GemfireTemplate> allRegionTemplates = CommandRegionUtils.getAllGemfireTemplates(mainContext);
        for (String key : allRegionTemplates.keySet()) {
            GemfireTemplate oneTemplate = allRegionTemplates.get(key);
            if (oneTemplate.getRegion().getName().equals(targetRegionName)) {
                Date startTimer = new Date();
                Object foundObject = oneTemplate.get(targetKey);
                Date endTimer = new Date();
                long timeDiffInMsec = endTimer.getTime() - startTimer.getTime();
                double timeDiff = timeDiffInMsec / 1000.0;
                messages.add("Region " + targetRegionName + ": get key=" + targetKey + " contains " + foundObject
                        + " took " + timeDiff + " secs");
            }
        }
        if (messages.size() == 0) {
            messages.add("No data found.");
        }
        return new CommandResult(null, messages);

    }

}
