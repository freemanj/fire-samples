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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.SecureRandom;

/**
 * 
 * This is part of the "make all instances of the same process run on different ports" framework
 * 
 */
public class ServerPortGenerator {
    /**
     * Super simple no-collision-detection method of grabbing a port. We use a large enough pool in hopes that our odds
     * of a collision are low
     * 
     * @return
     */
    public int generatePort() {
        SecureRandom random = new SecureRandom();
        int port = random.nextInt(10000);
        port += 40000;
        // implement a check to make sure port is not used.
        // on bind exception try again
        System.err.println("Server Port:" + port);
        return port;
    }

    /**
     * Returns a port that is available for use. It walks through the allowed port range attempting to open each one. It
     * returns the first one that it can open after closing it. There still could be a race condition between multiple
     * instances of this all oppened within a very short itme of each other.
     * 
     * @param min
     * @param max
     * @return
     * @throws IOException
     */
    public int generatePort(int min, int max) throws IOException {
        ServerSocket socket = new ServerSocket();
        int port = bind(socket, min, max - min);
        if (port > 0) {
            socket.close();
            return port;
        } else {
            throw new IOException("Unable to bind on to a prt between " + min + " and " + max);
        }

    }

    /**
     * Simple method that attmpts to figure out if a port is currently in use
     * 
     * @param socket
     * @param portstart
     * @param retries
     * @return
     * @throws IOException
     */
    protected int bind(ServerSocket socket, int portstart, int retries) throws IOException {
        InetSocketAddress addr = null;
        int port = portstart;
        while (retries > 0) {
            try {
                addr = new InetSocketAddress(port);
                socket.bind(addr);
                retries = 0;
                return port;
            } catch (IOException x) {
                retries--;
                if (retries <= 0) {
                    throw x;
                }
                port++;
            }
        }
        return -1;
    }

}
