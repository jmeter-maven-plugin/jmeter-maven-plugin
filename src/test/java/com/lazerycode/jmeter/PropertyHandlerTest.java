package com.lazerycode.jmeter;

import com.lazerycode.jmeter.properties.PropertyHandler;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class PropertyHandlerTest {

    URL testFile = this.getClass().getResource("/jmeter.properties");

    //Doesn't always work, e.g. can't map a HashMap to a Map
    private Object invokePrivateMethod(String methodName, Object[] parameters) throws Exception {
        ArrayList<Class> classArray = new ArrayList<Class>();
        for (Object item : parameters) {
            classArray.add(item.getClass());
        }
        return invokePrivateMethod(methodName, parameters, classArray.toArray(new Class[classArray.size()]));
    }

    private Object invokePrivateMethod(String methodName, Object[] parameters, Class[] parameterClasses) throws Exception {
        Method method = PropertyHandler.class.getDeclaredMethod(methodName, parameterClasses);
        method.setAccessible(true);
        return method.invoke(method, parameters);
    }

    @Test
    public void testMergeProperties() throws Exception {
        //Custom properties.
        HashMap<String, String> customProperties = new HashMap<String, String>();
        customProperties.put("log_level.jmeter.control", "INFO");
        customProperties.put("log_level.jmeter", "DEBUG");
        //Properties loaded from file.
        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.testFile.toURI())));
        //Perform merge
        Object[] parameters = {propertiesFile, customProperties};
        Class[] parameterClasses = {Properties.class, Map.class};
        Properties modifiedProperties = (Properties) invokePrivateMethod("mergeProperties", parameters, parameterClasses);

        assertEquals("property was not overwritten", "DEBUG", modifiedProperties.get("log_level.jmeter"));
        assertEquals("property was not added", "INFO", modifiedProperties.get("log_level.jmeter.control"));
        assertEquals("property should not differ from file entry", "DEBUG", modifiedProperties.get("log_level.jmeter.junit"));
    }

}
