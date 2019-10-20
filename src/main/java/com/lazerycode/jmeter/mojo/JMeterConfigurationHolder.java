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
    private File workingDirectory;
    private Map<ConfigurationFiles, PropertiesMapping> propertiesMap;

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
     * @return the workingDirectory
     */
    File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * @param workingDirectory the workingDirectory to set
     */
    void setWorkingDirectory(File workingDirectory) {
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
        this.propertiesMap = Collections.unmodifiableMap(propertiesMap);
    }
}
