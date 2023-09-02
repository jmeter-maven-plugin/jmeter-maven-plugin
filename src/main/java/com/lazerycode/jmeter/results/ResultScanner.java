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
//    private static final Logger LOGGER = LoggerFactory.getLogger(ResultScanner.class);
//    private static final String XML_REQUEST_FAILURE_PATTERN = "s=\"false\"";
//    private static final String XML_REQUEST_SUCCESS_PATTERN = "s=\"true\"";
    private final boolean countFailures;
    private final boolean countSuccesses;
    private final boolean onlyFailWhenMatchingFailureMessage;
    private final boolean csv;
    private final List<String> failureMessages;
    private int successCount = 0;
    private int failureCount = 0;
    private int customFailureCount = 0;

    private ParseResult parseResult;

    public ResultScanner(boolean countSuccesses, boolean countFailures, boolean isCsv, boolean onlyFailWhenMatchingFailureMessage, List<String> failureMessages) {
        this.csv = isCsv;
        this.countFailures = countFailures;
        this.countSuccesses = countSuccesses;
        this.onlyFailWhenMatchingFailureMessage = onlyFailWhenMatchingFailureMessage;
        this.failureMessages = failureMessages;
        parseResult = new ParseResult();
    }


    /**
     * @return failureCount
     */
    public void getParseResult(File file) throws MojoExecutionException {
        List<Integer> vars =  parseResult.parseResultFile(file, successCount, failureCount, countSuccesses, countFailures, failureMessages, customFailureCount, csv);
        this.successCount = vars.get(0);
        this.failureCount = vars.get(1);
        System.out.println("SuccessCount2========================="+successCount);
        System.out.println("FailureCount2========================="+failureCount);
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
