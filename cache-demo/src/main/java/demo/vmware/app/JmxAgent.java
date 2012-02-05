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
package demo.vmware.app;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * You can run the JMX agent with this class or you can just run the agent directly using the Eclipse executor with the
 * following settings
 * 
 * <pre>
 * create a Run target in eclipse
 * Run Configurations
 * New Java Application
 * Name JMX Agent
 * MAIN_TYPE com.gemstone.gemfire.admin.jmx.internal.AgentLauncher
 * PROGRAM_ARGUMENTS server -dir=${project_loc:/reaaccom}/target/data locators=localhost[10334],localhost[10335] mcast-port=0 rmi-port=1099
 * VM_ARGUMENTS -DAgentLauncher.PRINT_LAUNCH_COMMAND=true
 * </pre>
 */
public class JmxAgent {

    private static final Logger LOG = Logger.getLogger(JmxAgent.class);

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String resource[] = { "spring-cache-jmxagent.xml" };
        ClassPathXmlApplicationContext mainContext = new ClassPathXmlApplicationContext(resource, false);
        mainContext.setValidating(true);
        mainContext.refresh();
        LOG.debug("Done attempting to start jmx agent");
        Thread.sleep(Long.MAX_VALUE);
    }

}
