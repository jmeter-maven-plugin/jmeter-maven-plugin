package com.lazerycode.jmeter.configuration;

import java.util.List;
import java.util.Properties;

public class SystemProperties implements ConfigurationProperties {
    @Override
    public List<String> buildTypedPropertiesForContainerList(Properties properties) {
        return RemoteArgumentsArrayBuilder.buildTypedPropertiesForContainer(JMeterCommandLineArguments.SYSTEM_PROPERTY, properties);
    }
}
