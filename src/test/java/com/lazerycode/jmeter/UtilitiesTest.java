package com.lazerycode.jmeter;

import junit.framework.TestCase;

import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 * Tests {@link Utilities} class
 */
public class UtilitiesTest extends TestCase {
    public void testHumanReadableCommandLineOutput() throws Exception {
        String[] testArray = new String[]{"a","b","c","d"};
        
        String returnValue = Utilities.humanReadableCommandLineOutput(testArray);
        
        String expected = "a b c d";
        
        assertEquals("string does not match array input",expected,returnValue);
    }

    public void testIsNotSetMap() throws Exception {
        Map testMap = null;
        assertTrue("null value returns false", Utilities.isNotSet(testMap));
        
        Map testMap2 = Collections.emptyMap();
        assertTrue("empty value returns false", Utilities.isNotSet(testMap2));
    }

    public void testIsNotSetString() throws Exception {
        String testString = null;
        assertTrue("null value returns false",Utilities.isNotSet(testString));

        String testString2 = "";
        assertTrue("empty value returns false",Utilities.isNotSet(testString2));
    }

    public void testIsNotSetFile() throws Exception {
        File testFile = null;
        assertTrue("null value returns false",Utilities.isNotSet(testFile));
    }

    public void testArgumentsMapToString() throws Exception {
        Map<String,String> testMap = Collections.singletonMap("key","value");

        String returnValue = Utilities.argumentsMapToString(testMap,JMeterCommandLineArguments.PROXY_PASSWORD);
        
        String expected = "-a key=value";
        
        assertEquals("arguments not converted correctly",expected,returnValue);
    }
}
