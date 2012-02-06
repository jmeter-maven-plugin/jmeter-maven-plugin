package com.lazerycode.jmeter;

import org.apache.maven.plugin.MojoFailureException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
/**
 * Handles checking the jmeter xml logs for errors and failures.
 *
 * @author Jon Roberts
 */
public class ErrorScanner {

    private static final String ERROR_ELEMENT = "<error>true</error>";
    private static final String REQUEST_FAILURE_ELEMENT = "s=\"false\"";
    private static final String FAILURE_ELEMENT = "<failure>true</failure>";

    private boolean ignoreErrors;
    private boolean ignoreFailures;
    private int failureCount = 0;
    private int errorCount = 0;

    /**
     * @param ignoreErrors
     * @param ignoreFailures
     */
    public ErrorScanner(boolean ignoreErrors, boolean ignoreFailures) {
        this.ignoreErrors = ignoreErrors;
        this.ignoreFailures = ignoreFailures;
    }

    public boolean hasTestPassed(File file) throws IOException {
        resetErrorAndFailureCount();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                this.checkLineForErrors(line);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        if (this.errorCount == 0 && this.failureCount == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getFailureCount() {
        return this.failureCount;
    }

    public int getErrorCount() {
        return this.errorCount;
    }

    // ---------------------------------------------------------

    /**
     * protected for testing
     *
     * @param line
     * @return boolean
     * @throws MojoFailureException
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

    // =========================================================

    private void resetErrorAndFailureCount() {
        this.failureCount = 0;
        this.errorCount = 0;
    }
}
