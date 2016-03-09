package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesFile;
import com.lazerycode.jmeter.properties.PropertiesMapping;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class RemoteArgumentsArrayBuilderTest {

	private Map<ConfigurationFiles, PropertiesMapping> inputMap = new HashMap<>();
	private RemoteArgumentsArrayBuilder arrayBuilder = new RemoteArgumentsArrayBuilder();
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
	public void shouldReturnEmptyListWhenNoPropsPassed() {
		List<String> result = arrayBuilder.buildRemoteArgumentsArray(null);
		assertTrue(result.isEmpty());
	}

	@Test
	public void shouldBuildCommandLineArgumentsForSystemProperties() {

		inputMap.put(ConfigurationFiles.SYSTEM_PROPERTIES, propertiesMapping);
		List<String> result = arrayBuilder.buildRemoteArgumentsArray(inputMap);
		assertEquals(2, result.size());
		assertEquals("-Dhello", result.get(0));
		assertEquals("world", result.get(1));
	}

	@Test
	public void shouldBuildCommandLineArgumentsGlobalProperties() {

		inputMap.put(ConfigurationFiles.GLOBAL_PROPERTIES, propertiesMapping);
		List<String> result = arrayBuilder.buildRemoteArgumentsArray(inputMap);
		assertEquals(1, result.size());
		assertEquals("-Ghello=world", result.get(0));
	}

	@Test
	public void shoulIgnoreOtherTypesOfProperties() {

		inputMap.put(ConfigurationFiles.JMETER_PROPERTIES, propertiesMapping);
		List<String> result = arrayBuilder.buildRemoteArgumentsArray(inputMap);
		assertTrue(result.isEmpty());
	}
}
