package com.lazerycode.jmeter.configuration;

import java.util.List;
import java.util.Properties;

public class GlobalProperties implements ConfigurationProperties {
    @Override
    public List<String> buildTypedPropertiesForContainerList(Properties properties) {
        return RemoteArgumentsArrayBuilder.buildTypedPropertiesForContainer(JMeterCommandLineArguments.JMETER_GLOBAL_PROP, properties);
    }
}
