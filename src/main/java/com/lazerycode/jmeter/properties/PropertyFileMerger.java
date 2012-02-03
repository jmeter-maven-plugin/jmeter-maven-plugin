package com.lazerycode.jmeter.properties;

import com.lazerycode.jmeter.JMeterMojo;

import java.util.Map;
import java.util.Properties;

public class PropertyFileMerger extends JMeterMojo {

    private Properties baseProperties;

    public PropertyFileMerger(Properties baseProperties) {
        this.baseProperties = stripReservedProperties(baseProperties);
    }

    /**
     * Merge given Map into given Properties object
     *
     * @param customProperties Map to merge into the Properties object
     * @return merged Properties object
     */
    public Properties mergeProperties(Map<String, String> customProperties) {
        if (customProperties != null && !customProperties.isEmpty()) {
            for (String key : customProperties.keySet()) {
                if (!isReservedProperty(key)) {
                    this.baseProperties.setProperty(key, customProperties.get(key));
                } else {
                    getLog().warn("Unable to set '" + key + "', it is a reserved property in the jmeter-maven-plugin");
                }
                warnUserOfPossibleErrors(key);
            }
        }
        return this.baseProperties;
    }

    //==================================================================================================================

    private Properties stripReservedProperties(Properties propertyFile) {
        for (ReservedProperties reservedProperty : ReservedProperties.values()) {
            if (propertyFile.containsKey(reservedProperty.getPropertyKey())) {
                propertyFile.remove(reservedProperty.getPropertyKey());
                getLog().warn("Unable to set '" + reservedProperty.getPropertyKey() + "', it is a reserved property in the jmeter-maven-plugin");
            }
        }
        return propertyFile;
    }

    private boolean isReservedProperty(String value) {
        for (ReservedProperties reservedProperty : ReservedProperties.values()) {
            if (reservedProperty.getPropertyKey().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void warnUserOfPossibleErrors(String value) {
        for (String key : this.baseProperties.stringPropertyNames()) {
            if (!key.equals(value) && key.toLowerCase().equals(value.toLowerCase())) {
                getLog().warn("You have set a property called '" + value + "' which is very similar to '" + key + "'!");
            }
        }
    }
}

