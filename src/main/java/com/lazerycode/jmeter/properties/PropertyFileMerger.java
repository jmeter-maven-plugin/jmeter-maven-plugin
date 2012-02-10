package com.lazerycode.jmeter.properties;

import com.lazerycode.jmeter.JMeterMojo;

import java.util.Map;
import java.util.Properties;

/**
 * Handler that can merge Properties objects and Maps
 *
 * @author Arne Franken, Mark Collin
 */
public class PropertyFileMerger extends JMeterMojo {

    private Properties baseProperties;

    public PropertyFileMerger(Properties baseProperties) {
        this.baseProperties = baseProperties;
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
                this.baseProperties.setProperty(key, customProperties.get(key));
                warnUserOfPossibleErrors(key);
            }
        }
        this.baseProperties = stripReservedProperties(this.baseProperties);
        return this.baseProperties;
    }

    //==================================================================================================================

    /**
     * This will strip all reserved properties from a Properties object.
     * (Used to ensure that restricted properties haven't been set in custom properties files)
     *
     * @param propertyFile
     * @return
     */
    private Properties stripReservedProperties(Properties propertyFile) {
        for (ReservedProperties reservedProperty : ReservedProperties.values()) {
            if (propertyFile.containsKey(reservedProperty.getPropertyKey())) {
                propertyFile.remove(reservedProperty.getPropertyKey());
                getLog().warn("Unable to set '" + reservedProperty.getPropertyKey() + "', it is a reserved property in the jmeter-maven-plugin");
            }
        }
        return propertyFile;
    }

    /**
     * Print a warning out to the user to highlight potential typos in the properties they have set.
     *
     * @param value
     */
    private void warnUserOfPossibleErrors(String value) {
        for (String key : this.baseProperties.stringPropertyNames()) {
            if (!key.equals(value) && key.toLowerCase().equals(value.toLowerCase())) {
                getLog().warn("You have set a property called '" + value + "' which is very similar to '" + key + "'!");
            }
        }
    }
}