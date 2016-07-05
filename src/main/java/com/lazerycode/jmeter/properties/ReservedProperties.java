package com.lazerycode.jmeter.properties;

/**
 * An Enum holding a list of reserved properties.
 * Properties configured here may not be set by client configuration.
 *
 * @author Mark Collin
 */
public enum ReservedProperties {

	JAVA_CLASS_PATH("java.class.path", null),
	USER_DIR("user.dir", null),
	JMETERENGINE_REMOTE_SYSTEM_EXIT("jmeterengine.remote.system.exit", "false"),
	JMETERENGINE_STOPFAIL_SYSTEM_EXIT("jmeterengine.stopfail.system.exit", "false");

	private final String propertyKey;
	private final String requiredValue;

	ReservedProperties(String propertyKey, String requiredValue) {
		this.propertyKey = propertyKey;
		this.requiredValue = requiredValue;
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public String getRequiredValue() {
		return requiredValue;
	}
}
