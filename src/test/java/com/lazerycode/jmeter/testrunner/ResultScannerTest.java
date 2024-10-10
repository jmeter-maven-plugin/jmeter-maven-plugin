package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.results.ResultScannerCSV;
import com.lazerycode.jmeter.results.ResultScanner;
import com.lazerycode.jmeter.results.ResultScannerXML;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultScannerTest {

    private static final boolean COUNT_FAILURES = true;
    private static final boolean DO_NOT_COUNT_FAILURES = false;
    private static final boolean COUNT_SUCCESSES = true;
    private static final boolean DO_NOT_COUNT_SUCCESSES = false;
    private static final boolean DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES = false;
    private static final List<String> DEFAULT_FAILURE_LIST = new ArrayList<>();
    private final URL jtlFailingResultsFileURL = this.getClass().getResource("/jtl2-1-fail.jtl");
    private final URL jtlPassingResultsFileURL = this.getClass().getResource("/jtl2-1-pass.jtl");
    private final URL csvFailingResultsFileURL = this.getClass().getResource("/csv2-1-fail.csv");
    private final URL csvPassingResultsFileURL = this.getClass().getResource("/csv2-1-pass.csv");
    private final URL emptyCSVFileURL = this.getClass().getResource("/empty.csv");
    private final URL csvMissingDelimiterFileURL = this.getClass().getResource("/csv-missing-delimiter.csv");
    private final URL csvWithAlternateSeparatorPassingResultsFileURL = this.getClass().getResource("/csv3-1-pass.csv");

    @Test
    public void jtlFileWithFailuresCountSuccessAndFailures() throws Exception {
        File resultsFile = new File(jtlFailingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerXML(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(2);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(2);
        assertThat(fileScanner.getTotalCount()).isEqualTo(4);
    }

    @Test
    public void jtlMultipleFileWithFailuresCountSuccessAndFailures() throws Exception {
        File resultsFile = new File(jtlFailingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerXML(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(4);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(4);
        assertThat(fileScanner.getTotalCount()).isEqualTo(8);
    }

    @Test
    public void jtlFileWithFailuresCountSuccessesOnly() throws Exception {
        File resultsFile = new File(jtlFailingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerXML(COUNT_SUCCESSES, DO_NOT_COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(2);
        assertThat(fileScanner.getTotalCount()).isEqualTo(2);
    }

    @Test
    public void jtlFileWithFailuresCountFailuresOnly() throws Exception {
        File resultsFile = new File(jtlFailingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerXML(DO_NOT_COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(2);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
        assertThat(fileScanner.getTotalCount()).isEqualTo(2);
    }

    @Test
    public void jtlFileWithNoFailuresCountSuccessAndFailures() throws Exception {
        File resultsFile = new File(jtlPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerXML(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(4);
        assertThat(fileScanner.getTotalCount()).isEqualTo(4);
    }

    @Test
    public void jtlFileWithNoFailuresCountSuccessesOnly() throws Exception {
        File resultsFile = new File(jtlPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerXML(COUNT_SUCCESSES, DO_NOT_COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(4);
        assertThat(fileScanner.getTotalCount()).isEqualTo(4);
    }

    @Test
    public void jtlFileWithNoFailuresCountFailuresOnly() throws Exception {
        File resultsFile = new File(jtlPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerXML(DO_NOT_COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
        assertThat(fileScanner.getTotalCount()).isEqualTo(0);
    }

    @Test
    public void csvFileWithFailuresCountSuccessAndFailures() throws Exception {
        File resultsFile = new File(csvFailingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(2);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
        assertThat(fileScanner.getTotalCount()).isEqualTo(2);
    }

    @Test
    public void csvMultipleFilesWithFailuresCountSuccessAndFailures() throws Exception {
        File resultsFile = new File(csvFailingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(4);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
        assertThat(fileScanner.getTotalCount()).isEqualTo(4);
    }

    @Test
    public void csvFileWithNoFailuresCountSuccessAndFailures() throws Exception {
        File resultsFile = new File(csvPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(2);
        assertThat(fileScanner.getTotalCount()).isEqualTo(2);
    }

    @Test
    public void csvFileWithNoFailuresCountSuccessAndFailuresAlternateSep() throws Exception {
        File resultsFile = new File(csvWithAlternateSeparatorPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(2);
        assertThat(fileScanner.getTotalCount()).isEqualTo(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyCSVFileThrowsIllegalArgumentException() throws Exception {
        File resultsFile = new File(emptyCSVFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);
    }

    @Test(expected = IllegalStateException.class)
    public void csvFileMissingDelimiterThrowsIllegalStateException() throws Exception {
        File resultsFile = new File(csvMissingDelimiterFileURL.toURI());
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);
    }

    @Test
    public void csvFileOnlyCountMatchingFailureMessages() throws Exception {
        File resultsFile = new File(csvFailingResultsFileURL.toURI());
        List<String> failureMessages = new ArrayList<>();
        failureMessages.add("It went wrong!");
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, true, failureMessages);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(1);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
        assertThat(fileScanner.getTotalCount()).isEqualTo(1);
    }

    @Test
    public void csvFileOnlyCountMatchingFailureMessagesThatAreNotFound() throws Exception {
        File resultsFile = new File(csvFailingResultsFileURL.toURI());
        List<String> failureMessages = new ArrayList<>();
        failureMessages.add("FailureMessageNotFound");
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, true, failureMessages);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
        assertThat(fileScanner.getTotalCount()).isEqualTo(0);
    }

    @Test
    public void csvFileOnlyCountMatchingFailureMessagesWhenSomeAreFound() throws Exception {
        File resultsFile = new File(csvFailingResultsFileURL.toURI());
        List<String> failureMessages = new ArrayList<>();
        failureMessages.add("It went wrong!");
        failureMessages.add("FailureMessageNotFound");
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, true, failureMessages);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(1);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
        assertThat(fileScanner.getTotalCount()).isEqualTo(1);
    }

    @Test(expected = MojoExecutionException.class)
    public void fileThatDoesNotExistThrowsResultsFileNotFoundException() throws Exception {
        File resultsFile = new File("DoesNotExist.nope");
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidCSVFileThrowsIOException() throws Exception {
        File resultsFile = new File("/");
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidJTLileThrowsIOException() throws Exception {
        File resultsFile = new File("/");
        ResultScanner fileScanner = new ResultScannerCSV(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);
        fileScanner.parseResultFile(resultsFile);
    }
}
