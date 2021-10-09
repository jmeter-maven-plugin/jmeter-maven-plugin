package com.lazerycode.jmeter.results;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class XMLFileScanner {

    /**
     * Scans a xml file for a given pattern.
     *
     * @param file          The file to parse
     * @param searchPattern The pattern to scan for
     * @return The number of times the pattern appears in the xml file
     * @throws MojoExecutionException When an error occurs while reading the file
     */
    public static int scanXmlFileForPattern(File file, Pattern searchPattern) throws MojoExecutionException {
        int patternMatchCount = 0;
        try (Scanner resultFileScanner = new Scanner(file)) {
            while (resultFileScanner.findWithinHorizon(searchPattern, 0) != null) {
                patternMatchCount++;
            }
        } catch (IOException e) {
            throw new MojoExecutionException("An unexpected error occurred while reading file " + file.getAbsolutePath(), e);
        }
        return patternMatchCount;
    }
}
