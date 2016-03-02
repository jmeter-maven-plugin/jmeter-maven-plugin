package com.lazerycode.jmeter.properties;

import com.lazerycode.jmeter.mojo.RunJMeterMojo;

import java.util.Map;
import java.util.Properties;

/**
 * Handler that can merge Properties objects and Maps
 *
 * @author Arne Franken, Mark Collin
 */
class PropertyFileMerger extends RunJMeterMojo {

	/**
	 * Merge two properties files together.
	 * The additions will overwrite any existing properties in source if required.
	 *
	 * @param source    Properties
	 * @param additions Properties
	 * @return Properties
	 */
	public Properties mergePropertiesFiles(Properties source, Properties additions) {
		if (null == source && null == additions) return new Properties();
		if (null == source) return stripReservedProperties(additions);
		if (null == additions) return stripReservedProperties(source);
		source.putAll(additions);
		return stripReservedProperties(source);
	}

	/**
	 * Merge given Map into given Properties object
	 *
	 * @param customProperties Map to merge into the Properties object
	 * @return merged Properties object
	 */
	public Properties mergeProperties(Map<String, String> customProperties, Properties baseProperties) {
		if (customProperties != null && !customProperties.isEmpty()) {
			for (String key : customProperties.keySet()) {
				baseProperties.setProperty(key, customProperties.get(key));
				warnUserOfPossibleErrors(key, baseProperties);
			}
		}
		return stripReservedProperties(baseProperties);
	}

	/**
	 * This will strip all reserved properties from a Properties object.
	 * (Used to ensure that restricted properties haven't been set in custom properties files)
	 *
	 * @param propertyFile Properties
	 * @return Properties
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
	 * @param value          Property Value
	 * @param baseProperties Properties
	 */
	private void warnUserOfPossibleErrors(String value, Properties baseProperties) {
		for (String key : baseProperties.stringPropertyNames()) {
			if (!key.equals(value) && key.toLowerCase().equals(value.toLowerCase())) {
				getLog().warn("You have set a property called '" + value + "' which is very similar to '" + key + "'!");
			}
		}
	}
}