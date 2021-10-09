package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.results.IResultScanner;

/**
 * Since 3.8.1
 */
public class TestFailureDecider {

    private final IResultScanner resultScanner;
    private final boolean ignoreResultFailures;
    private final double errorPercentageThreshold;
    private double errorPercentage;
    private boolean checkRan;

    public TestFailureDecider(boolean ignoreResultFailures, double errorPercentageThreshold, IResultScanner resultScanner) {
        this.ignoreResultFailures = ignoreResultFailures;
        this.errorPercentageThreshold = errorPercentageThreshold;
        this.resultScanner = resultScanner;
    }

    public boolean failBuild() {
        if (!checkRan) {
            throw new IllegalStateException("You need to call runChecks");
        }
        return !ignoreResultFailures && errorPercentage > errorPercentageThreshold;
    }

    public void runChecks() {
        this.errorPercentage = (double) Math.round(resultScanner.getFailureCount() * 10000.0 / resultScanner.getTotalCount()) / 100;
        this.checkRan = true;
    }

    /**
     * @return the ignoreResultFailures
     */
    public boolean isIgnoreResultFailures() {
        return ignoreResultFailures;
    }

    /**
     * @return the errorPercentageThreshold
     */
    public double getErrorPercentageThreshold() {
        return errorPercentageThreshold;
    }

    /**
     * @return the errorPercentage
     */
    public double getErrorPercentage() {
        return errorPercentage;
    }

}
