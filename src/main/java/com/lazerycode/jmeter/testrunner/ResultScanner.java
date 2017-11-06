package com.lazerycode.jmeter.testrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lazerycode.jmeter.exceptions.ResultsFileNotFoundException;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
public class ResultScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultScanner.class);

    private static final String CSV_REQUEST_FAILURE = "\\bfalse\\b";
    private static final String CSV_REQUEST_SUCCESS = "\\btrue\\b";

	private static final String XML_REQUEST_FAILURE = "s=\"false\"";
	private static final String XML_REQUEST_SUCCESS = "s=\"true\"";
	private final boolean countFailures;
	private final boolean countSuccesses;
	private int failureCount = 0;
	private int successCount = 0;
    private boolean csv;

	public ResultScanner(boolean countSuccesses, boolean countFailures, boolean isCsv) {
        this.countFailures = countFailures;
        this.countSuccesses = countSuccesses;
        this.csv = isCsv;
    }
	
	public ResultScanner(boolean countSuccesses, boolean countFailures) {
	    this(countSuccesses, countFailures, false);
	}

	/**
	 * Work out how to parse the file (if at all)
	 *
	 * @param file File to parse
	 * @throws ResultsFileNotFoundException
	 */
	public void parseResultFile(File file) throws ResultsFileNotFoundException {
	    String failurePattern = this.csv ? CSV_REQUEST_FAILURE : XML_REQUEST_FAILURE;
	    String successPattern = this.csv ? CSV_REQUEST_SUCCESS : XML_REQUEST_SUCCESS;
	    LOGGER.info("Parsing results file '{}' in format '{}' using failurePattern:'{}', successPattern:'{}'",
	            file,
	            this.csv ? "CSV" : "XML", failurePattern, successPattern);
		if (countFailures) {
			failureCount = failureCount + scanFileForPattern(file, failurePattern);
		}
		if (countSuccesses) {
			successCount = successCount + scanFileForPattern(file, successPattern);
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
	private int scanFileForPattern(File file, String pattern) 
	        throws ResultsFileNotFoundException { // NOSONAR
		int patternCount = 0;
		LOGGER.debug("Scanning file '{}' using pattern '{}'", file.getAbsolutePath(), pattern);
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
        LOGGER.debug("Scanned file '{}' using pattern '{}', result:'{}'", file.getAbsolutePath(), pattern, patternCount);

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
