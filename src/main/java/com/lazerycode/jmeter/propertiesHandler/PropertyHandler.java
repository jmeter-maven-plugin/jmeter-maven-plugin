package com.lazerycode.jmeter.propertiesHandler;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Created by IntelliJ IDEA.
 * User: Mark Collin
 * Date: 01/02/12
 * Time: 18:15
 * To change this template use File | Settings | File Templates.
 */
public class PropertyHandler {

    private Map<String, String> jMeterProperties = null;
    private Map<String, String> jMeterSaveServiceProperties = null;
    private Map<String, String> jMeterSystemProperties = null;
    private Map<String, String> jMeterUpgradeProperties = null;
    private Map<String, String> jMeterUserProperties = null;
    private Map<String, String> jMeterGlobalProperties = null;
    private Artifact jmeterConfigArtifact;
    private File propertySourceDirectory;
    private File propertyOutputDirectory;

    public PropertyHandler(File sourceDirectory, File outputDirectory, Artifact jmeterConfigArtifact) throws MojoExecutionException {
        setSourceDirectory(sourceDirectory);
        this.propertyOutputDirectory = outputDirectory;
        this.jmeterConfigArtifact = jmeterConfigArtifact;
    }

    private void setSourceDirectory(File value) throws MojoExecutionException {
        if (value.exists()) {
            this.propertySourceDirectory = value;
        } else {
            throw new MojoExecutionException("Property source directory '" + value.getAbsolutePath() + "' does not exist!");
        }
    }

    private void setOutputDirectory(File value) throws MojoExecutionException {
        if (!value.exists()) {
            if (!value.mkdirs()) {
                throw new MojoExecutionException("Property output directory '" + value.getAbsolutePath() + "' cannot be created!");
            }
        }
        this.propertyOutputDirectory = value;
    }

    public void setJMeterProperties(Map<String, String> value) {
        if (value.size() == 0) return;
        this.jMeterProperties = value;
    }

    public void setJMeterSaveServiceProperties(Map<String, String> value) {
        if (value.size() == 0) return;
        this.jMeterSaveServiceProperties = value;
    }

    public void setJMeterSystemProperties(Map<String, String> value) {
        if (value.size() == 0) return;
        this.jMeterSystemProperties = value;
    }

    public void setJMeterUpgradeProperties(Map<String, String> value) {
        if (value.size() == 0) return;
        this.jMeterUpgradeProperties = value;
    }

    public void setJmeterUserProperties(Map<String, String> value) {
        if (value.size() == 0) return;
        this.jMeterUserProperties = value;
    }

    public void setJMeterGlobalProperties(Map<String, String> value) {
        if (value.size() == 0) return;
        this.jMeterGlobalProperties = value;
    }

    /**
     * Create/Copy the properties files used by JMeter into the JMeter directory tree.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *
     */
    private void configureJMeterPropertiesFiles() throws MojoExecutionException {

        Map<String, Map<String, String>> propertiesMapping = getJmeterPropertyFileToPropertiesMapping();
        for (String propertyFileName : propertiesMapping.keySet()) {
            try {
                OutputStream out = getPropertyFileOutputStream(propertyFileName);

                InputStream in = getPropertyFileInputStream(propertyFileName);
                PropertyFileMerger.mergePropertiesFile(in, out, propertiesMapping.get(propertyFileName));
            } catch (Exception e) {
                throw new MojoExecutionException("Could not create temporary property file: " + propertyFileName + " in directory ", e);
            }
        }
    }

    private OutputStream getPropertyFileOutputStream(String propertyFileName) throws FileNotFoundException {
        return new FileOutputStream(new File(this.propertyOutputDirectory + File.separator + propertyFileName));
    }

    private InputStream getPropertyFileInputStream(String propertyFileName) throws Exception {
        InputStream returnValue;

        //find out if propertyFile is provided in src/test/jmeter
        File propertyFile = new File(this.propertySourceDirectory + File.separator + propertyFileName);
        if (propertyFile.exists()) {
            returnValue = new FileInputStream(propertyFile);
        }
        // TODO: handling global.properties separately because it is not delivered with the JMeter installation. There probably is a better way to handle this.
        // TODO: maybe the JMeter guys would be willing to put global.properties into the JMeter_config artifact for us since it is officially a way to configure JMeter?
        else if ("global.properties".equals(propertyFileName)) {
            returnValue = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFileName);
        } else {
            JarFile propertyJar = new JarFile(this.jmeterConfigArtifact.getFile());
            returnValue = propertyJar.getInputStream(propertyJar.getEntry("bin/" + propertyFileName));
        }

        return returnValue;
    }

    /**
     * TODO: not very happy with this solution, but we have to describe the mapping somehow.
     * TODO: Using an Enum like JMeterPropertiesFiles is no option since Enum instantiation is static and the Maps are not...
     *
     * @return mapping from properties file to properties map
     */
    private Map<String, Map<String, String>> getJmeterPropertyFileToPropertiesMapping() {

        Map<String, Map<String, String>> returnMap = new HashMap<String, Map<String, String>>();

        returnMap.put("jmeter.properties", this.jMeterProperties);
        returnMap.put("saveservice.properties", this.jMeterSaveServiceProperties);
        returnMap.put("upgrade.properties", this.jMeterUpgradeProperties);
        returnMap.put("system.properties", this.jMeterSystemProperties);
        returnMap.put("user.properties", this.jMeterUserProperties);
        returnMap.put("global.properties", this.jMeterGlobalProperties);

        return returnMap;
    }
}
