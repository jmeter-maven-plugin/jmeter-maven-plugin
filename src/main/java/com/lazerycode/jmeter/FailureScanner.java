package com.lazerycode.jmeter;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
class FailureScanner {

	private static final String REQUEST_FAILURE = "s=\"false\"";
	private static final Pattern ERROR_PATTERN = Pattern.compile(REQUEST_FAILURE);
	private static final String REQUEST_SUCCESS = "s=\"true\"";
	private static final Pattern SUCCESS_PATTERN = Pattern.compile(REQUEST_SUCCESS);

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

		Scanner resultFileScanner;
		resultFileScanner = new Scanner(file);
		while(resultFileScanner.hasNextLine()) {
			String line = resultFileScanner.nextLine();
			//optimistic: assume that there are more successes than failures on average and scan for success first 
			if(SUCCESS_PATTERN.matcher(line).find()) {
				successCount++;
			} else if(ERROR_PATTERN.matcher(line).find()) {
				failureCount++;
			}
		}
		resultFileScanner.close();
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
