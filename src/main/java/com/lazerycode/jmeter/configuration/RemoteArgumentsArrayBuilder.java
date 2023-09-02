package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;

import java.util.*;
import java.util.Map.Entry;

public class RemoteArgumentsArrayBuilder {

    /**
     * Make constructor private as this is a non-instantiable helper classes
     */
    RemoteArgumentsArrayBuilder() throws InstantiationError {
        throw new InstantiationError("This class is non-instantiable.");
    }

    public static List<String> buildRemoteArgumentsArray(Map<ConfigurationFiles, PropertiesMapping> propertiesMap) {
        List<String> result = new ArrayList<>();
        for (Entry<ConfigurationFiles, PropertiesMapping> entry : propertiesMap.entrySet()) {
            Properties properties = entry.getValue().getPropertiesFile().getProperties();
            ConfigurationProperties configurationProperties = CONFIGURATION_PROPERTIES_MAP.get(entry.getKey());
            if(configurationProperties != null) {
                result.addAll(configurationProperties.buildTypedPropertiesForContainerList(properties));
            }
        }

        return result;
    }

    private static final Map<ConfigurationFiles, ConfigurationProperties> CONFIGURATION_PROPERTIES_MAP = new HashMap<>();
    static {
        CONFIGURATION_PROPERTIES_MAP.put(ConfigurationFiles.SYSTEM_PROPERTIES, new SystemProperties());
        CONFIGURATION_PROPERTIES_MAP.put(ConfigurationFiles.GLOBAL_PROPERTIES, new GlobalProperties());
    }

    public static List<String> buildTypedPropertiesForContainer(JMeterCommandLineArguments cmdLineArg, Properties props) {
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