package com.lazerycode.jmeter.properties;

import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;
import java.util.Properties;

public class PropertyContainer {

    private Map<String, String> customPropertyMap = null;
    private Properties customPropertyObject = null;
    private Properties defaultPropertyObject = new Properties();
    private Properties finalPropertyObject = new Properties();

    public PropertyContainer() {

    }

    public void setCustomPropertyMap(Map<String, String> value) {
        this.customPropertyMap = value;
    }

    public Map<String, String> getCustomPropertyMap() {
        return this.customPropertyMap;
    }

    public void setCustomPropertyObject(Properties value) {
        this.customPropertyObject = value;
    }

    public Properties getCustomPropertyObject() {
        return this.customPropertyObject;
    }

    public void setDefaultPropertyObject(Properties value) {
        this.defaultPropertyObject = value;
    }

    public Properties getDefaultPropertyObject() {
        return this.defaultPropertyObject;
    }

    public void setFinalPropertyObject(Properties value) {
        this.finalPropertyObject = value;
    }

    public Properties getFinalPropertyObject() {
        return this.finalPropertyObject;
    }

    /**
     * This will return the custom properties object if it is set.
     * If it is not set it will return the default properties object (this may be empty)
     *
     * @return
     * @throws MojoExecutionException
     */
    public Properties getBasePropertiesObject() throws MojoExecutionException {
        if (this.customPropertyObject == null) {
            return this.getDefaultPropertyObject();
        } else {
            return this.getCustomPropertyObject();
        }
    }

    public String getProperty(String value) {
        return this.finalPropertyObject.getProperty(value);
    }
}
