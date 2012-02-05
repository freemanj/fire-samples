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
import java.util.concurrent.CancellationException;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.control.RebalanceOperation;
import com.gemstone.gemfire.cache.control.RebalanceResults;
import com.gemstone.gemfire.cache.control.ResourceManager;

/**
 * Rebalance the servers
 * 
 * @author freemanj
 * 
 */
public class CommandRebalance implements ICommand {

    final static Logger LOG = Logger.getLogger(CommandRebalance.class);

    @Override
    public String commandDescription() {
        return "Rebalance cache. Only works on data/server node";
    }

    @Override
    public String usageDescription() {
        return "<cmd>";
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        List<String> messages = new ArrayList<String>();
        Cache cache = (Cache) mainContext.getBean("cache");
        ResourceManager manager = cache.getResourceManager();
        RebalanceOperation op = manager.createRebalanceFactory().start();
        // Wait until the rebalance is complete
        RebalanceResults results;
        try {
            results = op.getResults();
            messages.add("Rebalance Took " + results.getTotalTime() + " milliseconds\n");
            messages.add("Rebalance Transfered " + results.getTotalBucketTransferBytes() + "bytes\n");
        } catch (CancellationException e) {
            LOG.warn("Rebalance failed: ", e);
        } catch (InterruptedException e) {
            LOG.warn("Rebalance failed: ", e);
        }
        return new CommandResult(null, messages);
    }

    @Override
    public int numberOfParameters() {
        return 0;
    }

}
