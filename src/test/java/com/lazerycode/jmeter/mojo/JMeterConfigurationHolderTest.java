package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class JMeterConfigurationHolderTest {
    private final File WORKING_DIRECTORY = new File(this.getClass().getResource("/").getFile());
    private EnumMap<ConfigurationFiles, PropertiesMapping> propertiesMap = new EnumMap<>(ConfigurationFiles.class);
    private HashMap<String, String> properties = new HashMap<>();

    @Before
    public void testSetup() {
        properties.put("foo", "bar");
        properties.put("oof", "rab");
        propertiesMap.put(ConfigurationFiles.JMETER_PROPERTIES, new PropertiesMapping(properties));
    }

    @Test
    public void gettersAndSettersWorkAsExpected() {
        JMeterConfigurationHolder jMeterConfigurationHolder = JMeterConfigurationHolder.getInstance();
        jMeterConfigurationHolder.setWorkingDirectory(WORKING_DIRECTORY);
        jMeterConfigurationHolder.setPropertiesMap(propertiesMap);

        assertThat(jMeterConfigurationHolder.getWorkingDirectory()).isEqualTo(WORKING_DIRECTORY);
        assertThat(jMeterConfigurationHolder.getPropertiesMap()).isEqualTo(propertiesMap);
    }

    @Test
    public void resettingConfigurationAllowsYouToModifyJMeterConfigurationHolder() {
        JMeterConfigurationHolder jMeterConfigurationHolder = JMeterConfigurationHolder.getInstance();

        assertThat(jMeterConfigurationHolder.getWorkingDirectory()).isNull();
        assertThat(jMeterConfigurationHolder.getPropertiesMap()).isNull();

        jMeterConfigurationHolder.setWorkingDirectory(WORKING_DIRECTORY);
        jMeterConfigurationHolder.setPropertiesMap(propertiesMap);

        assertThat(jMeterConfigurationHolder.getWorkingDirectory()).isEqualTo(WORKING_DIRECTORY);
        assertThat(jMeterConfigurationHolder.getPropertiesMap()).isEqualTo(propertiesMap);
    }
}
