package com.lazerycode.jmeter.properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

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
     * @throws MojoExecutionException MojoExecutionException
     */
    public PropertiesFile(File propertiesFile) throws MojoExecutionException {
        this.properties = loadPropertiesFile(propertiesFile);
    }

    /**
     * Create a new PropertiesFiles object from a properties file held in the jMeterConfigArtifact
     *
     * @param jmeterConfigArtifact The artifact to try and find the properties file in
     * @param jMeterPropertiesFile The properties file that we want to find in the jmeterConfigArtifact
     * @throws MojoExecutionException MojoExecutionException
     */
    public PropertiesFile(Artifact jmeterConfigArtifact, ConfigurationFiles jMeterPropertiesFile) throws MojoExecutionException { // NOSONAR
        Properties defaultPropertySet = new Properties();
        if (null != jmeterConfigArtifact && jMeterPropertiesFile.createFileIfItDoesNotExist()) {
            try (JarFile propertyJar = new JarFile(jmeterConfigArtifact.getFile())) {
                try (InputStream sourceFile = propertyJar.getInputStream(propertyJar.getEntry("bin/" + jMeterPropertiesFile.getFilename()))) {
                    defaultPropertySet.load(sourceFile);
                }
            } catch (IOException ex) {
                throw new MojoExecutionException(ex.getMessage(), ex);
            }
        }

        this.properties = defaultPropertySet;
    }

    /**
     * Take a properties file and load it into a Properties Object
     *
     * @param propertiesFile The file to try and load
     * @return A properties object
     * @throws MojoExecutionException MojoExecutionException
     */
    private Properties loadPropertiesFile(File propertiesFile) throws MojoExecutionException { // NOSONAR
        try (FileInputStream propertiesFileInputStream = new FileInputStream(propertiesFile)) {
            Properties loadedProperties = new Properties();
            loadedProperties.load(propertiesFileInputStream);
            return loadedProperties;
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Check if a file exists.  If it does calculate if we need to merge it with existing properties, or replace all existing properties.
     *
     * @param providedPropertiesFile A properties file that will be check to see if it exists
     * @param replaceAllProperties   If we should replace all properties, or just merge them
     * @throws MojoExecutionException MojoExecutionException
     */
    public void loadProvidedPropertiesIfAvailable(File providedPropertiesFile, boolean replaceAllProperties) throws MojoExecutionException {
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
            if (!additionalPropertiesMap.getValue().trim().isEmpty()) {
                properties.setProperty(additionalPropertiesMap.getKey(), additionalPropertiesMap.getValue());
                warnUserOfPossibleErrors(additionalPropertiesMap.getKey(), properties);
            }
        }
    }


    /**
     * Strip out any reserved properties and then write properties object to a file.
     *
     * @param outputFile The file that our properties object will be written to
     * @throws MojoExecutionException
     */
    public void writePropertiesToFile(File outputFile) throws MojoExecutionException {// NOSONAR
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
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
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
            if (!key.equals(newKey) && key.equalsIgnoreCase(newKey)) {
                LOGGER.warn("You have set a property called '{}' which is very similar to '{}'!",
                        newKey, key);
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
