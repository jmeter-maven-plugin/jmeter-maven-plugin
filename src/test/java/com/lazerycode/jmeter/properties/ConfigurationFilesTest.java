package com.lazerycode.jmeter.properties;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ConfigurationFilesTest {

	@Test
	public void getPropertiesFileNameReturnsExpectedFilename() {
		assertThat(ConfigurationFiles.JMETER_PROPERTIES.getFilename(),
				is(equalTo("jmeter.properties")));
	}

	@Test
	public void createFileIfItDoesNotExistReturnsTrue() {
		assertThat(ConfigurationFiles.JMETER_PROPERTIES.createFileIfItDoesNotExist(),
				is(equalTo(true)));
	}

	@Test
	public void createFileIfItDoesNotExistReturnsFalse() {
		assertThat(ConfigurationFiles.GLOBAL_PROPERTIES.createFileIfItDoesNotExist(),
				is(equalTo(false)));
	}
}
