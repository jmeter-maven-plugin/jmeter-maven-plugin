/**
 *
 */
package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;

import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 * Holds configuration 
 */
public class JMeterConfigurationHolder {
    private static final JMeterConfigurationHolder INSTANCE = new JMeterConfigurationHolder();
    private String runtimeJarName;
    private File workingDirectory;
    private Map<ConfigurationFiles, PropertiesMapping> propertiesMap;

    private boolean configurationFrozen;

    /**
     *
     */
    private JMeterConfigurationHolder() {
        super();
    }

    public static JMeterConfigurationHolder getInstance() {
        return INSTANCE;
    }

    /**
     * @return the runtimeJarName
     */
    String getRuntimeJarName() {
        return runtimeJarName;
    }

    /**
     * @param runtimeJarName the runtimeJarName to set
     */
    void setRuntimeJarName(String runtimeJarName) {
        if (configurationFrozen) {
            throw new IllegalStateException("setRuntimeJarName called while JMeter configuration already frozen");
        }
        this.runtimeJarName = runtimeJarName;
    }

    /**
     * @return the workingDirectory
     */
    File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * @param workingDirectory the workingDirectory to set
     */
    void setWorkingDirectory(File workingDirectory) {
        if (configurationFrozen) {
            throw new IllegalStateException("setWorkingDirectory called while JMeter configuration already frozen");
        }
        this.workingDirectory = workingDirectory;
    }

    /**
     * @return the propertiesMap
     */
    Map<ConfigurationFiles, PropertiesMapping> getPropertiesMap() {
        return propertiesMap;
    }

    /**
     * @param propertiesMap the propertiesMap to set
     */
    void setPropertiesMap(Map<ConfigurationFiles, PropertiesMapping> propertiesMap) {
        if (configurationFrozen) {
            throw new IllegalStateException("setPropertiesMap called while JMeter configuration already frozen");
        }
        this.propertiesMap = Collections.unmodifiableMap(propertiesMap);
    }

    /**
     * Freeze configuration
     */
    void freezeConfiguration() {
        this.configurationFrozen = true;
    }
    
    public boolean isFreezed() {
        return configurationFrozen;
    }

    /**
     * Allow to reset configuration
     */
    void resetConfiguration() {
        workingDirectory = null;
        runtimeJarName = null;
        propertiesMap = null;
        this.configurationFrozen = false;
    }
}
