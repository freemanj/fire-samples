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

import java.util.Date;

/**
 * Usefull timer for timing commands
 * 
 * @author freemanj
 * 
 */
public class CommandTimer {

    Date startTimer;
    Date endTimer;

    /**
     * Timer starts on instantiation
     */
    public CommandTimer() {
        startTimer = new Date();
    }

    /**
     * Stops the timer. Good if you want to stop the timer and then do something else before getting difference
     */
    public void stop() {
        endTimer = new Date();
    }

    /**
     * Calculates the time between start and stop. Calls stop if it hasn't already been called
     * 
     * @return
     */
    public double getTimeDiffInSeconds() {
        if (endTimer == null) {
            stop();
        }
        long timeDiffInMsec = endTimer.getTime() - startTimer.getTime();
        double timeDiff = timeDiffInMsec / 1000.0;
        return timeDiff;
    }

}
