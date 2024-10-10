package com.lazerycode.jmeter.results;

import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public abstract class ResultScanner {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ResultScanner.class);
    protected final boolean countFailures;
    protected final boolean countSuccesses;
    protected final boolean onlyFailWhenMatchingFailureMessage;
    protected final List<String> failureMessages;
    protected int successCount = 0;
    protected int failureCount = 0;
    protected int customFailureCount = 0;

    public ResultScanner(boolean countSuccesses, boolean countFailures, boolean onlyFailWhenMatchingFailureMessage, List<String> failureMessages) {
        this.countFailures = countFailures;
        this.countSuccesses = countSuccesses;
        this.onlyFailWhenMatchingFailureMessage = onlyFailWhenMatchingFailureMessage;
        this.failureMessages = failureMessages;
    }

    abstract public void parseResultFile(File file) throws MojoExecutionException;

    /**
     * @return failure count
     */
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
     * @return success count
     */
    public int getSuccessCount() {
        if (countSuccesses) {
            return this.successCount;
        }
        return 0;
    }

    public int getTotalCount() {
        return getSuccessCount() + getFailureCount();
    }
}
