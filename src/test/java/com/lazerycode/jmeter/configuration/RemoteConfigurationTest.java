package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;
import org.junit.Test;

import java.util.EnumMap;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoteConfigurationTest {

    @Test
    public void defaultSettingsAreAsExpected() {
        RemoteConfiguration remoteConfiguration = new RemoteConfiguration();

        assertThat(remoteConfiguration.isStopServersAfterTests()).isFalse();
        assertThat(remoteConfiguration.isStartServersBeforeTests()).isFalse();
        assertThat(remoteConfiguration.isStartAndStopServersForEachTest()).isFalse();
        assertThat(remoteConfiguration.getServerList()).isEmpty();
        assertThat(remoteConfiguration.getPropertiesMap().size()).isEqualTo(0);
    }

    @Test
    public void propertiesMapIsReplacedWhenSet() {
        EnumMap<ConfigurationFiles, PropertiesMapping> firstEnumMap = new EnumMap<>(ConfigurationFiles.class);
        EnumMap<ConfigurationFiles, PropertiesMapping> secondEnumMap = new EnumMap<>(ConfigurationFiles.class);
        HashMap<String, String> firstEnumProperties = new HashMap<>();
        firstEnumProperties.put("foo", "bar");
        firstEnumProperties.put("oof", "rab");
        HashMap<String, String> secondEnumProperties = new HashMap<>();
        secondEnumProperties.put("ray", "far");
        firstEnumMap.put(ConfigurationFiles.JMETER_PROPERTIES, new PropertiesMapping(firstEnumProperties));
        secondEnumMap.put(ConfigurationFiles.SYSTEM_PROPERTIES, new PropertiesMapping(secondEnumProperties));
        RemoteConfiguration remoteConfiguration = new RemoteConfiguration();

        assertThat(remoteConfiguration.getPropertiesMap().size()).isEqualTo(0);

        remoteConfiguration.setPropertiesMap(firstEnumMap);

        assertThat(remoteConfiguration.getPropertiesMap().size()).isEqualTo(1);
        assertThat(remoteConfiguration.getPropertiesMap().containsKey(ConfigurationFiles.JMETER_PROPERTIES)).isTrue();
        assertThat(remoteConfiguration.getPropertiesMap().get(ConfigurationFiles.JMETER_PROPERTIES).getAdditionalProperties().size()).isEqualTo(2);
        assertThat(remoteConfiguration.getPropertiesMap().get(ConfigurationFiles.JMETER_PROPERTIES).getAdditionalProperties().get("foo")).isEqualTo("bar");
        assertThat(remoteConfiguration.getPropertiesMap().get(ConfigurationFiles.JMETER_PROPERTIES).getAdditionalProperties().get("oof")).isEqualTo("rab");

        remoteConfiguration.setPropertiesMap(secondEnumMap);

        assertThat(remoteConfiguration.getPropertiesMap().size()).isEqualTo(1);
        assertThat(remoteConfiguration.getPropertiesMap().containsKey(ConfigurationFiles.SYSTEM_PROPERTIES)).isTrue();
        assertThat(remoteConfiguration.getPropertiesMap().get(ConfigurationFiles.SYSTEM_PROPERTIES).getAdditionalProperties().size()).isEqualTo(1);
        assertThat(remoteConfiguration.getPropertiesMap().get(ConfigurationFiles.SYSTEM_PROPERTIES).getAdditionalProperties().get("ray")).isEqualTo("far");
    }

    @Test
    public void toStringReturnsCorrectInformationWhenPropertiesMapIsNotSet() {
        RemoteConfiguration remoteConfiguration = new RemoteConfiguration();

        assertThat(remoteConfiguration.toString()).isEqualTo("RemoteConfiguration [StartServer=false, StopServers=false, StartAndStopServerForEachTest=false, ServerList=]");
    }
}
