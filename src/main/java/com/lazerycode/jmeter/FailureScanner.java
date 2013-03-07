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
public class FailureScanner {

    /**
     * Element is added by JMeter if request is deemed as failed
     */
    private static final String REQUEST_FAILURE_ELEMENT = "s=\"false\"";

    private final boolean ignoreFailures;
    private final Log log;

    private int failureCount = 0;

    public FailureScanner(boolean ignoreFailures, Log log) {
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
        //If we are ignoring failures just return a pass without parsing the results file.
        if (this.ignoreFailures) return true;
        resetFailureCount();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                this.checkLineForFailures(line);
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

        return this.failureCount == 0;
    }

    /**
     * @return failureCount
     */
    public int getFailureCount() {
        return this.failureCount;
    }

    /**
     * protected for testing
     *
     * @param line String
     * @return boolean
     */
    protected boolean checkLineForFailures(String line) {
        if (line.contains(REQUEST_FAILURE_ELEMENT)) {
            if (!this.ignoreFailures) {
                this.failureCount++;
                return true;
            }
        }
        return false;
    }

    protected void resetFailureCount() {
        this.failureCount = 0;
    }
}
