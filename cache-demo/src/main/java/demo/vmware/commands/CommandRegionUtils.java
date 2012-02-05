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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;

import com.gemstone.gemfire.cache.query.SelectResults;

/**
 * reusable tools for the commands
 * 
 * @author freemanj
 * 
 */
public class CommandRegionUtils {

    final static Logger LOG = Logger.getLogger(CommandRegionUtils.class);

    /**
     * Fetches an entire region tied to a template
     * 
     * @param targetRegionName
     * @param targetRegionCount
     *            the number of objects we will send to the log files so folks can see keys and values
     * @param CommandResult
     *            the result is a SelectResults object and the messages are -- messages
     */
    public static CommandResult fetchOneRegion(String targetRegionName, int targetRegionCount,
            GemfireTemplate oneTemplate) {
        List<String> messages = new ArrayList<String>();

        CommandTimer timer = new CommandTimer();
        SelectResults allQueryResults = oneTemplate.find("select * from /" + targetRegionName);
        timer.stop();
        messages.add("Region " + targetRegionName + ": contains " + allQueryResults.size() + " elements" + " took "
                + timer.getTimeDiffInSeconds() + " secs");
        LOG.info("Region " + targetRegionName + ": contains " + allQueryResults.size() + " elements" + " took "
                + timer.getTimeDiffInSeconds() + " secs");
        // now log the first n elements
        if (targetRegionCount > 0) {
            int counter = targetRegionCount;
            Iterator<Object> foo = allQueryResults.iterator();
            while (foo.hasNext() && counter > 0) {
                messages.add("Region " + targetRegionName + ": " + foo.next());
                counter--;
            }
        }
        messages.add("Found " + allQueryResults.size() + " matching values");
        List<Object> results = new ArrayList<Object>();
        results.add(allQueryResults);
        return new CommandResult(results, messages);
    }

    /**
     * this is an argument for making this a singleton bean instead of static. We cold then make this
     * ApplicationContextAware
     * 
     * @param mainContext
     * @param targetRegionName
     * @return
     */
    public static GemfireTemplate findTemplateForRegionName(ApplicationContext mainContext, String targetRegionName) {
        Map<String, GemfireTemplate> allRegionTemplates = getAllGemfireTemplates(mainContext);
        for (String key : allRegionTemplates.keySet()) {
            GemfireTemplate oneTemplate = allRegionTemplates.get(key);
            if (oneTemplate.getRegion().getName().equals(targetRegionName)) {
                return oneTemplate;
            }
        }
        throw new IllegalArgumentException("region not found " + targetRegionName);
    }

    /**
     * this is an argument for making this a singleton bean instead of static. We cold then make this
     * ApplicationContextAware
     * 
     * @param mainContext
     * @return
     */
    public static Map<String, GemfireTemplate> getAllGemfireTemplates(ApplicationContext mainContext) {
        Map<String, GemfireTemplate> allRegionTemplates = mainContext.getBeansOfType(GemfireTemplate.class);
        return allRegionTemplates;
    }

}
