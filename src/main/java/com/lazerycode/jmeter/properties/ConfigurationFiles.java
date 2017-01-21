package com.lazerycode.jmeter.properties;

/**
 * An Enum holding a list of JMeter properties files.
 * If it is set to true JMeter expects it to be in the bin dir.
 *
 * @author Mark Collin
 */
public enum ConfigurationFiles {

	JMETER_PROPERTIES("jmeter.properties", true),
	SAVE_SERVICE_PROPERTIES("saveservice.properties", true),
	UPGRADE_PROPERTIES("upgrade.properties", true),
	SYSTEM_PROPERTIES("system.properties", true),
	USER_PROPERTIES("user.properties", true),
	REPORT_GENERATOR_PROPERTIES("reportgenerator.properties", true),
	GLOBAL_PROPERTIES("global.properties", false);

	private final String propertiesFilename;
	private final boolean isRequired;

	ConfigurationFiles(String propertiesFilename, boolean isRequired) {
		this.propertiesFilename = propertiesFilename;
		this.isRequired = isRequired;
	}

	public String getFilename() {
		return propertiesFilename;
	}

	public boolean createFileIfItDoesNotExist() {
		return isRequired;
	}

}