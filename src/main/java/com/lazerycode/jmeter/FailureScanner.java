package com.lazerycode.jmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
class FailureScanner {

	private static final String REQUEST_FAILURE_PATTERN = "s=\"false\"";
	private static final String REQUEST_SUCCESS_PATTERN = "s=\"true\"";
	private final boolean ignoreFailures;
	private int failureCount;
	private int successCount;

	public FailureScanner(boolean ignoreFailures) {
		this.ignoreFailures = ignoreFailures;
	}

	/**
	 * Check file for errors
	 *
	 * @return false if file doesn't contain failures
	 */
	public boolean hasTestFailed() {
		return !this.ignoreFailures && this.failureCount > 0;
	}

	/**
	 * Parse given results file
	 *
	 * @param file File to parse for results
	 * @throws IOException
	 */
	public void parseResults(File file) throws IOException {

		failureCount = 0;
		successCount = 0;

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains(REQUEST_FAILURE_PATTERN)) {
				this.failureCount++;
			} else if (line.contains(REQUEST_SUCCESS_PATTERN)) {
				this.successCount++;
			}
		}
		br.close();
	}

	/**
	 * @return failureCount
	 */
	public int getFailureCount() {
		if (this.ignoreFailures) {
			return 0;
		} else {
			return this.failureCount;
		}
	}
	
	public int getRequestCount() {
		return this.failureCount + this.successCount;
	}
}
