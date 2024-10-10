package com.lazerycode.jmeter.results;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
public class ResultScannerXML extends ResultScanner {

    private static final String XML_REQUEST_FAILURE_PATTERN = "s=\"false\"";
    private static final String XML_REQUEST_SUCCESS_PATTERN = "s=\"true\"";

    public ResultScannerXML(boolean countSuccesses, boolean countFailures, boolean onlyFailWhenMatchingFailureMessage, List<String> failureMessages) {
        super(countSuccesses, countFailures, onlyFailWhenMatchingFailureMessage, failureMessages);
    }

    /**
     * Work out how to parse an XML file
     *
     * @param file File to parse
     * @throws MojoExecutionException MojoExecutionException
     */
    @Override
    public void parseResultFile(File file) throws MojoExecutionException {
        if (!file.exists()) {
            throw new MojoExecutionException("Unable to find " + file.getAbsolutePath());
        }
        LOGGER.info(" ");
        LOGGER.info("Parsing results file '{}' as type: XML", file);
        if (countSuccesses) {
            successCount += scanXmlFileForPattern(file, Pattern.compile(XML_REQUEST_SUCCESS_PATTERN, Pattern.CASE_INSENSITIVE));
        }
        if (countFailures) {
            failureCount += scanXmlFileForPattern(file, Pattern.compile(XML_REQUEST_FAILURE_PATTERN, Pattern.CASE_INSENSITIVE));
        }
    }

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
