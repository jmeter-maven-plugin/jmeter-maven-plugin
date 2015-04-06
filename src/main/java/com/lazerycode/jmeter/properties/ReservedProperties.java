package com.lazerycode.jmeter.properties;

/**
 * An Enum holding a list of reserved properties.
 * Properties configured here may not be set by client configuration.
 *
 * @author Mark Collin
 */
public enum ReservedProperties {

	JAVA_CLASS_PATH("java.class.path"),
	USER_DIR("user.dir");

	private final String propertyKey;

	ReservedProperties(String propertyKey) {
		this.propertyKey = propertyKey;
	}

	public String getPropertyKey() {
		return propertyKey;
	}
}
