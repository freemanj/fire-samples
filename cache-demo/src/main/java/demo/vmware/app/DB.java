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

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * PoC and teaching aid that starts a demonstration database.
 */
public class DB {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String resource = ("spring-db.xml");
        ClassPathXmlApplicationContext mainContext = new ClassPathXmlApplicationContext(new String[] { resource },
                false);
        mainContext.setValidating(true);
        mainContext.refresh();

        Thread.sleep(Long.MAX_VALUE);
    }

}
