package com.lazerycode.jmeter.properties;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.lazerycode.jmeter.exceptions.IOException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import static com.lazerycode.jmeter.properties.JMeterPropertiesFiles.GLOBAL_PROPERTIES;
import static com.lazerycode.jmeter.properties.JMeterPropertiesFiles.JMETER_PROPERTIES;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

public class PropertiesFilesTest {

	private final URL sourcePropertiesFile = this.getClass().getResource("/testSource.properties");
	private final URL additionsPropertiesFile = this.getClass().getResource("/testAdditions.properties");
	private final URL emptyPropertiesFile = this.getClass().getResource("/empty.properties");
	private final URL fakeJAR = this.getClass().getResource("/fakejar.jar");
	private final File invalidPropertiesFile = new File("/invalid/directory/foo/bar");
	private static final boolean REPLACE_ALL_PROPERTIES = true;
	private static final boolean MERGE_PROPERTIES = false;

	@Test(expected = IOException.class)
	public void invalidPropertiesFileThrowsIOException() throws Exception {
		new PropertiesFiles(invalidPropertiesFile);
	}

	@Test
	public void canCreateAValidPropertiesFilesObject() throws Exception {
		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		Properties initialProperties = propertiesFile.getProperties();

		assertThat(initialProperties.getProperty("log_level.jmeter"), is(equalTo("INFO")));
		assertThat(initialProperties.getProperty("log_level.jmeter.junit"), is(equalTo("DEBUG")));
		assertThat(initialProperties.size(), is(equalTo(2)));
	}

	@Test
	public void emptyPropertiesObjectIsCreatedIfArtifactDoesNotExist() throws Exception {
		PropertiesFiles propertiesFile = new PropertiesFiles(null, JMETER_PROPERTIES);
		Properties initialProperties = propertiesFile.getProperties();

		assertThat(initialProperties.size(), is(equalTo(0)));
	}

	@Test(expected = IOException.class)
	public void ifTheArtifactIsInvalidAnIOExceptionWillBeThrown() throws Exception {
		Artifact artifact = new DefaultArtifact("g.i.d:a.i.d:1.0-SNAPSHOT").setFile(invalidPropertiesFile);
		PropertiesFiles propertiesFile = new PropertiesFiles(artifact, JMETER_PROPERTIES);
		Properties initialProperties = propertiesFile.getProperties();

		assertThat(initialProperties.size(), is(equalTo(0)));
	}

	@Test
	public void emptyPropertiesObjectIsCreatedIfFileShouldNotBeCreatedIfItDoesNotExist() throws Exception {
		Artifact artifact = new DefaultArtifact("g.i.d:a.i.d:1.0-SNAPSHOT").setFile(invalidPropertiesFile);
		PropertiesFiles propertiesFile = new PropertiesFiles(artifact, GLOBAL_PROPERTIES);
		Properties initialProperties = propertiesFile.getProperties();

		assertThat(initialProperties.size(), is(equalTo(0)));
	}

	@Test
	public void createAPropertiesFileObjectUsingAnArtifact() throws Exception {
		Artifact jarFile = new DefaultArtifact("g.i.d:a.i.d:1.0-SNAPSHOT").setFile(new File(fakeJAR.getFile()));
		PropertiesFiles propertiesFile = new PropertiesFiles(jarFile, JMETER_PROPERTIES);
		Properties initialProperties = propertiesFile.getProperties();

		assertThat(initialProperties.size(), is(equalTo(73)));
	}

	@Test
	public void mergeValidPropertiesFromAListWithEmptyValuesTest() throws Exception {
		HashMap<String, String> customProperties = new HashMap<>();
		customProperties.put("log_level.jmeter.control", null);
		customProperties.put("log_level.jmeter", "");
		customProperties.put("log_level.jmeter.junit", "    ");

		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		propertiesFile.addAndOverwriteProperties(customProperties);
		Properties modifiedProperties = propertiesFile.getProperties();

		assertThat(modifiedProperties.getProperty("log_level.jmeter"), is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"), is(equalTo("DEBUG")));
		assertThat(modifiedProperties.size(), is(equalTo(2)));
	}

	@Test
	public void mergeValidPropertiesFromListTest() throws Exception {
		HashMap<String, String> customProperties = new HashMap<>();
		customProperties.put("log_level.jmeter.control", "INFO");
		customProperties.put("log_level.jmeter", "DEBUG");

		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		propertiesFile.addAndOverwriteProperties(customProperties);
		Properties modifiedProperties = propertiesFile.getProperties();

		assertThat(modifiedProperties.getProperty("log_level.jmeter"), is(equalTo("DEBUG")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.control"), is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"), is(equalTo("DEBUG")));
		assertThat(modifiedProperties.size(), is(equalTo(3)));
	}

	@Test
	public void mergeValidPropertiesFromFileTest() throws Exception {
		HashMap<String, String> customProperties = new HashMap<String, String>();
		customProperties.put("log_level.jmeter.control", "INFO");
		customProperties.put("log_level.jmeter", "DEBUG");

		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		propertiesFile.loadProvidedPropertiesIfAvailable(new File(additionsPropertiesFile.getFile()), MERGE_PROPERTIES);
		Properties modifiedProperties = propertiesFile.getProperties();

		assertThat(modifiedProperties.getProperty("log_level.jmeter"), is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"), is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.control"), is(equalTo("DEBUG")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.testbeans"), is(equalTo("DEBUG")));
		assertThat(modifiedProperties.size(), is(equalTo(4)));
	}

	@Test
	public void replaceValidPropertiesFromFileTest() throws Exception {
		HashMap<String, String> customProperties = new HashMap<String, String>();
		customProperties.put("log_level.jmeter.control", "INFO");
		customProperties.put("log_level.jmeter", "DEBUG");

		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		propertiesFile.loadProvidedPropertiesIfAvailable(new File(additionsPropertiesFile.getFile()), REPLACE_ALL_PROPERTIES);
		Properties modifiedProperties = propertiesFile.getProperties();

		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"), is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.control"), is(equalTo("DEBUG")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.testbeans"), is(equalTo("DEBUG")));
		assertThat(modifiedProperties.size(), is(equalTo(3)));
	}

	@Test
	public void fileIgnoredIfItIsNotAvailableTest() throws Exception {
		HashMap<String, String> customProperties = new HashMap<String, String>();
		customProperties.put("log_level.jmeter.control", "INFO");
		customProperties.put("log_level.jmeter", "DEBUG");

		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		propertiesFile.loadProvidedPropertiesIfAvailable(invalidPropertiesFile, MERGE_PROPERTIES);
		Properties modifiedProperties = propertiesFile.getProperties();

		assertThat(modifiedProperties.getProperty("log_level.jmeter"), is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"), is(equalTo("DEBUG")));

		assertThat(modifiedProperties.size(), is(equalTo(2)));
	}

	@Test(expected = IOException.class)
	public void tryingToWriteToAnInvalidFileThrowsAnIOException() throws Exception {
		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		propertiesFile.writePropertiesToFile(invalidPropertiesFile);
	}

	@Test
	public void canWritePropertiesToAFile() throws Exception {
		File writtenProperties = File.createTempFile("output", ".properties");
		writtenProperties.deleteOnExit();
		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		propertiesFile.writePropertiesToFile(writtenProperties);

		Properties readWrittenProperties = new Properties();
		try (FileInputStream writtenPropertiesInputStream = new FileInputStream(writtenProperties)) {
			readWrittenProperties.load(writtenPropertiesInputStream);
		}

		assertThat(readWrittenProperties.getProperty("log_level.jmeter"), is(equalTo("INFO")));
		assertThat(readWrittenProperties.getProperty("log_level.jmeter.junit"), is(equalTo("DEBUG")));
		assertThat(readWrittenProperties.size(), is(equalTo(2)));
	}

	@Test
	public void nothingWrittenToFileIfThereAreNoProperties() throws Exception {
		PropertiesFiles propertiesFile = new PropertiesFiles(new File(emptyPropertiesFile.toURI()));

		assertThat(propertiesFile.getProperties().size(), is(equalTo(0)));

		propertiesFile.writePropertiesToFile(invalidPropertiesFile);

		assertThat(invalidPropertiesFile.exists(), is(equalTo(false)));
	}

	@Test
	public void reservedPropertiesAreStrippedOutBeforeWritingFile() throws Exception {
		HashMap<String, String> reservedProperties = new HashMap<>();
		reservedProperties.put("java.class.path", "/foo/bar");
		reservedProperties.put("user.dir", "/bar/foo");
		reservedProperties.put("jmeter.save.saveservice.output_format", "pink");
		reservedProperties.put("jmeterengine.remote.system.exit", "true");
		reservedProperties.put("jmeterengine.stopfail.system.exit", "true");

		File writtenProperties = File.createTempFile("output", ".properties");
		writtenProperties.deleteOnExit();
		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		propertiesFile.addAndOverwriteProperties(reservedProperties);

		assertThat(propertiesFile.getProperties().size(), is(equalTo(7)));

		propertiesFile.writePropertiesToFile(writtenProperties);

		Properties readWrittenProperties = new Properties();
		try (FileInputStream writtenPropertiesInputStream = new FileInputStream(writtenProperties)) {
			readWrittenProperties.load(writtenPropertiesInputStream);
		}

		assertThat(readWrittenProperties.getProperty("log_level.jmeter"), is(equalTo("INFO")));
		assertThat(readWrittenProperties.getProperty("log_level.jmeter.junit"), is(equalTo("DEBUG")));
		assertThat(readWrittenProperties.size(), is(equalTo(2)));
	}

	@Test
	public void nothingWrittenToFileIfAllPropertiesAreReserved() throws Exception {
		HashMap<String, String> reservedProperties = new HashMap<>();
		reservedProperties.put("java.class.path", "/foo/bar");
		reservedProperties.put("user.dir", "/bar/foo");
		reservedProperties.put("jmeter.save.saveservice.output_format", "pink");
		reservedProperties.put("jmeterengine.remote.system.exit", "true");
		reservedProperties.put("jmeterengine.stopfail.system.exit", "true");

		PropertiesFiles propertiesFile = new PropertiesFiles(new File(emptyPropertiesFile.toURI()));
		propertiesFile.addAndOverwriteProperties(reservedProperties);

		assertThat(propertiesFile.getProperties().size(), is(equalTo(5)));

		propertiesFile.writePropertiesToFile(invalidPropertiesFile);

		assertThat(propertiesFile.getProperties().size(), is(equalTo(0)));

		assertThat(invalidPropertiesFile.exists(), is(equalTo(false)));
	}

	@Test
	public void usersAreWarnedOfPotentialPropertyNameErrors() throws Exception {
		final String EXPECTED_LOG_ENTRY = "You have set a property called 'log_level.JMETER' which is very similar to 'log_level.jmeter'!";
		Logger root = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
		final Appender mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		HashMap<String, String> customProperties = new HashMap<>();
		customProperties.put("log_level.JMETER", "DEBUG");

		PropertiesFiles propertiesFile = new PropertiesFiles(new File(sourcePropertiesFile.toURI()));
		propertiesFile.addAndOverwriteProperties(customProperties);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher() {
			@Override
			public boolean matches(final Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage().equals(EXPECTED_LOG_ENTRY);
			}
		}));

		Properties modifiedProperties = propertiesFile.getProperties();

		assertThat(modifiedProperties.getProperty("log_level.jmeter"), is(equalTo("INFO")));
		assertThat(modifiedProperties.getProperty("log_level.jmeter.junit"), is(equalTo("DEBUG")));
		assertThat(modifiedProperties.getProperty("log_level.JMETER"), is(equalTo("DEBUG")));
		assertThat(modifiedProperties.size(), is(equalTo(3)));
	}
}
