package com.lazerycode.jmeter.properties;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class PropertyContainerTest {

    @Test
    public void checkGetProperty() throws Exception {
        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.getClass().getResource("/testSource.properties").toURI())));
        PropertyContainer propertyContainer = new PropertyContainer();
        propertyContainer.setFinalPropertyObject(propertiesFile);

        assertThat(propertyContainer.getProperty("log_level.jmeter"),
                is(equalTo("INFO")));
    }

    @Test
    public void propertyReturnCustomWhenBothExist() throws Exception {
        Properties propertiesDefault = new Properties();
        Properties propertiesCustom = new Properties();
        propertiesDefault.load(new FileInputStream(new File(this.getClass().getResource("/testSource.properties").toURI())));
        propertiesCustom.load(new FileInputStream(new File(this.getClass().getResource("/testAdditions.properties").toURI())));
        PropertyContainer propertyContainer = new PropertyContainer();
        propertyContainer.setDefaultPropertyObject(propertiesDefault);
        propertyContainer.setCustomPropertyObject(propertiesCustom);
        Properties mergedProperties = propertyContainer.getBasePropertiesObject();

        assertThat(mergedProperties.containsKey("log_level.jmeter"),
                is(equalTo(false)));
        assertThat(mergedProperties.getProperty("log_level.jmeter.junit"),
                is(equalTo("INFO")));
        assertThat(mergedProperties.getProperty("log_level.jmeter.control"),
                is(equalTo("DEBUG")));
    }

    @Test
    public void propertyReturnDefaultTest() throws Exception {
        Properties propertiesFile = new Properties();
        propertiesFile.load(new FileInputStream(new File(this.getClass().getResource("/testSource.properties").toURI())));
        PropertyContainer propertyContainer = new PropertyContainer();
        propertyContainer.setDefaultPropertyObject(propertiesFile);
        Properties mergedProperties = propertyContainer.getBasePropertiesObject();

        assertThat(mergedProperties.getProperty("log_level.jmeter"),
                is(equalTo("INFO")));
        assertThat(mergedProperties.getProperty("log_level.jmeter.junit"),
                is(equalTo("DEBUG")));
        assertThat(mergedProperties.containsKey("log_level.jmeter.engine"),
                is(equalTo(false)));
    }

    @Test
    public void propertyReturnCustomTest() throws Exception {
        Properties propertiesCustom = new Properties();
        propertiesCustom.load(new FileInputStream(new File(this.getClass().getResource("/testAdditions.properties").toURI())));
        PropertyContainer propertyContainer = new PropertyContainer();
        propertyContainer.setCustomPropertyObject(propertiesCustom);
        Properties mergedProperties = propertyContainer.getBasePropertiesObject();

        assertThat(mergedProperties.containsKey("log_level.jmeter"),
                is(equalTo(false)));
        assertThat(mergedProperties.getProperty("log_level.jmeter.junit"),
                is(equalTo("INFO")));
        assertThat(mergedProperties.getProperty("log_level.jmeter.control"),
                is(equalTo("DEBUG")));
        assertThat(mergedProperties.containsKey("log_level.jmeter.engine"),
                is(equalTo(false)));
    }

    @Test
    public void propertyEmptyPropertySetWhenNothingSet() throws Exception {
        PropertyContainer propertyContainer = new PropertyContainer();
        Properties mergedProperties = propertyContainer.getBasePropertiesObject();

        assertThat(mergedProperties.containsKey("log_level.jmeter"),
                is(equalTo(false)));
        assertThat(mergedProperties.size(),
                is(equalTo(0)));
    }
}
