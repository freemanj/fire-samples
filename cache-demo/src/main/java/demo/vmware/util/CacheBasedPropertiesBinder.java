package demo.vmware.util;

import java.util.ArrayList;
import java.util.List;

import com.gemstone.gemfire.cache.Region;

/**
 * Lets us get and set "system" properties in the cache properties region via static api. We use the cache to hold
 * configuration information so server side code can be enabled and disabled through the cache.
 * 
 * @author freemanj
 * 
 */
public class CacheBasedPropertiesBinder {

    /** public so command processor can use same string */
    public static final String CACHE_WRITER_ENABLE_PROPERTY = "cacheWriterEnable";
    /** public so command processor can use same string */
    public static final String CACHE_LOADER_ENABLE_PROPERTY = "cacheLoaderEnable";

    /** adapter to our properties region we expect to be <String,String> */
    private Region<String, String> propertiesRegion;

    /**
     * 
     * @param regionName
     *            region name or null if asking globally
     * @return
     */
    List<String> getCacheLoaderEnablePropertyNames(String regionName) {
        return getPropertyNames(CACHE_LOADER_ENABLE_PROPERTY, regionName);
    }

    /**
     * 
     * @param regionName
     *            region name or null if asking globally
     * @return
     */
    List<String> getCacheWriterEnablePropertyNames(String regionName) {
        return getPropertyNames(CACHE_WRITER_ENABLE_PROPERTY, regionName);
    }

    /**
     * creates a list of valid property names including an optional regionName
     * 
     * @param basePropertyName
     *            assumes this is non empty string
     * @param regionName
     * @return
     */
    List<String> getPropertyNames(String basePropertyName, String regionName) {
        List<String> returnValue = new ArrayList<String>();
        if (regionName != null && regionName.length() > 0) {
            returnValue.add(basePropertyName + "." + regionName);
        }
        returnValue.add(basePropertyName);
        return returnValue;
    }

    /**
     * package private so we can unit test
     * 
     * @param cacheWriterEnablePropertyNames
     *            list of properties to check in the order they are passed in
     * @param defaultValue
     *            the default value if no property found
     * @return calculated value
     */
    boolean getPropertyValue(List<String> cacheWriterEnablePropertyNames, boolean defaultValue) {
        boolean result = defaultValue;
        for (String oneName : cacheWriterEnablePropertyNames) {
            Object propValue = propertiesRegion.get(oneName);
            if (propValue != null) {
                // assume all properties are strings
                result = Boolean.getBoolean((String) propValue);
                break;
            }
        }
        return result;
    }

    /**
     * 
     * @param regionName
     *            individual regionName or null if global
     * @return
     */
    public boolean isCacheLoaderEnabled(String regionName) {
        return getPropertyValue(getCacheLoaderEnablePropertyNames(regionName), true);
    }

    /**
     * 
     * @param regionName
     *            individual regionName or null if global
     * @return
     */
    public boolean isCacheWriterEnabled(String regionName) {
        return getPropertyValue(getCacheWriterEnablePropertyNames(regionName), true);
    }

    /**
     * turn on or off cache writer
     * 
     * @param regionName
     * @param flag
     *            true|false converted to boolean when used
     * @return previous value
     */
    public String setCacheLoaderEnabled(String regionName, String flag) {
        String oldValue = "" + isCacheLoaderEnabled(regionName);
        String propertyName = CACHE_LOADER_ENABLE_PROPERTY;
        if (regionName != null && regionName.length() > 0) {
            propertyName += "." + regionName;
        }
        setPropertyValue(propertyName, flag);
        return oldValue;
    }

    /**
     * turn on or off cache writer
     * 
     * @param regionName
     * @param flag
     *            true|false converted to boolean when used
     * @return
     */
    public String setCacheWriterEnabled(String regionName, String flag) {
        String oldValue = "" + isCacheWriterEnabled(regionName);
        String propertyName = CACHE_WRITER_ENABLE_PROPERTY;
        if (regionName != null && regionName.length() > 0) {
            propertyName += "." + regionName;
        }
        setPropertyValue(propertyName, flag);
        return oldValue;
    }

    /** for spring wiring */
    public void setPropertiesRegion(Region<String, String> propertiesRegion) {
        this.propertiesRegion = propertiesRegion;
    }

    /**
     * Sets a property in the cache as a string representation of this boolean
     * 
     * @param propertyName
     * @param flag
     */
    private void setPropertyValue(String propertyName, String flag) {
        propertiesRegion.put(propertyName, Boolean.valueOf(flag).toString());
    }

}
