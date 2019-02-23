package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.mojo.JMeterConfigurationHolder;
import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class JMeterProcessBuilderTest {
    private static final String RUNTIME_JAR_NAME = "fred";
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
        jMeterConfigurationHolder.setRuntimeJarName(RUNTIME_JAR_NAME);
        jMeterConfigurationHolder.setWorkingDirectory(WORKING_DIRECTORY);
        jMeterConfigurationHolder.setPropertiesMap(propertiesMap);

        assertThat(jMeterConfigurationHolder.getRuntimeJarName()).isEqualTo(RUNTIME_JAR_NAME);
        assertThat(jMeterConfigurationHolder.getWorkingDirectory()).isEqualTo(WORKING_DIRECTORY);
        assertThat(jMeterConfigurationHolder.getPropertiesMap()).isEqualTo(propertiesMap);
    }

    @Test(expected = IllegalStateException.class)
    public void tryingToModifyRuntimeJarNameWhileFrozenThrowsIllegalStateException() {
        JMeterConfigurationHolder jMeterConfigurationHolder = JMeterConfigurationHolder.getInstance();
        jMeterConfigurationHolder.freezeConfiguration();
        jMeterConfigurationHolder.setRuntimeJarName(RUNTIME_JAR_NAME);
    }

    @Test(expected = IllegalStateException.class)
    public void tryingToModifyWorkingDirectoryWhileFrozenThrowsIllegalStateException() {
        JMeterConfigurationHolder jMeterConfigurationHolder = JMeterConfigurationHolder.getInstance();
        jMeterConfigurationHolder.freezeConfiguration();
        jMeterConfigurationHolder.setWorkingDirectory(WORKING_DIRECTORY);
    }

    @Test(expected = IllegalStateException.class)
    public void tryingToModifyPropertiesMapWhileFrozenThrowsIllegalStateException() {
        JMeterConfigurationHolder jMeterConfigurationHolder = JMeterConfigurationHolder.getInstance();
        jMeterConfigurationHolder.freezeConfiguration();
        jMeterConfigurationHolder.setPropertiesMap(propertiesMap);
    }

    @Test
    public void resettingConfigurationAllowsYouToModifyJMeterConfigurationHolder() {
        JMeterConfigurationHolder jMeterConfigurationHolder = JMeterConfigurationHolder.getInstance();
        jMeterConfigurationHolder.freezeConfiguration();

        assertThat(jMeterConfigurationHolder.getRuntimeJarName()).isNull();
        assertThat(jMeterConfigurationHolder.getWorkingDirectory()).isNull();
        assertThat(jMeterConfigurationHolder.getPropertiesMap()).isNull();

        jMeterConfigurationHolder.resetConfiguration();
        jMeterConfigurationHolder.setRuntimeJarName(RUNTIME_JAR_NAME);
        jMeterConfigurationHolder.setWorkingDirectory(WORKING_DIRECTORY);
        jMeterConfigurationHolder.setPropertiesMap(propertiesMap);

        assertThat(jMeterConfigurationHolder.getRuntimeJarName()).isEqualTo(RUNTIME_JAR_NAME);
        assertThat(jMeterConfigurationHolder.getWorkingDirectory()).isEqualTo(WORKING_DIRECTORY);
        assertThat(jMeterConfigurationHolder.getPropertiesMap()).isEqualTo(propertiesMap);
    }
}
