package com.lazerycode.jmeter.results;

import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.lazerycode.jmeter.results.XMLFileScanner.scanXmlFileForPattern;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
public class ResultScanner implements IResultScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultScanner.class);
    private static final String XML_REQUEST_FAILURE_PATTERN = "s=\"false\"";
    private static final String XML_REQUEST_SUCCESS_PATTERN = "s=\"true\"";
    private final boolean countFailures;
    private final boolean countSuccesses;
    private final boolean onlyFailWhenMatchingFailureMessage;
    private final boolean csv;
    private final List<String> failureMessages;
    private int successCount = 0;
    private int failureCount = 0;
    private int customFailureCount = 0;

    public ResultScanner(boolean countSuccesses, boolean countFailures, boolean isCsv, boolean onlyFailWhenMatchingFailureMessage, List<String> failureMessages) {
        this.csv = isCsv;
        this.countFailures = countFailures;
        this.countSuccesses = countSuccesses;
        this.onlyFailWhenMatchingFailureMessage = onlyFailWhenMatchingFailureMessage;
        this.failureMessages = failureMessages;
    }

    /**
     * Work out how to parse the file (if at all)
     *
     * @param file File to parse
     * @throws MojoExecutionException MojoExecutionException
     */
    public void parseResultFile(File file) throws MojoExecutionException {
        if (!file.exists()) {
            throw new MojoExecutionException("Unable to find " + file.getAbsolutePath());
        }
        LOGGER.info(" ");
        LOGGER.info("Parsing results file '{}' as type: {}", file, this.csv ? "CSV" : "XML");
        if (csv) {
            CSVScanResult csvScanResult = CSVFileScanner.scanCsvForValues(file, failureMessages);
            successCount = csvScanResult.getSuccessCount();
            failureCount = csvScanResult.getFailureCount();
            for (Map.Entry<String, Integer> entry : csvScanResult.getSpecificFailureMessages().entrySet()) {
                customFailureCount = customFailureCount + entry.getValue();
                LOGGER.info("Number of potential custom failures using '{}' in '{}': {}", entry.getKey(), file.getName(), customFailureCount);
            }
        } else {
            if (countSuccesses) {
                successCount = scanXmlFileForPattern(file, Pattern.compile(XML_REQUEST_SUCCESS_PATTERN, Pattern.CASE_INSENSITIVE));
            }
            if (countFailures) {
                failureCount = scanXmlFileForPattern(file, Pattern.compile(XML_REQUEST_FAILURE_PATTERN, Pattern.CASE_INSENSITIVE));
            }
        }
    }

    /**
     * @return failureCount
     */
    @Override
    public int getFailureCount() {
        if (countFailures) {
            if (onlyFailWhenMatchingFailureMessage) {
                return this.customFailureCount;
            } else {
                return this.failureCount;
            }
        }
        return 0;
    }

    /**
     * @return failureCount
     */
    @Override
    public int getSuccessCount() {
        if (countSuccesses) {
            return this.successCount;
        }
        return 0;
    }

    @Override
    public int getTotalCount() {
        return getSuccessCount() + getFailureCount();
    }
}
