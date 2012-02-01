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
     * Merge properties from sourceFile and customProperties into the given outputDirectory
     *
     * @param sourceFile
     * @param outputDirectory
     * @param propertiesFilename
     * @param propertiesMap
     * @throws MojoExecutionException
     */
    public static void mergePropertiesFile(InputStream sourceFile, File outputDirectory, String propertiesFilename, Map<String, String> propertiesMap) throws MojoExecutionException {
        //Drop out right away if there is nothing to create, source file will never be null if the properties file is required.
        if(sourceFile == null && propertiesMap == null) return;
        Properties baseProperties = new Properties();
        try {
            if (sourceFile != null) {
                //Read in base properties
                baseProperties.load(sourceFile);
                sourceFile.close();
            }
            //Create final properties set
            Properties modifiedProperties = mergeProperties(baseProperties, propertiesMap);
            //Write out final properties file.
            FileOutputStream writeOutFinalPropertiesFile = new FileOutputStream(new File(outputDirectory.getCanonicalFile() + File.separator + propertiesFilename));
            modifiedProperties.store(writeOutFinalPropertiesFile, null);
            writeOutFinalPropertiesFile.flush();
            writeOutFinalPropertiesFile.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating consolidated properties file " + propertiesFilename + ": " + e);
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
                //TODO check to see if property being set is a close match to an existing property and warn user if it is e.g. have they set User.dir instead of user.dir
                //TODO remove any reserved properties
                properties.setProperty(key, customProperties.get(key));
            }
        }
        return properties;
    }
}
