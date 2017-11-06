/**
 * 
 */
package com.lazerycode.jmeter.mojo;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;

/**
 * Holds configuration 
 */
public class JMeterConfigurationHolder {
    private static final JMeterConfigurationHolder INSTANCE = new JMeterConfigurationHolder();
    private String runtimeJarName;
    private File workingDirectory;
    private Map<ConfigurationFiles, PropertiesMapping> propertiesMap;
    
    private boolean configurationFreezed;

    /**
     * 
     */
    private JMeterConfigurationHolder() {
        super();
    }

    public static final JMeterConfigurationHolder getInstance() {
        return INSTANCE;
    }

    /**
     * @return the runtimeJarName
     */
    public String getRuntimeJarName() {
        return runtimeJarName;
    }

    /**
     * @param runtimeJarName the runtimeJarName to set
     */
    void setRuntimeJarName(String runtimeJarName) {
        if(configurationFreezed) {
            throw new IllegalStateException("setRuntimeJarName called while JMeter configuration already freezed");
        }
        this.runtimeJarName = runtimeJarName;
    }

    /**
     * @return the workingDirectory
     */
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * @param workingDirectory the workingDirectory to set
     */
    void setWorkingDirectory(File workingDirectory) {
        if(configurationFreezed) {
            throw new IllegalStateException("setWorkingDirectory called while JMeter configuration already freezed");
        }
        this.workingDirectory = workingDirectory;
    }

    /**
     * @return the propertiesMap
     */
    public Map<ConfigurationFiles, PropertiesMapping> getPropertiesMap() {
        return propertiesMap;
    }

    /**
     * @param propertiesMap the propertiesMap to set
     */
    void setPropertiesMap(Map<ConfigurationFiles, PropertiesMapping> propertiesMap) {
        if(configurationFreezed) {
            throw new IllegalStateException("setPropertiesMap called while JMeter configuration already freezed");
        }
        this.propertiesMap = Collections.unmodifiableMap(propertiesMap);
    }

    /**
     * Freeze configuration
     */
    void freezeConfiguration() {
        this.configurationFreezed = true;
    }
    
    /**
     * Allow to reset configuration
     */
    void resetConfiguration() {
        workingDirectory = null;
        runtimeJarName = null;
        propertiesMap = null;
        this.configurationFreezed = false;
    }
}
