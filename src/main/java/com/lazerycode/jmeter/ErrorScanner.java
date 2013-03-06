package com.lazerycode.jmeter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
public class ErrorScanner {

    //TODO: where would such an element come from?
    private static final String ERROR_ELEMENT = "<error>true</error>";
    //TODO: where would such an element come from?
    private static final String FAILURE_ELEMENT = "<failure>true</failure>";

    /**
     * Element is added by JMeter if request is deemed as failed
     */
    private static final String REQUEST_FAILURE_ELEMENT = "s=\"false\"";

    private boolean ignoreErrors;
    private boolean ignoreFailures;
    private Log log;

    private int failureCount = 0;
    private int errorCount = 0;

    public ErrorScanner(boolean ignoreErrors, boolean ignoreFailures, Log log) {
        this.ignoreErrors = ignoreErrors;
        this.ignoreFailures = ignoreFailures;
        this.log = log;
    }

    /**
     * Check given file for errors
     *
     * @param file File to parse for errors
     * @return true of file doesn't contain errors
     * @throws MojoExecutionException
     */
    public boolean hasTestPassed(File file) throws MojoExecutionException {
        resetErrorAndFailureCount();
        //If we are ignoring errors/failures just return a pass without parsing the results file.
        if (this.ignoreErrors && this.ignoreFailures) return true;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                this.checkLineForErrors(line);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read test results file " + file, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("Error closing input stream", e);
                }
            }
        }

        return this.errorCount == 0 && this.failureCount == 0;
    }

    /**
     * @return failureCount
     */
    public int getFailureCount() {
        return this.failureCount;
    }

    /**
     * TODO: what are errors?
     *
     * @return errorCount
     */
    public int getErrorCount() {
        return this.errorCount;
    }

    // ---------------------------------------------------------

    /**
     * protected for testing
     *
     * @param line String
     * @return boolean
     */
    protected boolean checkLineForErrors(String line) {
        boolean lineContainsError = false;
        if (line.contains(ERROR_ELEMENT)) {
            if (!this.ignoreErrors) {
                this.errorCount++;
                lineContainsError = true;
            }
        }
        if (line.contains(FAILURE_ELEMENT) || line.contains(REQUEST_FAILURE_ELEMENT)) {
            if (!this.ignoreFailures) {
                this.failureCount++;
                lineContainsError = true;
            }
        }
        return lineContainsError;
    }

    private void resetErrorAndFailureCount() {
        this.failureCount = 0;
        this.errorCount = 0;
    }
}
