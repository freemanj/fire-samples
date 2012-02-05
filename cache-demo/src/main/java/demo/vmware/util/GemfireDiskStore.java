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

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.DiskStore;

public class GemfireDiskStore {
    private Cache cache;
    private String diskStoreName;
    private File baseDir;
    private DiskStore store;

    public void init() {
        store = cache.createDiskStoreFactory().setDiskDirs(new File[] { baseDir }).create(getDiskStoreName());
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public String getDiskStoreName() {
        return diskStoreName;
    }

    public void setDiskStoreName(String diskStoreName) {
        this.diskStoreName = diskStoreName;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public DiskStore getStore() {
        return store;
    }

    public void setStore(DiskStore store) {
        this.store = store;
    }

}
