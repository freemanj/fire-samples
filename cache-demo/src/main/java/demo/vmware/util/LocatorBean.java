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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gemstone.bp.edu.emory.mathcs.backport.java.util.Arrays;
import com.gemstone.gemfire.distributed.Locator;
import com.gemstone.gemfire.distributed.internal.InternalLocator;
import com.gemstone.gemfire.i18n.LogWriterI18n;

/**
 * 
 * Fancy locator bean that lets us simply spring wire the instantiation of a Gemfire locator. This supports running n
 * locators on a single machine. It will try and auto-bind to one of the two predefined port addresses
 * 
 * 
 */
public class LocatorBean {

    final static Logger LOG = Logger.getLogger(LocatorBean.class);

    private boolean throwOnBindFailure = false;
    /** Gemfire locator instance that we contain */
    private Locator locator;
    private File log;
    private File state;
    private boolean peerLocator = true;
    private boolean serverLocator = true;

    /** optional bind address on machines with multiple interfaces */
    private InetAddress bind;
    private String hostnameForClients;
    /** fully qualified locator strings for all locators in cluster */
    private String locators;

    /** raw ports we scan to open this locator on */
    private List<String> locatorPortNumbers = new ArrayList<String>();
    /** default port numbers if none set */
    private String locatorPorts = "10334,10335";
    private int boundPort = -1;

    public String getLocators() {
        return locators;
    }

    public void setLocators(String locators) {
        this.locators = locators;
    }

    public void startLocator() {

        for (String tryPortAsString : locatorPortNumbers) {
            int tryPort = Integer.parseInt(tryPortAsString);
            LOG.debug("Attempting to start locator on " + tryPort);
            if (startLocator(tryPort)) {
                boundPort = tryPort;
                break;
            }
        }
        if (boundPort < 0) {
            throw new IllegalStateException("Unable to bind to locator port from list " + locatorPorts);
        }
    }

    /**
     * 
     * @param port
     * @return
     */
    public boolean startLocator(int port) {
        try {
            Properties props = new Properties();
            props.setProperty("locators", locators);
            props.setProperty("mcast-port", "0");
            locator = InternalLocator.startLocator(port, log, state, (LogWriterI18n) null, (LogWriterI18n) null, bind,
                    true, props, peerLocator, serverLocator, hostnameForClients);
            // locator = Locator.startLocatorAndDS(port, log, props);
            LOG.info("Started locator bind=" + bind + " port=" + port + " locators=" + locators);
            return true;
        } catch (Exception x) {
            LOG.debug("Locator start failure for port[" + port + "]: (" + x.getMessage() + ")");
            return false;
        }
    }

    public void stopLocator() {
        if (locator != null) {
            locator.stop();
            locator = null;
        }
    }

    public boolean isThrowOnBindFailure() {
        return throwOnBindFailure;
    }

    public void setThrowOnBindFailure(boolean throwOnBindFailure) {
        this.throwOnBindFailure = throwOnBindFailure;
    }

    public InetAddress getBind() {
        return bind;
    }

    public void setBind(InetAddress bind) {
        this.bind = bind;
    }

    public Locator getLocator() {
        return locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public File getLog() {
        return log;
    }

    public void setLog(File log) {
        this.log = log;
    }

    public File getState() {
        return state;
    }

    public void setState(File state) {
        this.state = state;
    }

    public boolean isPeerLocator() {
        return peerLocator;
    }

    public void setPeerLocator(boolean peerLocator) {
        this.peerLocator = peerLocator;
    }

    public boolean isServerLocator() {
        return serverLocator;
    }

    public void setServerLocator(boolean serverLocator) {
        this.serverLocator = serverLocator;
    }

    public String getHostnameForClients() {
        return hostnameForClients;
    }

    public void setHostnameForClients(String hostnameForClients) {
        this.hostnameForClients = hostnameForClients;
    }

    /** arrays.asList doesn't support generics */
    @SuppressWarnings("unchecked")
    public void setLocatorPorts(String locatorPorts) {
        this.locatorPorts = locatorPorts;
        locatorPortNumbers = Arrays.asList(locatorPorts.split(","));
    }

}
