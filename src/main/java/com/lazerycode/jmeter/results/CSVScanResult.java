package com.lazerycode.jmeter.results;

import java.util.Map;

public class CSVScanResult {
    private final Map<String, Integer> specificFailureMessages;
    private final int successCount;
    private final int failureCount;

    public CSVScanResult(Map<String, Integer> specificFailureMessages, int successCount, int failureCount) {
        this.specificFailureMessages = specificFailureMessages;
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    public Map<String, Integer> getSpecificFailureMessages() {
        return specificFailureMessages;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }
}
