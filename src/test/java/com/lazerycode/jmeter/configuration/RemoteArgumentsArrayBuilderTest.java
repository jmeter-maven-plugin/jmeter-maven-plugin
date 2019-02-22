package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesFile;
import com.lazerycode.jmeter.properties.PropertiesMapping;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class RemoteArgumentsArrayBuilderTest {

    private Map<ConfigurationFiles, PropertiesMapping> inputMap = new HashMap<>();
    private PropertiesMapping propertiesMapping;

    @Before
    public void setup() {
        Map<String, String> additionalProperties = new HashMap<>();
        additionalProperties.put("hello", "world");
        propertiesMapping = new PropertiesMapping(additionalProperties);
        PropertiesFile propertiesFile = new PropertiesFile();
        propertiesFile.addAndOverwriteProperties(additionalProperties);
        propertiesMapping.setPropertiesFile(propertiesFile);
    }

    @Test
    public void shouldBuildCommandLineArgumentsForSystemProperties() {
        inputMap.put(ConfigurationFiles.SYSTEM_PROPERTIES, propertiesMapping);
        List<String> result = RemoteArgumentsArrayBuilder.buildRemoteArgumentsArray(inputMap);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo("-Dhello");
        assertThat(result.get(1)).isEqualTo("world");
    }

    @Test
    public void shouldBuildCommandLineArgumentsGlobalProperties() {
        inputMap.put(ConfigurationFiles.GLOBAL_PROPERTIES, propertiesMapping);
        List<String> result = RemoteArgumentsArrayBuilder.buildRemoteArgumentsArray(inputMap);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo("-Ghello=world");
    }

    @Test
    public void shouldIgnoreOtherTypesOfProperties() {
        inputMap.put(ConfigurationFiles.JMETER_PROPERTIES, propertiesMapping);
        List<String> result = RemoteArgumentsArrayBuilder.buildRemoteArgumentsArray(inputMap);

        assertThat(result.isEmpty()).isTrue();
    }
}
