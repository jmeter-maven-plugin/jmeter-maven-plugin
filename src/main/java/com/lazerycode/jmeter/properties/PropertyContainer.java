package com.lazerycode.jmeter.properties;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

//TODO remove this, PropertiesFiles will replace it

public class PropertyContainer {

    private Map<String, String> customPropertyMap = null;
    private Properties customPropertyObject = null;
    private Properties defaultPropertyObject = new Properties();
    private Properties finalPropertyObject = new Properties();

    public PropertyContainer() {

    }

    public void setCustomPropertyMap(Map<String, String> propertyMap) {
        this.customPropertyMap = removesEntriesWithNullValues(propertyMap);
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

	public Map<String,String> removesEntriesWithNullValues(Map<String, String> propertiesMap) {
		Iterator it = propertiesMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry propertyDefinition = (Map.Entry)it.next();
			if(null == propertyDefinition.getValue() || propertyDefinition.getValue().equals("")){
				it.remove();
			}
		}
		return propertiesMap;
	}

    /**
     * This will return the custom properties object if it is set.
     * If it is not set it will return the default properties object (this may be empty)
     *
     * @return Properties
     */
    public Properties getBasePropertiesObject() {
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
