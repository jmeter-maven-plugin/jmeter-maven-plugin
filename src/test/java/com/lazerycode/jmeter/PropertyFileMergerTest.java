package com.lazerycode.jmeter;

import com.lazerycode.jmeter.propertiesHandler.PropertyFileMerger;
import junit.framework.TestCase;

import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Test utility class {@link PropertyFileMerger}
 */
public class PropertyFileMergerTest extends TestCase {
    
    public void testMergeProperties() throws Exception {

        URL testFile = getClass().getResource("/jmeter.properties");

        Map<String,String> customProperties = new HashMap<String, String>();
        customProperties.put("log_level.jmeter.control","INFO");
        customProperties.put("log_level.jmeter","DEBUG");
        
        Properties properties = new Properties();
        properties.load(new FileInputStream(testFile.getFile()));

        Properties modifiedProperties = PropertyFileMerger.mergeProperties(properties, customProperties);

        assertEquals("property was not overwritten","DEBUG",modifiedProperties.get("log_level.jmeter"));
        assertEquals("property was not added","INFO",modifiedProperties.get("log_level.jmeter.control"));
        assertEquals("property should not differ from file entry","DEBUG",modifiedProperties.get("log_level.jmeter.junit"));
    }

}
