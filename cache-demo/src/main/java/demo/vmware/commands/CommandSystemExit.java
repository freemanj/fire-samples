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

import org.springframework.context.ConfigurableApplicationContext;

/**
 * Terminate this VM with extreme prejudice. This is almost always a bad idea
 * 
 * @author freemanj
 * 
 */
public class CommandSystemExit implements ICommand {

    @Override
    public String commandDescription() {
        return "Exit this program without hesitation";
    }

    @Override
    public String usageDescription() {
        return "<cmd>";
    }

    @Override
    public int numberOfParameters() {
        return 0;
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        System.exit(00);
        return new CommandResult(null, "system exited should never see this message");
    }

}
