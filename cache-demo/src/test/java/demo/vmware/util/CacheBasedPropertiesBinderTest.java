package demo.vmware.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for CacheBasedPropertiesBinder
 * 
 * @author freemanj
 * 
 */
public class CacheBasedPropertiesBinderTest {

    @Test
    public void testGetCacheLoaderEnablePropertyNames() {
        CacheBasedPropertiesBinder jig;
        jig = new CacheBasedPropertiesBinder();
        assertEquals(1, jig.getCacheLoaderEnablePropertyNames(null).size());
        assertEquals(2, jig.getCacheLoaderEnablePropertyNames("foo").size());
    }
}
