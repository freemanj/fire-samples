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
package demo.vmware.util;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.gemstone.gemfire.admin.AdminException;
import com.gemstone.gemfire.admin.jmx.Agent;
import com.gemstone.gemfire.admin.jmx.AgentConfig;
import com.gemstone.gemfire.admin.jmx.AgentFactory;
import com.gemstone.gemfire.admin.jmx.internal.AgentConfigImpl;

/**
 * Lets us spring wire and start the agent
 * 
 * @author joefreeman
 * 
 */
public class JmxAgentBean {

    final static Logger LOG = Logger.getLogger(JmxAgentBean.class);

    private File logFile;
    private String locators;
    private int mcastPort;
    private int rmiPort;

    /**
     * Fire up the agent. This method never returns effectively making this a JMX agent only program if it's single
     * threaded. This has the side effect of possibly not cleaning up the log directory.
     */
    public void startAgent() {
        AgentConfig config = new AgentConfigImpl();
        config.setLocators(getLocators());
        config.setRmiPort(getRmiPort());
        config.setMcastPort(getMcastPort());
        config.setLogFile(logFile.getPath());
        // spin off the jmx agent so that we can return allowing this to deploy in a container
        ExecutorService taskExecutor = Executors.newFixedThreadPool(1);
        AgentRunable runJmx = new AgentRunable(config);
        taskExecutor.execute(runJmx);
        return;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File dir) {
        this.logFile = dir;
    }

    public int getMcastPort() {
        return mcastPort;
    }

    public void setMcastPort(int mcastPort) {
        this.mcastPort = mcastPort;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public void setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
    }

    public String getLocators() {
        return locators;
    }

    public void setLocators(String locators) {
        this.locators = locators;
    }

    public class AgentRunable implements Runnable {
        AgentConfig config;

        public AgentRunable(AgentConfig config) {
            this.config = config;
        }

        @Override
        public void run() {
            Agent agent;
            try {

                agent = AgentFactory.getAgent(config);
                LOG.debug("Starting JMX Agent");
                agent.start();
                // this never returns so the caller cannot do anything after this initialization
            } catch (AdminException e) {
                LOG.error("Exception starting agent", e);
            }
        }

    }

}
