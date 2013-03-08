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
public class FailureScanner {

	private static final String REQUEST_FAILURE_PATTERN = "s=\"false\"";
	private final boolean ignoreFailures;
	private int failureCount;

	public FailureScanner(boolean ignoreFailures) {
		this.ignoreFailures = ignoreFailures;
	}

	/**
	 * Check given file for errors
	 *
	 * @param file File to parse for failures
	 * @return true if file doesn't contain failures
	 * @throws IOException
	 */
	public boolean hasTestFailed(File file) throws IOException {
		if (this.ignoreFailures) return false;
		failureCount = 0;
		Scanner resultFileScanner;
		Pattern errorPattern = Pattern.compile(REQUEST_FAILURE_PATTERN);
		resultFileScanner = new Scanner(file);
		while (resultFileScanner.findWithinHorizon(errorPattern, 0) != null) {
			failureCount++;
		}
		resultFileScanner.close();

		return this.failureCount > 0;
	}

	/**
	 * @return failureCount
	 */
	public int getFailureCount() {
		return this.failureCount;
	}
}
