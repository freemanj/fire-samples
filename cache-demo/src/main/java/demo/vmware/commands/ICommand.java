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
 * All plugable commands implement this interface
 * 
 * @author freemanj
 * 
 */
public interface ICommand {

    /** returns a basic description of the command for the menu */
    String commandDescription();

    /**
     * returns the usage string displayed in a command for the menu and when there is a parse error
     */
    String usageDescription();

    /** return the required number of parameters */
    int numberOfParameters();

    CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters);

}
