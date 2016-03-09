package com.lazerycode.jmeter.properties;

import java.util.Map;

public class PropertiesMapping {
	private Map<String, String> additionalProperties;
	private PropertiesFiles propertiesFile;

	public PropertiesMapping(Map<String, String> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	public Map<String, String> getAdditionalProperties() {
		return additionalProperties;
	}

	public PropertiesFiles getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(PropertiesFiles propertiesFile) {
		this.propertiesFile = propertiesFile;
	}
}
