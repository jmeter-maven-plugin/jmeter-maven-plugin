package com.lazerycode.jmeter.properties;

import com.lazerycode.jmeter.UtilityFunctions;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class PropertyFileMergerTest {

	private final URL sourcePropertiesFiles = this.getClass().getResource("/testSource.properties");
	private final URL additionsPropertiesFile = this.getClass().getResource("/testAdditions.properties");
	private final PrintStream originalOut = System.out;

	@Test
	public void validMergePropertiesTest() throws Exception {
		HashMap<String, String> customProperties = new HashMap<String, String>();
		customProperties.put("log_level.jmeter.control", "INFO");
		customProperties.put("log_level.jmeter", "DEBUG");

		Properties propertiesFile = new Properties();
		propertiesFile.load(new FileInputStream(new File(sourcePropertiesFiles.toURI())));

		Properties modifiedProperties = new PropertyFileMerger().mergeProperties(customProperties, propertiesFile);

		assertThat(modifiedProperties.getProperty("log_level.jmeter"),
				is(equalTo("DEBUG")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.control"),
				is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"),
				is(equalTo("DEBUG")));
		assertThat(modifiedProperties.size(),
				is(equalTo(3)));
	}

	@Test
	public void attemptToMergeReservedProperty() throws Exception {
		HashMap<String, String> customProperties = new HashMap<String, String>();
		customProperties.put("user.dir", "/home/foo");

		Properties propertiesFile = new Properties();
		propertiesFile.load(new FileInputStream(new File(sourcePropertiesFiles.toURI())));

		ByteArrayOutputStream logCapture = new ByteArrayOutputStream();
		System.setOut(new PrintStream(logCapture));

		Properties modifiedProperties = new PropertyFileMerger().mergeProperties(customProperties, propertiesFile);

		String loggedMessage = UtilityFunctions.stripCarriageReturns(logCapture.toString());
		System.setOut(originalOut);

		assertThat(loggedMessage,
				is(equalTo("[warn] Unable to set 'user.dir', it is a reserved property in the jmeter-maven-plugin")));
		assertThat(modifiedProperties.containsKey("user.dir"),
				is(equalTo(false)));
		assertThat(modifiedProperties.size(),
				is(equalTo(2)));
	}

	@Test
	public void showWarningWithSimilarProperty() throws Exception {
		HashMap<String, String> customProperties = new HashMap<String, String>();
		customProperties.put("log_level.Jmeter", "INFO");

		Properties propertiesFile = new Properties();
		propertiesFile.load(new FileInputStream(new File(sourcePropertiesFiles.toURI())));

		ByteArrayOutputStream logCapture = new ByteArrayOutputStream();
		System.setOut(new PrintStream(logCapture));

		Properties modifiedProperties = new PropertyFileMerger().mergeProperties(customProperties, propertiesFile);

		String loggedMessage = UtilityFunctions.stripCarriageReturns(logCapture.toString());
		System.setOut(originalOut);

		assertThat(loggedMessage,
				is(equalTo("[warn] You have set a property called 'log_level.Jmeter' which is very similar to 'log_level.jmeter'!")));
		assertThat(modifiedProperties.size(),
				is(equalTo(3)));
	}

	@Test
	public void mergeTwoPropertiesFiles() throws Exception {

		Properties propertiesFile = new Properties();
		propertiesFile.load(new FileInputStream(new File(sourcePropertiesFiles.toURI())));

		Properties customProperties = new Properties();
		customProperties.load(new FileInputStream(new File(additionsPropertiesFile.toURI())));

		Properties modifiedProperties = new PropertyFileMerger().mergePropertiesFiles(propertiesFile, customProperties);

		assertThat(modifiedProperties.getProperty("log_level.jmeter"),
				is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.control"),
				is(equalTo("DEBUG")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"),
				is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.testbeans"),
				is(equalTo("DEBUG")));
		assertThat(modifiedProperties.containsKey("log_level.jmeter.engine"),
				is(equalTo(false)));
		assertThat(modifiedProperties.size(),
				is(equalTo(4)));
	}

	@Test
	public void mergeTwoPropertiesWithNullSource() throws Exception {

		Properties propertiesFile = null;

		Properties customProperties = new Properties();
		customProperties.load(new FileInputStream(new File(this.additionsPropertiesFile.toURI())));

		Properties modifiedProperties = new PropertyFileMerger().mergePropertiesFiles(propertiesFile, customProperties);

		assertThat(modifiedProperties.getProperty("log_level.jmeter.control"),
				is(equalTo("DEBUG")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"),
				is(equalTo("INFO")));
		assertThat(modifiedProperties.containsKey("log_level.jmeter"),
				is(equalTo(false)));
		assertThat(modifiedProperties.containsKey("log_level.jmeter.engine"),
				is(equalTo(false)));
		assertThat(modifiedProperties.size(),
				is(equalTo(3)));
	}

	@Test
	public void mergeTwoPropertiesWithNullAdditions() throws Exception {
		Properties propertiesFile = new Properties();
		propertiesFile.load(new FileInputStream(new File(this.sourcePropertiesFiles.toURI())));

		Properties customProperties = null;

		Properties modifiedProperties = new PropertyFileMerger().mergePropertiesFiles(propertiesFile, customProperties);

		assertThat(modifiedProperties.getProperty("log_level.jmeter"),
				is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"),
				is(equalTo("DEBUG")));
		assertThat(modifiedProperties.containsKey("log_level.jmeter.control"),
				is(equalTo(false)));
		assertThat(modifiedProperties.containsKey("log_level.jmeter.engine"),
				is(equalTo(false)));
		assertThat(modifiedProperties.size(),
				is(equalTo(2)));
	}

	@Test
	public void validPropertiesObjectReturnIfBothFilesAreNull() {
		IsInstanceOf anInstanceOfPropertiesClass = new IsInstanceOf(Properties.class);
		Properties propertiesFile = null;
		Properties customProperties = null;

		Properties modifiedProperties = new PropertyFileMerger().mergePropertiesFiles(propertiesFile, customProperties);
		assertThat(modifiedProperties,
				is(anInstanceOfPropertiesClass));
		assertThat(modifiedProperties.size(),
				is(equalTo(0)));
	}

}
