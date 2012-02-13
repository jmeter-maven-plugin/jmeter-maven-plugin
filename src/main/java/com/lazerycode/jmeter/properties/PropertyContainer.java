package com.lazerycode.jmeter.properties;

import java.util.Map;
import java.util.Properties;

public class PropertyContainer {

    private Map<String, String> customPropertyMap = null;
    private Properties customPropertyObject = null;
    private Properties defaultPropertyObject = null;
    private Properties finalPropertyObject = null;

    public PropertyContainer() {

    }

    public PropertyContainer(Map<String, String> value) {
        this.setCustomPropertyMap(value);
    }

    public PropertyContainer(Properties value) {
        this.setCustomPropertyObject(value);
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

    public Properties getBasePropertiesObject() {
        if (this.customPropertyObject == null) {
            return this.defaultPropertyObject;
        } else {
            return this.customPropertyObject;
        }
    }

    public Properties getMergedPropertiesObject() {
        return new PropertyFileMerger().mergePropertiesFiles(this.defaultPropertyObject, this.customPropertyObject);
    }

    public String getProperty(String value) {
        return this.finalPropertyObject.getProperty(value);
    }
}
