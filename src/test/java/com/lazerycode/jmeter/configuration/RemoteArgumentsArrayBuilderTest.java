package com.lazerycode.jmeter.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.lazerycode.jmeter.properties.JMeterPropertiesFiles;
import com.lazerycode.jmeter.properties.PropertyContainer;


public class RemoteArgumentsArrayBuilderTest {

	private Map<JMeterPropertiesFiles, PropertyContainer> inputMap = new HashMap<JMeterPropertiesFiles, PropertyContainer>(); 
	private RemoteArgumentsArrayBuilder arrayBuilder = new RemoteArgumentsArrayBuilder() ; 
	private PropertyContainer container = new PropertyContainer(); 
	
	@Before
	public void setup() { 
		Properties props = new Properties(); 
		props.setProperty("hello","world"); 
		container.setFinalPropertyObject(props); 
	}
	
	@Test
	public void shouldReturnEmptyListWhenNoPropsPassed() {
		List<String> result = arrayBuilder.buildRemoteArgumentsArray(null); 
		assertTrue(result.isEmpty()); 
	}

	@Test
	public void shouldBuildCommandLineArgumentsForSystemProperties() {	

		inputMap.put(JMeterPropertiesFiles.SYSTEM_PROPERTIES, container);	
		List<String> result = arrayBuilder.buildRemoteArgumentsArray(inputMap); 
		assertEquals(2,result.size());
		assertEquals("-Dhello",result.get(0)); 
		assertEquals("world",result.get(1));
	}
	
	@Test
	public void shouldBuildCommandLineArgumentsGlobalProperties() {

		inputMap.put(JMeterPropertiesFiles.GLOBAL_PROPERTIES, container);	
		List<String> result = arrayBuilder.buildRemoteArgumentsArray(inputMap); 
		assertEquals(1,result.size());
		assertEquals("-Ghello=world",result.get(0)); 
	}
	
	@Test
	public void shoulIgnoreOtherTypesOfProperties() {

		inputMap.put(JMeterPropertiesFiles.JMETER_PROPERTIES, container);	
		List<String> result = arrayBuilder.buildRemoteArgumentsArray(inputMap); 
		assertTrue(result.isEmpty()); 
	}
}
