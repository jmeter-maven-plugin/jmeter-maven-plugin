package com.lazerycode.jmeter;

import com.lazerycode.jmeter.propertiesHandler.PropertyHandler;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Test utility class {@link PropertyHandler}
 */
public class PropertyFileMergerTest extends TestCase {

    URL testFile = this.getClass().getResource("/jmeter.properties");

    @Ignore
    @Test
    public void testMergeProperties() throws Exception {



        Map<String,String> customProperties = new HashMap<String, String>();
        customProperties.put("log_level.jmeter.control","INFO");
        customProperties.put("log_level.jmeter","DEBUG");
        
        Properties properties = new Properties();
        properties.load(new FileInputStream(this.testFile.getFile()));

//        PropertyHandler.mergePropertiesFile(getSourcePropertyFile(propertyFile), this.propertyOutputDirectory, propertyFile.getPropertiesFileName(), masterPropertiesMap.get(propertyFile));
//        Properties modifiedProperties = PropertyFileMerger.mergeProperties(properties, customProperties);

//        assertEquals("property was not overwritten","DEBUG",modifiedProperties.get("log_level.jmeter"));
//        assertEquals("property was not added","INFO",modifiedProperties.get("log_level.jmeter.control"));
//        assertEquals("property should not differ from file entry","DEBUG",modifiedProperties.get("log_level.jmeter.junit"));
    }

}
