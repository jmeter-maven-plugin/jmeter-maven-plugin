package com.lazerycode.jmeter.propertiesHandler;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class that can merge properties with property files
 */
public class PropertyFileMerger {

    /**
     * Merge properties from inputFile and customProperties into the given outputFile
     *
     * @param inputFile        File to read properties from
     * @param outputFile       File to write properties to
     * @param customProperties Map to merge properties from
     * @throws org.apache.maven.plugin.MojoExecutionException
     *
     */
    public static void mergePropertiesFile(InputStream inputFile, OutputStream outputFile, Map<String, String> customProperties) throws MojoExecutionException {

        // Read properties file.
        Properties properties = new Properties();
        try {
            properties.load(inputFile);
            Properties modifiedProperties = mergeProperties(properties, customProperties);
            // Write properties file.
            modifiedProperties.store(outputFile, null);
            //cleanup
            outputFile.flush();
            outputFile.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Error merging properties file" + e);
        }
    }

    /**
     * Merge given Map into given Properties object
     *
     * @param properties       object to merge the Map into
     * @param customProperties Map to merge into the Properties object
     * @return merged Properties object
     */
    protected static Properties mergeProperties(Properties properties, Map<String, String> customProperties) {

        if (customProperties != null && !customProperties.isEmpty()) {
            for (String key : customProperties.keySet()) {
                properties.setProperty(key, customProperties.get(key));
            }
        }
        return properties;
    }
}
