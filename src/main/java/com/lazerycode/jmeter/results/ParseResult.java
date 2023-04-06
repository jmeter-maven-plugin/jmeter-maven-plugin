package com.lazerycode.jmeter.results;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lazerycode.jmeter.results.XMLFileScanner.scanXmlFileForPattern;

public class ParseResult {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultScanner.class);
    private static final String XML_REQUEST_FAILURE_PATTERN = "s=\"false\"";
    private static final String XML_REQUEST_SUCCESS_PATTERN = "s=\"true\"";
    private final boolean csv;
    private final List<String> failureMessages;
    private int successCount = 0;
    private int failureCount = 0;
    private int customFailureCount = 0;
    private final boolean countSuccesses;
    private final boolean countFailures;

    public ParseResult(boolean isCsv, List<String> failureMessages, boolean countSuccesses, boolean countFailures){
        this.csv = isCsv;
        this.failureMessages = failureMessages;
        this.countFailures = countFailures;
        this.countSuccesses = countSuccesses;
    }

    /**
     * Work out how to parse the file (if at all)
     *
     * @param file File to parse
     * @return
     * @throws MojoExecutionException MojoExecutionException
     */
    public List<Integer> parseResultFile(File file) throws MojoExecutionException {
        if (!file.exists()) {
            throw new MojoExecutionException("Unable to find " + file.getAbsolutePath());
        }
        LOGGER.info(" ");
        LOGGER.info("Parsing results file '{}' as type: {}", file, this.csv ? "CSV" : "XML");
        if (csv) {
            CSVScanResult csvScanResult = CSVFileScanner.scanCsvForValues(file, failureMessages);
            successCount += csvScanResult.getSuccessCount();
            failureCount += csvScanResult.getFailureCount();
            for (Map.Entry<String, Integer> entry : csvScanResult.getSpecificFailureMessages().entrySet()) {
                customFailureCount = customFailureCount + entry.getValue();
                LOGGER.info("Number of potential custom failures using '{}' in '{}': {}", entry.getKey(), file.getName(), customFailureCount);
            }
        } else {
            if (countSuccesses) {
                successCount += scanXmlFileForPattern(file, Pattern.compile(XML_REQUEST_SUCCESS_PATTERN, Pattern.CASE_INSENSITIVE));
            }
            if (countFailures) {
                failureCount += scanXmlFileForPattern(file, Pattern.compile(XML_REQUEST_FAILURE_PATTERN, Pattern.CASE_INSENSITIVE));
            }
        }
        return Arrays.asList(successCount,failureCount);
    }
}
