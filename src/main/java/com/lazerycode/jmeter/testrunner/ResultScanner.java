package com.lazerycode.jmeter.testrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.lazerycode.jmeter.exceptions.IOException;
import com.lazerycode.jmeter.exceptions.ResultsFileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
public class ResultScanner implements IResultScanner {
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
	private static final CsvMapper CSV_MAPPER = new CsvMapper();

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
	 * @throws IOException
	 */
	public void parseResultFile(File file)
			throws ResultsFileNotFoundException, IOException {
	    String failurePattern = this.csv ? CSV_REQUEST_FAILURE : XML_REQUEST_FAILURE;
	    String successPattern = this.csv ? CSV_REQUEST_SUCCESS : XML_REQUEST_SUCCESS;
	    LOGGER.info("Parsing results file '{}' in format '{}', using failurePattern:'{}', successPattern:'{}'",
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
	 * @throws IOException
	 */
	private int scanFileForPattern(File file, String pattern)
			throws ResultsFileNotFoundException, IOException { // NOSONAR
		int patternCount = 0;
		LOGGER.debug("Scanning file '{}' using pattern '{}'", file.getAbsolutePath(), pattern);
		try {
			if (csv)
				patternCount = scanCsvForPattern(file, pattern);
			else
				patternCount = scanXmlForPattern(file, pattern);
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
	 * Scans a csv file for the given pattern and returns the number of times
	 * the pattern appears in the success column. This function assumes that the
	 * csv will always include the header row.
	 * @param file    The file to parse
	 * @param pattern The pattern to look for
	 * @return The number of times the pattern appears in the success column
	 * @throws IOException When an error occurs while reading the file
	 */
	private int scanCsvForPattern(File file, String pattern) throws IOException {
		CsvSchema schema = CsvSchema.emptySchema().withHeader();
		int bufferSize = 1024 * 1024;
		int patternCount = 0;

		try (FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader reader = new BufferedReader(isr, bufferSize)) {

			MappingIterator<Map<String, String>> it = CSV_MAPPER.readerFor(Map.class)
					.with(schema)
					.readValues(reader);
					
			while (it.hasNext()) {
				Map<String, String> row = it.next();
				String success = row.get("success");
				if (success != null && success.matches(pattern))
					patternCount++;
			}
		} catch (java.io.IOException e) {
			String message = "An unexpected error occured while reading file "
					+ file.getAbsolutePath();
			throw new IOException(message, e);
		}
		return patternCount;
	}

	/**
	 * Scans an xml file for the given pattern and returns the number of times the
	 * pattern appears in the xml.
	 * @param file    The file to parse
	 * @param pattern The pattern to look for
	 * @return The number of times the pattern appears in the xml file
	 * @throws FileNotFoundException When the file is not found
	 */
	private int scanXmlForPattern(File file, String pattern) 
			throws FileNotFoundException {
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
	@Override
    public int getFailureCount() {
		return this.failureCount;
	}

	/**
	 * @return failureCount
	 */
	@Override
    public int getSuccessCount() {
		return this.successCount;
	}
}
