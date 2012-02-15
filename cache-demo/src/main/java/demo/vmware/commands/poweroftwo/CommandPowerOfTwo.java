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
package demo.vmware.commands.poweroftwo;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import demo.vmware.commands.CommandResult;
import demo.vmware.commands.ICommand;

/**
 * Calculates a power of two and caches the value using @Cacheable so it doesn't have to recalculate
 * 
 */
public class CommandPowerOfTwo implements ICommand {

    public static final Logger LOG = Logger.getLogger(CommandPowerOfTwo.class);

    /**
     * Example of autowiring. There is one helper class that implements this interface type in the context file. We do
     * this so we can pick up the proxy for that guy so the @Cacheable aspect works.
     */
    @Autowired
    ICommandPowerOfTwoHelper helper;

    @Override
    public String commandDescription() {
        return "Generate 2^n as a demo of @Cacheable.";
    }

    @Override
    public String usageDescription() {
        return "<cmd> <n>";
    }

    @Override
    public int numberOfParameters() {
        return 1;
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        try {
            int initialHitCount = helper.getHitCount();
            int targetNumber = Integer.parseInt(parameters.get(0));
            int result = helper.calculateNthPower(targetNumber);
            int finalHitCounter = helper.getHitCount();
            if (finalHitCounter > initialHitCount) {
                return new CommandResult(new Integer(result), "Calculated 2^" + targetNumber + " is " + result);
            } else {
                return new CommandResult(new Integer(result), "Retrieved previously calculated 2^" + targetNumber
                        + " is " + result);
            }
        } catch (NumberFormatException e) {
            return new CommandResult(null, "Invalid N specified for 2^nth power:" + parameters.get(0));
        }
    }

}
