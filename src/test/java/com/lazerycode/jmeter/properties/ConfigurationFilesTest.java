package com.lazerycode.jmeter.properties;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationFilesTest {

    @Test
    public void getPropertiesFileNameReturnsExpectedFilename() {
        assertThat(ConfigurationFiles.JMETER_PROPERTIES.getFilename()).isEqualTo("jmeter.properties");
    }

    @Test
    public void createFileIfItDoesNotExistReturnsTrue() {
        assertThat(ConfigurationFiles.JMETER_PROPERTIES.createFileIfItDoesNotExist()).isTrue();
    }

    @Test
    public void createFileIfItDoesNotExistReturnsFalse() {
        assertThat(ConfigurationFiles.GLOBAL_PROPERTIES.createFileIfItDoesNotExist()).isFalse();
    }
}
