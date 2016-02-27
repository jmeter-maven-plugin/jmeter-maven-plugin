package com.lazerycode.jmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
class ResultScanner {

	private static final String REQUEST_FAILURE = "s=\"false\"";
	private static final String REQUEST_SUCCESS = "s=\"true\"";
	private final boolean countFailures;
	private final boolean countSuccesses;
	private int failureCount = 0;
	private int successCount = 0;

	public ResultScanner(boolean countSuccesses, boolean countFailures) {
		this.countFailures = countFailures;
		this.countSuccesses = countSuccesses;
	}

	/**
	 * Work out how to parse the file (if at all)
	 *
	 * @param file File to parse
	 * @throws IOException
	 */
	public void parseResultFile(File file) throws IOException {
		if (countFailures) {
			failureCount = failureCount + scanFileforPattern(file, REQUEST_FAILURE);
		}
		if (countSuccesses) {
			successCount = successCount + scanFileforPattern(file, REQUEST_SUCCESS);
		}
	}

	/**
	 * Parse a file for instances of a pattern
	 *
	 * @param file    The file to parse
	 * @param pattern The pattern to look for
	 * @return The number of times the pattern has been found
	 * @throws FileNotFoundException
	 */
	private int scanFileforPattern(File file, String pattern) throws FileNotFoundException {
		int patternCount = 0;
		try (Scanner resultFileScanner = new Scanner(file)) {
			while (resultFileScanner.findWithinHorizon(pattern, 0) != null) {
				patternCount++;
			}
		}

		return patternCount;
	}

	/**
	 * @return failureCount
	 */
	public int getFailureCount() {
		return this.failureCount;
	}

	/**
	 * @return failureCount
	 */
	public int getSuccessCount() {
		return this.successCount;
	}
}
