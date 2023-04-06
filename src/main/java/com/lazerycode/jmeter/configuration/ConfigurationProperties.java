package com.lazerycode.jmeter.configuration;

import java.util.List;
import java.util.Properties;

public interface ConfigurationProperties {
    List<String> buildTypedPropertiesForContainerList(Properties properties);
}
