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
 * Gemfire caching client startup class. This runs a continuous query looking for certain changes. Cq can be combined
 * with any other client side functionality. It is broken out into it's own app here to make it easier to demonstrate
 * and modify.
 * 
 */
public class ClientCq {

    private static Logger LOG = Logger.getLogger(ClientCq.class);

    public static void main(String[] args) throws Exception {
        String resource[] = { "spring-cache-client-core.xml", "spring-cache-client-cq-only.xml" };
        ClassPathXmlApplicationContext mainContext = new ClassPathXmlApplicationContext(resource, false);
        mainContext.setValidating(true);
        mainContext.refresh();

        LOG.info("awaiting query callback");
        Thread.sleep(Long.MAX_VALUE);

    }

}
