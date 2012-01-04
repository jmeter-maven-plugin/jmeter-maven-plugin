package org.apache.jmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.plugin.MojoFailureException;

/**
 * Handles checking the jmeter xml logs for errors and failures.
 *
 * @author Jon Roberts
 */
public class ErrorScanner {

    private static final String PAT_ERROR = "<error>true</error>";
    private static final String PAT_FAILURE_REQUEST = "s=\"false\"";
    private static final String PAT_FAILURE = "<failure>true</failure>";

    private boolean ignoreErrors;
    private boolean ignoreFailures;
    private int failureCount = 0;
    private int errorCount = 0;

    /**
     * @param ignoreErrors   if an error is found with this scanner it will throw an
     *                       exception instead of returning true;
     * @param ignoreFailures if a failure is found with this scanner it will throw an
     *                       exception instead of returning true;
     */
    public ErrorScanner(boolean ignoreErrors, boolean ignoreFailures) {
        this.ignoreErrors = ignoreErrors;
        this.ignoreFailures = ignoreFailures;
    }

    private void resetErrorAndFailureCount(){
        this.failureCount = 0;
        this.errorCount = 0;
    }
    
    public boolean hasTestPassed(File file) throws IOException {
        resetErrorAndFailureCount();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                this.lineContainsForErrors(line);
            }
        } finally {
            in.close();
        }
        if (this.errorCount == 0 && this.failureCount == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * protected for testing
     *
     * @param line
     * @return
     * @throws MojoFailureException
     */
    protected boolean lineContainsForErrors(String line) {
        boolean lineHasProblems = false;
        if (line.contains(PAT_ERROR)) {
            if (!this.ignoreErrors) {
                this.errorCount++;
                lineHasProblems = true;
            }
        }
        if (line.contains(PAT_FAILURE) || line.contains(PAT_FAILURE_REQUEST)) {
            if (!this.ignoreFailures) {
                this.failureCount++;
                lineHasProblems = true;
            }
        }
        return lineHasProblems;
    }

    public int getFailureCount() {
        return this.failureCount;
    }

    public int getErrorCount() {
        return this.errorCount;
    }
}
