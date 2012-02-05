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

import demo.vmware.util.DataLoaderUtils;

/**
 * Load a region from a CSV file using spring batch components, the FlatFileReader and the BeanWrapperFileMapper
 * <p>
 * Accepts a region name and the file name.
 * <p>
 * Assumes
 * <ul>
 * <li>there is always a label row at the top of the csv file
 * <li>each object in cache is a single object represented by one row in spreadsheet
 * <li>the column headers match the model object attribute names "close enough" underlines are removed and it is not
 * case sensitive
 * <li>the cache key is the String value in the FIRST COLUMN of the csv file. It does not support complex keys
 * </ul>
 * <p>
 * Example from in eclipse
 * <ul>
 * <li>ATTRIBUTE src/test/datafiles/companies.csv</li>
 * <li>RELATIONSHIP src/test/datafiles/relationship.csv</li>
 * </ul>
 * 
 * @author freemanj
 */
public class CommandLoadRegionFromFile implements ICommand {

    /** loader utils that actually load data and store to the cache */
    private final DataLoaderUtils loaderUtils;

    /** constructor injection is best */
    public CommandLoadRegionFromFile(DataLoaderUtils loaderUtils) {
        this.loaderUtils = loaderUtils;
    }

    @Override
    public String commandDescription() {
        return "Load a region from a CSV file (absolute or relative to project root).";
    }

    @Override
    public String usageDescription() {
        return "<cmd> <region_name> <csv_file_name>";
    }

    @Override
    public int numberOfParameters() {
        return 2;
    }

    /**
     * this could be spring wired but I don't want to confuse anyone with a bunch of spring config for functionality
     * that is really just for this demo. Note that this is set up this way so that we don't have to create a different
     * class to load each region for each file type
     */
    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        String regionName = parameters.get(0);
        String fileName = parameters.get(1);

        // TODO turn off write-behind
        CommandResult result = loaderUtils.loadRegionFromResource(regionName, fileName);
        // TODO turn on write-behind
        return result;
    }

}
