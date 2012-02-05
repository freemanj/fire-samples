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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;

/**
 * This command lets you modify a first level attribute on any object form any region
 * <p>
 * This command runs with 4 parameters
 * <ul>
 * <li>region name</li>
 * <li>primary key</li>
 * <li>property name</li>
 * <li>new value</li>
 * </ul>
 * 
 * The simplest thing to test is a string attribute like "CONTACT" region's "phone" field
 * 
 * @author freemanj
 * 
 */
public class CommandModifyStringAttribute implements ICommand {

    Logger LOG = Logger.getLogger(CommandModifyStringAttribute.class);

    @Override
    public String commandDescription() {
        return "Modify a string attribute.";
    }

    @Override
    public String usageDescription() {
        return "<cmd> <region_name> <pk> <property_name> <attribute_name>";
    }

    @Override
    public int numberOfParameters() {
        return 4;
    }

    @Override
    public CommandResult run(ConfigurableApplicationContext mainContext, List<String> parameters) {
        String regionName = parameters.get(0);
        String pk = parameters.get(1);
        String attributeName = parameters.get(2);
        String attributeValue = parameters.get(3);
        GemfireTemplate template = CommandRegionUtils.findTemplateForRegionName(mainContext, regionName);
        Object modelObject = template.get(pk);
        if (modelObject == null) {
            LOG.error("no object found in region " + regionName + " for key " + pk);
            return new CommandResult(null, " no object found in region " + regionName + " for key " + pk);
        } else {
            setValue(modelObject, attributeName, attributeValue);
            template.put(pk, modelObject);
            return new CommandResult(null, "Attribute " + attributeName + " on object with key " + pk + " in region "
                    + regionName + " successfully set to " + attributeValue);
        }
    }

    /**
     * reflection based method that lets us modify objects
     * 
     * @param modelObject
     * @param attributeName
     * @param attributeValue
     */
    void setValue(Object modelObject, String attributeName, String attributeValue) {
        @SuppressWarnings("rawtypes")
        Class params[] = { String.class };
        try {
            String methodName = "set" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
            Method setMethod = modelObject.getClass().getMethod(methodName, params);
            setMethod.invoke(modelObject, attributeValue);
            LOG.info("Object state: " + modelObject);
        } catch (SecurityException e) {
            LOG.error("unable to set value");
        } catch (NoSuchMethodException e) {
            LOG.error("unable to set value");
        } catch (IllegalAccessException e) {
            LOG.error("unable to set value");
        } catch (InvocationTargetException e) {
            LOG.error("unable to set value");
        }
    }
}
