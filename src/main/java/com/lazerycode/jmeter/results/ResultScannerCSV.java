package com.lazerycode.jmeter.results;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResultScannerCSV extends ResultScanner {

    private static final String ROW_NAME_SUCCESS = "success";
    private static final String ROW_NAME_FAILURE_MESSAGE = "failureMessage";
    private static final CsvMapper CSV_MAPPER = new CsvMapper();
    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    public ResultScannerCSV(boolean countSuccesses, boolean countFailures, boolean onlyFailWhenMatchingFailureMessage, List<String> failureMessages) {
        super(countSuccesses, countFailures, onlyFailWhenMatchingFailureMessage, failureMessages);
    }

    /**
     * Work out how to parse a CSV file
     *
     * @param file File to parse
     * @throws MojoExecutionException MojoExecutionException
     */
    @Override
    public void parseResultFile(File file) throws MojoExecutionException {
        if (!file.exists()) {
            throw new MojoExecutionException("Unable to find " + file.getAbsolutePath());
        }
        LOGGER.info(" ");
        LOGGER.info("Parsing results file '{}' as type: CSV", file);
        CSVScanResult csvScanResult = scanCsvForValues(file, failureMessages);
        successCount += csvScanResult.getSuccessCount();
        failureCount += csvScanResult.getFailureCount();
        for (Map.Entry<String, Integer> entry : csvScanResult.getSpecificFailureMessages().entrySet()) {
            customFailureCount = customFailureCount + entry.getValue();
            LOGGER.info("Number of potential custom failures using '{}' in '{}': {}", entry.getKey(), file.getName(), customFailureCount);
        }

    }

    /**
     * Scans a csv file to calculate success/failure counts.
     * Will also take a list of failure messages to search for
     *
     * @param file   The file to parse
     * @param values Failure messages to search for
     * @return A map of failure messages/associated count for failure messages explicitly searched for
     * @throws MojoExecutionException When an error occurs while reading the file
     */
    protected static CSVScanResult scanCsvForValues(File file, List<String> values) throws MojoExecutionException {
        Map<String, Integer> specificFailureMessages = values.stream().collect(Collectors.toMap(Function.identity(), (a) -> 0));
        int successCount = 0, failureCount = 0;
        try {
            char separator = computeSeparator(file);
            CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(separator);
            try (FileReader fr = new FileReader(file); BufferedReader reader = new BufferedReader(fr, DEFAULT_BUFFER_SIZE)) {
                MappingIterator<Map<String, String>> it = CSV_MAPPER.readerFor(Map.class).with(schema).readValues(reader);
                while (it.hasNext()) {
                    Map<String, String> row = it.next();
                    if (Boolean.parseBoolean(row.get(ROW_NAME_SUCCESS))) {
                        successCount++;
                    } else {
                        failureCount++;
                        for (Map.Entry<String, Integer> entry : specificFailureMessages.entrySet()) {
                            String failureMessage = row.get(ROW_NAME_FAILURE_MESSAGE);
                            if (entry.getKey().equalsIgnoreCase(failureMessage)) {
                                entry.setValue(entry.getValue() + 1);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("An unexpected error occurred while reading file " + file.getAbsolutePath(), e);
        }

        return new CSVScanResult(specificFailureMessages, successCount, failureCount);
    }

    private static char computeSeparator(File file) throws java.io.IOException {
        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr, DEFAULT_BUFFER_SIZE)) {
            String line = reader.readLine();
            if (line != null) {
                return lookForDelimiter(line);
            }
            throw new IllegalArgumentException("No line read from file " + file.getAbsolutePath());
        }
    }

    private static char lookForDelimiter(String line) {
        for (char ch : line.toCharArray()) {
            if (!Character.isLetter(ch)) {
                return ch;
            }
        }
        throw new IllegalStateException("Cannot find delimiter in header " + line);
    }
}
