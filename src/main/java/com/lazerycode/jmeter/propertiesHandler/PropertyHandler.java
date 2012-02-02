package com.lazerycode.jmeter.propertiesHandler;

import com.lazerycode.jmeter.UtilityFunctions;
import com.lazerycode.jmeter.enums.JMeterPropertiesFiles;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * Handler to deal with properties file creation.
 */
public class PropertyHandler {

    private Log log;
    private Map<JMeterPropertiesFiles, Map<String, String>> masterPropertiesMap = new HashMap<JMeterPropertiesFiles, Map<String, String>>();
    private Map<String, String> jMeterProperties = null;
    private Map<String, String> jMeterSaveServiceProperties = null;
    private Map<String, String> jMeterSystemProperties = null;
    private Map<String, String> jMeterUpgradeProperties = null;
    private Map<String, String> jMeterUserProperties = null;
    private Map<String, String> jMeterGlobalProperties = null;
    private Artifact jMeterConfigArtifact;
    private File propertySourceDirectory;
    private File propertyOutputDirectory;

    public PropertyHandler(File sourceDirectory, File outputDirectory, Artifact jMeterConfigArtifact, Log log) throws MojoExecutionException {
        setSourceDirectory(sourceDirectory);
        setOutputDirectory(outputDirectory);
        this.jMeterConfigArtifact = jMeterConfigArtifact;
        this.log = log;
    }

    /**
     * Check that the source directory exists, throw an error if it does not
     *
     * @param value
     * @throws MojoExecutionException
     */
    private void setSourceDirectory(File value) throws MojoExecutionException {
        if (value.exists()) {
            this.propertySourceDirectory = value;
        } else {
            throw new MojoExecutionException("Property source directory '" + value.getAbsolutePath() + "' does not exist!");
        }
    }

    /**
     * Create the output directory, throw an error if we can't
     *
     * @param value
     * @throws MojoExecutionException
     */
    private void setOutputDirectory(File value) throws MojoExecutionException {
        if (!value.exists()) {
            if (!value.mkdirs()) {
                throw new MojoExecutionException("Property output directory '" + value.getAbsolutePath() + "' cannot be created!");
            }
        }
        this.propertyOutputDirectory = value;
    }

    public void setJMeterProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.jMeterProperties = value;
    }

    public void setJMeterSaveServiceProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.jMeterSaveServiceProperties = value;
    }

    public void setJMeterSystemProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.jMeterSystemProperties = value;
    }

    public void setJMeterUpgradeProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.jMeterUpgradeProperties = value;
    }

    public void setJmeterUserProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.jMeterUserProperties = value;
    }

    public void setJMeterGlobalProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.jMeterGlobalProperties = value;
    }

    /**
     * Create/Copy the properties files used by JMeter into the JMeter directory tree.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *
     */
    public void configureJMeterPropertiesFiles() throws MojoExecutionException {
        setMasterPropertiesMap();
        for (JMeterPropertiesFiles propertyFile : JMeterPropertiesFiles.values()) {
            mergePropertiesFile(propertyFile, this.propertyOutputDirectory);
        }
    }

    /**
     * Load the individual properties maps into the master map
     */
    private void setMasterPropertiesMap() {
        masterPropertiesMap.put(JMeterPropertiesFiles.JMETER_PROPERTIES, this.jMeterProperties);
        masterPropertiesMap.put(JMeterPropertiesFiles.SAVE_SERVICE_PROPERTIES, this.jMeterSaveServiceProperties);
        masterPropertiesMap.put(JMeterPropertiesFiles.UPGRADE_PROPERTIES, this.jMeterUpgradeProperties);
        masterPropertiesMap.put(JMeterPropertiesFiles.SYSTEM_PROPERTIES, this.jMeterSystemProperties);
        masterPropertiesMap.put(JMeterPropertiesFiles.USER_PROPERTIES, this.jMeterUserProperties);
        masterPropertiesMap.put(JMeterPropertiesFiles.GLOBAL_PROPERTIES, this.jMeterGlobalProperties);
    }

    /**
     * Merge properties from sourceFile and customProperties into the given outputDirectory
     */
    public void mergePropertiesFile(JMeterPropertiesFiles propertyFile, File outputDirectory) throws MojoExecutionException {
        InputStream sourceFile = null;
        try {
            sourceFile = getSourcePropertyFile(propertyFile);
        } catch (IOException ex) {
            throw new MojoExecutionException("Error setting source file InputStream: " + ex);
        }
        //Drop out right away if there is nothing to create, source file will never be null if the properties file is required.
        if (sourceFile == null && this.masterPropertiesMap.get(propertyFile) == null) return;
        Properties baseProperties = new Properties();
        try {
            //Only read in base properties if there are some
            if (sourceFile != null) {
                baseProperties.load(sourceFile);
                sourceFile.close();
            }
            //Create final properties set
            Properties modifiedProperties = mergeProperties(baseProperties, this.masterPropertiesMap.get(propertyFile));
            //Write out final properties file.
            FileOutputStream writeOutFinalPropertiesFile = new FileOutputStream(new File(outputDirectory.getCanonicalFile() + File.separator + propertyFile.getPropertiesFileName()));
            modifiedProperties.store(writeOutFinalPropertiesFile, null);
            writeOutFinalPropertiesFile.flush();
            writeOutFinalPropertiesFile.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating consolidated properties file " + propertyFile.getPropertiesFileName() + ": " + e);
        }
    }

    /**
     * This will load in a custom properties file and return an InputStream.
     * If it does not exist it will drop back to the default JMeter properties file (if populated).
     * If the default JMeter properties file is not populated it will return a null.
     *
     * @param value
     * @return
     * @throws IOException
     */
    private InputStream getSourcePropertyFile(JMeterPropertiesFiles value) throws IOException {
        File sourcePropertyFile = new File(this.propertySourceDirectory.getCanonicalFile() + File.separator + value.getPropertiesFileName());
        if (!sourcePropertyFile.exists()) {
            log.warn("Unable to find " + value.getPropertiesFileName() + "...");
            if (value.createFileIfItDoesntExist()) {
                log.warn("Using default JMeter version of " + value.getPropertiesFileName() + "...");
                JarFile propertyJar = new JarFile(this.jMeterConfigArtifact.getFile());
                return propertyJar.getInputStream(propertyJar.getEntry("bin/" + value.getPropertiesFileName()));
            }
            return null;
        }
        return new FileInputStream(sourcePropertyFile);
    }

    /**
     * Merge given Map into given Properties object
     *
     * @param properties       object to merge the Map into
     * @param customProperties Map to merge into the Properties object
     * @return merged Properties object
     */
    private static Properties mergeProperties(Properties properties, Map<String, String> customProperties) {
        if (customProperties != null && !customProperties.isEmpty()) {
            for (String key : customProperties.keySet()) {
                //TODO check to see if property being set is a close match to an existing property and warn user if it is e.g. have they set User.dir instead of user.dir
                //TODO remove any reserved properties
                properties.setProperty(key, customProperties.get(key));
            }
        }
        return properties;
    }
}
