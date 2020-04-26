package com.lazerycode.jmeter.properties;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class PropertiesMapping {
    protected Map<String, String> additionalProperties;
    private PropertiesFile propertiesFile;

    @JsonCreator
    public PropertiesMapping(@JsonProperty("additionalProperties") Map<String, String> additionalProperties) {
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
