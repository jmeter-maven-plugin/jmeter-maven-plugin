package com.lazerycode.jmeter.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lazerycode.jmeter.exceptions.IOException;

public class PropertiesFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFile.class);
	private Properties properties;

	/**
	 * Basic constructor for testing purposes.
	 */
	public PropertiesFile() {
		this.properties = new Properties();
	}

	/**
	 * Create a new PropertiesFiles object from a properties file
	 *
	 * @param propertiesFile The file to use to create a PropertiesFiles object
	 * @throws IOException
	 */
	public PropertiesFile(File propertiesFile) throws IOException {
		this.properties = loadPropertiesFile(propertiesFile);
	}

	/**
	 * Create a new PropertiesFiles object from a properties file held in the jMeterConfigArtifact
	 *
	 * @param jmeterConfigArtifact The artifact to try and find the properties file in
	 * @param jMeterPropertiesFile The properties file that we want to find in the jmeterConfigArtifact
	 * @throws IOException
	 */
	public PropertiesFile(Artifact jmeterConfigArtifact, ConfigurationFiles jMeterPropertiesFile) throws IOException {
		Properties defaultPropertySet = new Properties();
		if (null != jmeterConfigArtifact && jMeterPropertiesFile.createFileIfItDoesNotExist()) {
			try (JarFile propertyJar = new JarFile(jmeterConfigArtifact.getFile())){
				try (InputStream sourceFile = propertyJar.getInputStream(propertyJar.getEntry("bin/" + jMeterPropertiesFile.getFilename()))) {
					defaultPropertySet.load(sourceFile);
				}
			} catch (java.io.IOException ex) {
				throw new IOException(ex.getMessage(), ex);
			}
		}

		this.properties = defaultPropertySet;
	}

	/**
	 * Take a properties file and load it into a Properties Object
	 *
	 * @param propertiesFile The file to try and load
	 * @return A properties object
	 * @throws IOException
	 */
	private Properties loadPropertiesFile(File propertiesFile) throws IOException {
		try (FileInputStream propertiesFileInputStream = new FileInputStream(propertiesFile)) {
			Properties loadedProperties = new Properties();
			loadedProperties.load(propertiesFileInputStream);
			return loadedProperties;
		} catch (java.io.IOException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Check if a file exists.  If it does calculate if we need to merge it with existing properties, or replace all existing properties.
	 *
	 * @param providedPropertiesFile A properties file that will be check to see if it exists
	 * @param replaceAllProperties   If we should replace all properties, or just merge them
	 * @throws IOException
	 */
	public void loadProvidedPropertiesIfAvailable(File providedPropertiesFile, boolean replaceAllProperties) throws IOException {
		if (providedPropertiesFile.exists()) {
			Properties providedPropertySet = loadPropertiesFile(providedPropertiesFile);
			if (replaceAllProperties) {
				this.properties = providedPropertySet;
			} else {
				this.properties.putAll(providedPropertySet);
			}
		}
	}

	/**
	 * Merge a Map of properties into our Properties object
	 * The additions will overwrite any existing properties
	 *
	 * @param additionalProperties Map to merge into our Properties object
	 */
	public void addAndOverwriteProperties(Map<String, String> additionalProperties) {
		additionalProperties.values().removeAll(Collections.singleton(null));
		for (Map.Entry<String, String> additionalPropertiesMap : additionalProperties.entrySet()) {
			if (!additionalPropertiesMap.getValue().trim().equals("")) {
				properties.setProperty(additionalPropertiesMap.getKey(), additionalPropertiesMap.getValue());
				warnUserOfPossibleErrors(additionalPropertiesMap.getKey(), properties);
			}
		}
	}

	/**
	 * Strip out any reserved properties and then write properties object to a file.
	 *
	 * @param outputFile The file that our properties object will be written to
	 */
	public void writePropertiesToFile(File outputFile) throws IOException {
		stripOutReservedProperties();
		//TODO if jmeter.properties write properties that are required for plugin
		if (properties.isEmpty()) {
			return;
		}
		try {
			try (FileOutputStream writeOutFinalPropertiesFile = new FileOutputStream(outputFile)) {
				properties.store(writeOutFinalPropertiesFile, null);
				writeOutFinalPropertiesFile.flush();
			}
		} catch (java.io.IOException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Print a warning out to the user to highlight potential typos in the properties they have set.
	 *
	 * @param newKey         Property Value
	 * @param baseProperties Properties
	 */
	private void warnUserOfPossibleErrors(String newKey, Properties baseProperties) {
		for (String key : baseProperties.stringPropertyNames()) {
			if (!key.equals(newKey) && key.toLowerCase().equals(newKey.toLowerCase())) {
				LOGGER.warn("You have set a property called '" + newKey + "' which is very similar to '" + key + "'!");
			}
		}
	}

	/**
	 * This will strip all reserved properties from a Properties object.
	 * (Used to ensure that restricted properties haven't been set in custom properties files)
	 */
	protected void stripOutReservedProperties() {
		for (ReservedProperties reservedProperty : ReservedProperties.values()) {
			if (properties.containsKey(reservedProperty.getPropertyKey())) {
				properties.remove(reservedProperty.getPropertyKey());
				LOGGER.warn("Unable to set '" + reservedProperty.getPropertyKey() + "', it is a reserved property in the jmeter-maven-plugin");
			}
		}
	}

	/**
	 * A getter used for testing purposes
	 *
	 * @return this.properties
	 */
	public Properties getProperties() {
		return properties;
	}
}
