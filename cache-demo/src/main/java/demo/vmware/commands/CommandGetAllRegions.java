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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;

import com.gemstone.gemfire.cache.query.SelectResults;

/**
 * Retrieve all of the content for all of the regions in parallel threads
 * 
 * @author freemanj
 */
public class CommandGetAllRegions implements ICommand {

    final static Logger LOG = Logger.getLogger(CommandGetAllRegions.class);

    /** should we do parallel fetch */
    private final boolean parallelFetch;

    public CommandGetAllRegions(boolean parallelFetch) {
        this.parallelFetch = parallelFetch;
    }

    @Override
    public String commandDescription() {
        return "Retrieve all data in all Regions. Does not display. May run parallel fetch (uses templates)";
    }

    @Override
    public String usageDescription() {
        return "<cmd>";
    }

    @Override
    public int numberOfParameters() {
        return 0;
    }

    /**
     * Retrieve the contents for all regions, one region per task executor
     */
    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        // Use the template for this meaning we can only fetch the whole contents of any region we have a template for
        // CommandGetCounts goes right to the cache so it may list more regions
        Map<String, GemfireTemplate> allRegionTemplates = CommandRegionUtils.getAllGemfireTemplates(mainContext);

        // use the Java executor service because of it's awesome invokeAll method.
        ExecutorService taskExecutor = Executors.newFixedThreadPool(allRegionTemplates.size());
        Collection tasks = new ArrayList<RegionFetcher>();

        CommandTimer timer = new CommandTimer();
        for (String key : allRegionTemplates.keySet()) {
            GemfireTemplate oneTemplate = allRegionTemplates.get(key);
            if (parallelFetch) {
                tasks.add(new RegionFetcher(oneTemplate.getRegion().getName(), 0, oneTemplate));
            } else {
                // don't write anything out and don't capture results
                fetchOneRegion(oneTemplate.getRegion().getName(), 5, oneTemplate);
            }
        }
        if (parallelFetch) {
            // invokeAll() returns when all tasks are complete
            try {
                List<Future<?>> futures = taskExecutor.invokeAll(tasks);
                taskExecutor.shutdown();
                LOG.info("Fetched " + futures.size() + " regions in threads");
                // the futures hold the results at this point futures.get(X).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        timer.stop();
        return new CommandResult(null, "Loading all regions took " + timer.getTimeDiffInSeconds() + " seconds");
    }

    /**
     * Fetches an entire region tied to a template.
     * <p>
     * Exists to support unit testing override
     * 
     * @param targetRegionName
     * @param targetRegionCount
     * @param oneTemplate
     * @return SelectResults
     */
    private CommandResult fetchOneRegion(String targetRegionName, int targetRegionCount, GemfireTemplate oneTemplate) {
        return CommandRegionUtils.fetchOneRegion(targetRegionName, targetRegionCount, oneTemplate);
    }

    /**
     * Fetches the contents of a region. used by task executor
     * 
     * @author freemanj
     * 
     */
    // we don't know the types since this can call any region
    @SuppressWarnings("rawtypes")
    private class RegionFetcher implements Callable<SelectResults> {

        private final String targetRegionName;
        private final int targetRegionCount;
        private final GemfireTemplate oneTemplate;

        public RegionFetcher(String targetRegionName, int targetRegionCount, GemfireTemplate oneTemplate) {
            this.targetRegionName = targetRegionName;
            this.targetRegionCount = targetRegionCount;
            this.oneTemplate = oneTemplate;
        }

        @Override
        public SelectResults call() {
            // this is really ugly side effect of the way we wrap results in a CommandResult
            CommandResult results = CommandRegionUtils.fetchOneRegion(targetRegionName, targetRegionCount, oneTemplate);
            return (SelectResults) results.getResults().get(0);
        }

    }

}
