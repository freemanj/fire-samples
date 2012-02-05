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
package demo.vmware.client;

import org.apache.log4j.Logger;
import org.springframework.data.gemfire.listener.ContinuousQueryListener;

import com.gemstone.gemfire.cache.query.CqEvent;

/**
 * This uses the listener interface but it could instead be plugged into ContinuousQueryListenerAdapter which would let
 * us create custom messages for each even type
 * 
 */
public class ContinuousQueryListenerSample implements ContinuousQueryListener {

    Logger LOG = Logger.getLogger(ContinuousQueryListenerSample.class);

    @Override
    public void onEvent(CqEvent arg0) {
        LOG.info("continuous query generated " + arg0);
    }

}
