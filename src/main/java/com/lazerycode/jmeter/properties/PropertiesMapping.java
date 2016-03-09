package com.lazerycode.jmeter.properties;

import java.util.Map;

public class PropertiesMapping {
	private Map<String, String> additionalProperties;
	private PropertiesFile propertiesFile;

	public PropertiesMapping(Map<String, String> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	public Map<String, String> getAdditionalProperties() {
		return additionalProperties;
	}

	public PropertiesFile getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(PropertiesFile propertiesFile) {
		this.propertiesFile = propertiesFile;
	}
}
