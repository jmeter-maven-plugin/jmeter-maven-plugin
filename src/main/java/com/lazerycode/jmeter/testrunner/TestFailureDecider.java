package com.lazerycode.jmeter.testrunner;

/**
 * Since 3.8.1
 *
 */
public class TestFailureDecider {

    private IResultScanner resultScanner;
    private boolean ignoreResultFailures;
    private float errorPercentageThreshold;
    private float errorPercentage;
    private boolean checkRan;
    
    public TestFailureDecider(boolean ignoreResultFailures, float errorPercentageThreshold, IResultScanner resultScanner) {
        this.ignoreResultFailures = ignoreResultFailures;
        this.errorPercentageThreshold = errorPercentageThreshold;
        this.resultScanner = resultScanner;
    }

    public boolean failBuild() {
        if(!checkRan) {
            throw new IllegalStateException("You need to call runChecks");
        }
        return !ignoreResultFailures &&  errorPercentage > errorPercentageThreshold;
    }

    public void runChecks() {
        this.errorPercentage = (float)resultScanner.getFailureCount() / 
                (float)(resultScanner.getSuccessCount() + resultScanner.getFailureCount()) * 100;
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
    public float getErrorPercentageThreshold() {
        return errorPercentageThreshold;
    }

    /**
     * @return the errorPercentage
     */
    public float getErrorPercentage() {
        return errorPercentage;
    }
    
}
