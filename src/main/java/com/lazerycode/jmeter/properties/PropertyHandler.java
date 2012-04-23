package com.lazerycode.jmeter.properties;

import com.lazerycode.jmeter.JMeterMojo;
import com.lazerycode.jmeter.UtilityFunctions;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * Handler to deal with properties file creation.
 *
 * @author Arne Franken, Mark Collin
 */
public class PropertyHandler extends JMeterMojo {

    private EnumMap<JMeterPropertiesFiles, PropertyContainer> masterPropertiesMap = new EnumMap<JMeterPropertiesFiles, PropertyContainer> (JMeterPropertiesFiles.class);
    private File propertySourceDirectory;
    private File propertyOutputDirectory;
    private boolean replaceDefaultProperties;

    public PropertyHandler(File sourceDirectory, File outputDirectory, Artifact jMeterConfigArtifact, boolean replaceDefaultProperties) throws MojoExecutionException {
        //Initialise the enum map
        for (JMeterPropertiesFiles propertyFile : JMeterPropertiesFiles.values()) {
            this.masterPropertiesMap.put(propertyFile, new PropertyContainer());
        }
        setSourceDirectory(sourceDirectory);
        setOutputDirectory(outputDirectory);
        this.replaceDefaultProperties = replaceDefaultProperties;
        try {
            this.loadDefaultProperties(jMeterConfigArtifact);
            this.loadCustomProperties();
            loadMiscellaneousProperties(jMeterConfigArtifact);
        } catch (Exception ex) {
            getLog().error("Error loading properties: " + ex);
        }
    }

    /**
     * Load in the default properties held in the JMeter artifact
     *
     * @param jMeterConfigArtifact
     * @throws MojoExecutionException
     */
    private void loadDefaultProperties(Artifact jMeterConfigArtifact) throws IOException {
        for (JMeterPropertiesFiles propertyFile : JMeterPropertiesFiles.values()) {
            if (propertyFile.createFileIfItDoesntExist()) {
                JarFile propertyJar = new JarFile(jMeterConfigArtifact.getFile());
                InputStream sourceFile = propertyJar.getInputStream(propertyJar.getEntry("bin/" + propertyFile.getPropertiesFileName()));
                Properties defaultPropertySet = new Properties();
                defaultPropertySet.load(sourceFile);
                sourceFile.close();
                this.getPropertyObject(propertyFile).setDefaultPropertyObject(defaultPropertySet);
            } else {
                this.getPropertyObject(propertyFile).setDefaultPropertyObject(new Properties());
            }
        }
    }

    /**
     * Load in any custom properties that are available in the propertySourceDirectory
     *
     * @throws IOException
     */
    private void loadCustomProperties() throws IOException {
        for (JMeterPropertiesFiles propertyFile : JMeterPropertiesFiles.values()) {
            File sourceFile = new File(this.propertySourceDirectory.getCanonicalFile() + File.separator + propertyFile.getPropertiesFileName());
            if (sourceFile.exists()) {
                InputStream sourceInputStream = new FileInputStream(sourceFile);
                Properties sourcePropertySet = new Properties();
                sourcePropertySet.load(sourceInputStream);
                sourceInputStream.close();
                getPropertyObject(propertyFile).setCustomPropertyObject(sourcePropertySet);
            }
        }
    }

    /**
     * Copy the miscellaneous default properties held in the JMeter artifact to the bin/ directory
     *
     * @param jMeterConfigArtifact
     * @throws MojoExecutionException
     * @throws java.io.IOException
     */
    private void loadMiscellaneousProperties(Artifact jMeterConfigArtifact) throws MojoExecutionException, IOException {
        //TODO: list may be filled elsewhere. Maybe use an Enum
        List<String> fileList = new ArrayList<String>();
        fileList.add("proxyserver.jks");
        fileList.add("logkit.xml");
        fileList.add("log4j.conf");
        fileList.add("httpclient.parameters");
        fileList.add("hc.parameters");
        fileList.add("BeanShellSampler.bshrc");
        fileList.add("BeanShellListeners.bshrc");
        fileList.add("BeanShellFunction.bshrc");
        fileList.add("BeanShellAssertion.bshrc");

        for (String propertyFile : fileList) {

          InputStream sourceFile = null;

          try {
              //copy properties file.
              JarFile propertyJar = new JarFile(jMeterConfigArtifact.getFile());
              sourceFile = propertyJar.getInputStream(propertyJar.getEntry("bin/" + propertyFile));
              FileOutputStream propertiesFile = new FileOutputStream(new File(this.propertyOutputDirectory.getCanonicalFile() + File.separator + propertyFile));
              IOUtils.copy(sourceFile,propertiesFile);
              propertiesFile.flush();
              propertiesFile.close();
          } catch (IOException e) {
              throw new MojoExecutionException("Error creating properties file " + propertyFile + ": " + e);
          } finally {
              if (sourceFile != null) {
                  sourceFile.close();
              }
          }

        }
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
        this.getPropertyObject(JMeterPropertiesFiles.JMETER_PROPERTIES).setCustomPropertyMap(value);
    }

    public void setJMeterSaveServiceProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.getPropertyObject(JMeterPropertiesFiles.SAVE_SERVICE_PROPERTIES).setCustomPropertyMap(value);
    }

    public void setJMeterSystemProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.getPropertyObject(JMeterPropertiesFiles.SYSTEM_PROPERTIES).setCustomPropertyMap(value);
    }

    public void setJMeterUpgradeProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.getPropertyObject(JMeterPropertiesFiles.UPGRADE_PROPERTIES).setCustomPropertyMap(value);
    }

    public void setJmeterUserProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.getPropertyObject(JMeterPropertiesFiles.USER_PROPERTIES).setCustomPropertyMap(value);
    }

    public void setJMeterGlobalProperties(Map<String, String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.getPropertyObject(JMeterPropertiesFiles.GLOBAL_PROPERTIES).setCustomPropertyMap(value);
    }

    public PropertyContainer getPropertyObject(JMeterPropertiesFiles value){
        return this.masterPropertiesMap.get(value);
    }

    /**
     * Create/Copy the properties files used by JMeter into the JMeter directory tree.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *
     */
    public void configureJMeterPropertiesFiles() throws MojoExecutionException {
        for (JMeterPropertiesFiles propertyFile : JMeterPropertiesFiles.values()) {
            if (this.replaceDefaultProperties) {
                getPropertyObject(propertyFile).setFinalPropertyObject(new PropertyFileMerger().mergeProperties(getPropertyObject(propertyFile).getCustomPropertyMap(), getPropertyObject(propertyFile).getBasePropertiesObject()));
            } else {
                getPropertyObject(propertyFile).setFinalPropertyObject(new PropertyFileMerger().mergeProperties(getPropertyObject(propertyFile).getCustomPropertyMap(), new PropertyFileMerger().mergePropertiesFiles(getPropertyObject(propertyFile).getDefaultPropertyObject(), getPropertyObject(propertyFile).getCustomPropertyObject())));
            }
            try {
                //Write out final properties file.
                FileOutputStream writeOutFinalPropertiesFile = new FileOutputStream(new File(this.propertyOutputDirectory.getCanonicalFile() + File.separator + propertyFile.getPropertiesFileName()));
                getPropertyObject(propertyFile).getFinalPropertyObject().store(writeOutFinalPropertiesFile, null);
                writeOutFinalPropertiesFile.flush();
                writeOutFinalPropertiesFile.close();
            } catch (IOException e) {
                throw new MojoExecutionException("Error creating consolidated properties file " + propertyFile.getPropertiesFileName() + ": " + e);
            }
        }
    }
}
