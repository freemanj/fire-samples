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

/**
 * Command result class so we can standardize message display and results aggregation
 * 
 * @author freemanj
 * 
 */
public class CommandResult {

    private List<Object> results = new ArrayList<Object>();
    private List<String> messages = new ArrayList<String>();

    /**
     * 
     * @param results
     *            optional results - not all commands generate results
     * @param messages
     *            optional messages - logged by command processor
     */
    public CommandResult(List<Object> results, List<String> messages) {
        if (results != null) {
            this.results = results;
        }
        if (messages != null) {
            this.messages = messages;
        }
    }

    public CommandResult(Object oneResult, String oneMessage) {
        if (oneResult != null) {
            results = new ArrayList<Object>();
            results.add(oneResult);
        }
        if (oneMessage != null) {
            messages = new ArrayList<String>();
            messages.add(oneMessage);
        }
    }

    /**
     * arbitrary return data depends on the command
     * 
     * @return
     */
    public List<Object> getResults() {
        return results;
    }

    /**
     * messages for the user
     * 
     * @return
     */
    public List<String> getMessages() {
        return messages;
    }

}
