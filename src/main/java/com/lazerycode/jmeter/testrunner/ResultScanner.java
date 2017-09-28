package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.exceptions.ResultsFileNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
public class ResultScanner {

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
	 * @throws ResultsFileNotFoundException
	 */
	public void parseResultFile(File file) throws ResultsFileNotFoundException {
		if (countFailures) {
			failureCount = failureCount + scanFileForPattern(file, REQUEST_FAILURE);
		}
		if (countSuccesses) {
			successCount = successCount + scanFileForPattern(file, REQUEST_SUCCESS);
		}
	}

	/**
	 * Parse a file for instances of a pattern
	 *
	 * @param file    The file to parse
	 * @param pattern The pattern to look for
	 * @return The number of times the pattern has been found
	 * @throws ResultsFileNotFoundException
	 */
	private int scanFileForPattern(File file, String pattern) throws ResultsFileNotFoundException {
		int patternCount = 0;
		try (Scanner resultFileScanner = new Scanner(file)) {
			while (resultFileScanner.findWithinHorizon(pattern, 0) != null) {
				patternCount++;
			}
		} catch (FileNotFoundException ex) {
			throw new ResultsFileNotFoundException("File not found for file:"
			        +file.getAbsolutePath()
			        +", pattern:"
			        +pattern, ex);
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
