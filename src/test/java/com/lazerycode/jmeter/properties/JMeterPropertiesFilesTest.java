package com.lazerycode.jmeter.properties;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class JMeterPropertiesFilesTest {

	@Test
	public void getPropertiesFileNameReturnsExpectedFilename() {
		assertThat(JMeterPropertiesFiles.JMETER_PROPERTIES.getFilename(),
				is(equalTo("jmeter.properties")));
	}

	@Test
	public void createFileIfItDoesNotExistReturnsTrue() {
		assertThat(JMeterPropertiesFiles.JMETER_PROPERTIES.createFileIfItDoesNotExist(),
				is(equalTo(true)));
	}

	@Test
	public void createFileIfItDoesNotExistReturnsFalse() {
		assertThat(JMeterPropertiesFiles.GLOBAL_PROPERTIES.createFileIfItDoesNotExist(),
				is(equalTo(false)));
	}
}
