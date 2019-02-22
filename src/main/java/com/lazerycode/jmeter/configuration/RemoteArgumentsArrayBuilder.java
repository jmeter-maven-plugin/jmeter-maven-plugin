package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class RemoteArgumentsArrayBuilder {

    public static List<String> buildRemoteArgumentsArray(Map<ConfigurationFiles, PropertiesMapping> propertiesMap) {
        List<String> result = new ArrayList<>();
        for (Entry<ConfigurationFiles, PropertiesMapping> entry : propertiesMap.entrySet()) {
            Properties properties = entry.getValue().getPropertiesFile().getProperties();
            switch (entry.getKey()) {
                case SYSTEM_PROPERTIES:
                    result.addAll(buildTypedPropertiesForContainer(JMeterCommandLineArguments.SYSTEM_PROPERTY, properties));
                    break;
                case GLOBAL_PROPERTIES:
                    result.addAll(buildTypedPropertiesForContainer(JMeterCommandLineArguments.JMETER_GLOBAL_PROP, properties));
                    break;
                default:
                    break;
            }
        }

        return result;
    }

    private static List<String> buildTypedPropertiesForContainer(JMeterCommandLineArguments cmdLineArg, Properties props) {
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