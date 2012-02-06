package com.lazerycode.jmeter.properties;

import com.lazerycode.jmeter.JMeterMojo;
import com.lazerycode.jmeter.UtilityFunctions;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * Handler to deal with properties file creation.
 *
 * @author Arne Franken, Mark Collin
 */
//TODO: should PropertyHandler really extend JMeterMojo just for using getLog()?
public class PropertyHandler extends JMeterMojo {

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

    public PropertyHandler(File sourceDirectory, File outputDirectory, Artifact jMeterConfigArtifact) throws MojoExecutionException {
        setSourceDirectory(sourceDirectory);
        setOutputDirectory(outputDirectory);
        this.jMeterConfigArtifact = jMeterConfigArtifact;
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
     * Merge properties from sourceFile and customProperties into the given outputDirectory
     *
     * @param propertyFile
     * @param outputDirectory
     * @throws MojoExecutionException
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
            Properties modifiedProperties = new PropertyFileMerger(baseProperties).mergeProperties(this.masterPropertiesMap.get(propertyFile));
            //Write out final properties file.
            FileOutputStream writeOutFinalPropertiesFile = new FileOutputStream(new File(outputDirectory.getCanonicalFile() + File.separator + propertyFile.getPropertiesFileName()));
            modifiedProperties.store(writeOutFinalPropertiesFile, null);
            writeOutFinalPropertiesFile.flush();
            writeOutFinalPropertiesFile.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating consolidated properties file " + propertyFile.getPropertiesFileName() + ": " + e);
        }
    }

    //=======================================================================================================

    /**
     * Load the individual properties maps into the master map
     */
    private void setMasterPropertiesMap() {
        masterPropertiesMap.put(JMeterPropertiesFiles.JMETER_PROPERTIES, this.jMeterProperties);
        masterPropertiesMap.put(JMeterPropertiesFiles.SAVE_SERVICE_PROPERTIES, this.jMeterSaveServiceProperties);
        masterPropertiesMap.put(JMeterPropertiesFiles.UPGRADE_PROPERTIES, this.jMeterUpgradeProperties);
        masterPropertiesMap.put(JMeterPropertiesFiles.SYSTEM_PROPERTIES, this.jMeterSystemProperties);
        masterPropertiesMap.put(JMeterPropertiesFiles.USER_PROPERTIES, this.jMeterUserProperties);
        //TODO: now, global properties are "real" global properties, meaning properties have to be defined twice if they should be used locally and on remote servers.
        masterPropertiesMap.put(JMeterPropertiesFiles.GLOBAL_PROPERTIES, this.jMeterGlobalProperties);
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
            getLog().debug("No custom file " + value.getPropertiesFileName() + " found ...");
            if (value.createFileIfItDoesntExist()) {
                getLog().info("Using default JMeter version of " + value.getPropertiesFileName() + "...");
                JarFile propertyJar = new JarFile(this.jMeterConfigArtifact.getFile());
                return propertyJar.getInputStream(propertyJar.getEntry("bin/" + value.getPropertiesFileName()));
            }
            return null;
        }
        return new FileInputStream(sourcePropertyFile);
    }
}
