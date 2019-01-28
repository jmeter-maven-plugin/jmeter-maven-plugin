package com.lazerycode.jmeter.testrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.lazerycode.jmeter.exceptions.IOException;
import com.lazerycode.jmeter.exceptions.ResultsFileNotFoundException;

/**
 * Handles checking a JMeter results file in XML format for errors and failures.
 *
 * @author Jon Roberts
 */
public class ResultScanner implements IResultScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultScanner.class);

    private static final String CSV_REQUEST_FAILURE = "false";
    private static final String CSV_REQUEST_SUCCESS = "true";

	private static final String XML_REQUEST_FAILURE_PATTERN = "s=\"false\"";
	private static final String XML_REQUEST_SUCCESS_PATTERN = "s=\"true\"";
	private final boolean countFailures;
	private final boolean countSuccesses;
	private int failureCount = 0;
	private int successCount = 0;
	private boolean csv;
	private static final CsvMapper CSV_MAPPER = new CsvMapper();

    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

	public ResultScanner(boolean countSuccesses, boolean countFailures, boolean isCsv) {
        this.countFailures = countFailures;
        this.countSuccesses = countSuccesses;
        this.csv = isCsv;
    }
	
	public ResultScanner(boolean countSuccesses, boolean countFailures) {
	    this(countSuccesses, countFailures, false);
	}

    private static char lookForDelimiter(String line) {
        for (char ch : line.toCharArray()) {
            if (!Character.isLetter(ch)) {
                return ch;
            }
        }
        throw new IllegalStateException("Cannot find delimiter in header " + line);
    }
    
	/**
	 * Work out how to parse the file (if at all)
	 *
	 * @param file File to parse
	 * @throws ResultsFileNotFoundException
	 * @throws IOException
	 */
	public void parseResultFile(File file)
			throws IOException {
        LOGGER.info("Parsing results file '{}' in format '{}'",
                file,
                this.csv ? "CSV" : "XML");

	    if (countFailures) {
		    if(csv) {
		        failureCount = failureCount + scanCsvForValue(file, CSV_REQUEST_FAILURE);
		    } else {
		        failureCount = failureCount + scanXmlForPattern(file, XML_REQUEST_FAILURE_PATTERN);
		    }
		    LOGGER.info("Scanned file '{}', number of results in failure:'{}'", file.getAbsolutePath(), failureCount);
		}
		if (countSuccesses) {
		    if(csv) {
		        successCount = successCount + scanCsvForValue(file, CSV_REQUEST_SUCCESS);
            } else {
                successCount = successCount + scanXmlForPattern(file, XML_REQUEST_SUCCESS_PATTERN);
            }
		    LOGGER.info("Scanned file '{}', number of results in success:'{}'", file.getAbsolutePath(), successCount);
		}
	}

	/**
	 * Scans a csv file for the given pattern and returns the number of times
	 * the pattern appears in the success column. This function assumes that the
	 * csv will always include the header row.
	 * @param file    The file to parse
	 * @param searchedForValue The pattern to look for
	 * @return The number of times the pattern appears in the success column
	 * @throws IOException When an error occurs while reading the file
	 */
	private int scanCsvForValue(File file, String searchedForValue) throws IOException {
		int numberOfMatches = 0;
		try {
	        char separator = computeSeparator(file);
	        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(separator);
    		try (FileReader fr = new FileReader(file);
    				BufferedReader reader = new BufferedReader(fr, DEFAULT_BUFFER_SIZE)) {
    			MappingIterator<Map<String, String>> it = CSV_MAPPER.readerFor(Map.class)
    					.with(schema)
    					.readValues(reader);
    			while (it.hasNext()) {
    				Map<String, String> row = it.next();
    				String successValue = row.get("success");
    				if (searchedForValue.equals(successValue)) {
    				    numberOfMatches++;
    				}
    			}
    		} 
		} catch (java.io.IOException e) {
            throw new IOException("An unexpected error occured while reading file "
                    + file.getAbsolutePath(), e);
        }
		return numberOfMatches;
	}

	private char computeSeparator(File file) throws java.io.IOException {
	    try (FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr, DEFAULT_BUFFER_SIZE)) {
	        String line = reader.readLine();
	        if(line != null) {
	            return lookForDelimiter(line);
	        }
	        throw new IllegalArgumentException("No line read from file "+file.getAbsolutePath());
	    }
    }

    /**
	 * Scans an xml file for the given pattern and returns the number of times the
	 * pattern appears in the xml.
	 * @param file    The file to parse
	 * @param patternAsString The pattern to look for
	 * @return The number of times the pattern appears in the xml file
	 * @throws IOException When the file is not found
	 */
	private int scanXmlForPattern(File file, String patternAsString) 
			throws IOException {
		int patternCount = 0;
		Pattern pattern = Pattern.compile(patternAsString);
		try (Scanner resultFileScanner = new Scanner(file)) {
			while (resultFileScanner.findWithinHorizon(pattern, 0) != null) {
				patternCount++;
			}
		} catch (java.io.IOException e) {
            throw new IOException("An unexpected error occured while reading file "
                    + file.getAbsolutePath(), e);
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
