package com.lazerycode.jmeter.properties;

import com.lazerycode.jmeter.UtilityFunctions;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class PropertyFileMergerTest {

    private final URL testFile = this.getClass().getResource("/jmeter.properties");
    private final URL testFileAdditional = this.getClass().getResource("/custom.properties");
    private final PrintStream originalOut = System.out;

    @Test
    public void validMergePropertiesTest() throws Exception {
        HashMap<String, String> customProperties = new HashMap<String, String>();
        customProperties.put("log_level.jmeter.control", "INFO");
        customProperties.put("log_level.jmeter", "DEBUG");

        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.testFile.toURI())));

        Properties modifiedProperties = new PropertyFileMerger().mergeProperties(customProperties, propertiesFile);

        assertEquals("property was not overwritten", "DEBUG", modifiedProperties.get("log_level.jmeter"));
        assertEquals("property was not added", "INFO", modifiedProperties.get("log_level.jmeter.control"));
        assertEquals("property should not differ from file entry", "DEBUG", modifiedProperties.get("log_level.jmeter.junit"));
    }

    @Test
    public void attemptToMergeReservedProperty() throws Exception {
        HashMap<String, String> customProperties = new HashMap<String, String>();
        customProperties.put("user.dir", "/home/foo");

        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.testFile.toURI())));

        ByteArrayOutputStream logCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(logCapture));

        Properties modifiedProperties = new PropertyFileMerger().mergeProperties(customProperties, propertiesFile);

        String loggedMessage = UtilityFunctions.stripCarriageReturns(logCapture.toString());
        System.setOut(this.originalOut);

        assertThat(modifiedProperties.containsKey("user.dir"),
                is(equalTo(false)));
        assertThat(modifiedProperties.size(),
                is(equalTo(2)));
        assertThat(loggedMessage,
                is(equalTo("[warn] Unable to set 'user.dir', it is a reserved property in the jmeter-maven-plugin")));
    }

    @Test
    public void showWarningWithSimilarProperty() throws Exception {
        HashMap<String, String> customProperties = new HashMap<String, String>();
        customProperties.put("log_level.Jmeter", "INFO");

        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.testFile.toURI())));

        ByteArrayOutputStream logCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(logCapture));

        Properties modifiedProperties = new PropertyFileMerger().mergeProperties(customProperties, propertiesFile);

        String loggedMessage = UtilityFunctions.stripCarriageReturns(logCapture.toString());
        System.setOut(this.originalOut);

        assertThat(modifiedProperties.size(),
                is(equalTo(3)));
        assertThat(loggedMessage,
                is(equalTo("[warn] You have set a property called 'log_level.Jmeter' which is very similar to 'log_level.jmeter'!")));
    }

    @Test
    public void mergeTwoPropertiesFiles() throws Exception {

        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.testFile.toURI())));

        Properties customProperties = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.testFileAdditional.toURI())));

        Properties modifiedProperties = new PropertyFileMerger().mergePropertiesFiles(propertiesFile, customProperties);

        assertThat(modifiedProperties.getProperty("log_level.jmeter"),
                is(equalTo("INFO")));
        assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"),
                is(equalTo("INFO")));
        assertThat(modifiedProperties.getProperty("log_level.jmeter.control"),
                is(equalTo("DEBUG")));
        assertThat(modifiedProperties.containsKey("log_level.jmeter.engine"),
                is(equalTo(false)));
    }
    
}
