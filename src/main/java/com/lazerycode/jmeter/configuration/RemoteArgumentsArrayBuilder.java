package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;

import java.util.*;
import java.util.Map.Entry;

public class RemoteArgumentsArrayBuilder {

	public List<String> buildRemoteArgumentsArray(Map<ConfigurationFiles, PropertiesMapping> propertiesMap) {
		if (propertiesMap == null) {
			return Collections.emptyList();
		}

		List<String> result = new ArrayList<>();
		for (Entry<ConfigurationFiles, PropertiesMapping> entry : propertiesMap.entrySet()) {
			Properties properties = entry.getValue().getPropertiesFile().getProperties();
			switch (entry.getKey()) {
				case SYSTEM_PROPERTIES: {
					result.addAll(buildTypedPropertiesForContainer(JMeterCommandLineArguments.SYSTEM_PROPERTY, properties));
					break;
				}
				case GLOBAL_PROPERTIES: {
					result.addAll(buildTypedPropertiesForContainer(JMeterCommandLineArguments.JMETER_GLOBAL_PROP, properties));
					break;
				}
				default:
					break;
			}
		}
		return result;
	}

	private List<String> buildTypedPropertiesForContainer(JMeterCommandLineArguments cmdLineArg, Properties props) {
		List<String> result = new ArrayList<>();
		for (Entry<Object, Object> e : props.entrySet()) {
			if (cmdLineArg == JMeterCommandLineArguments.SYSTEM_PROPERTY) {
				result.add(cmdLineArg.getCommandLineArgument() + e.getKey());
				result.add(e.getValue().toString());
			} else {
				result.add(cmdLineArg.getCommandLineArgument() + e.getKey() + "=" + e.getValue());
			}
		}
		return result;
	}
}