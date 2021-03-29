package com.lazerycode.jmeter.testrunner;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultScannerTest {

    private static final boolean COUNT_FAILURES = true;
    private static final boolean DO_NOT_COUNT_FAILURES = false;
    private static final boolean COUNT_SUCCESSES = true;
    private static final boolean DO_NOT_COUNT_SUCCESSES = false;
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
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(2);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(2);
    }

    @Test
    public void jtlFileWithFailuresCountSuccessesOnly() throws Exception {
        File resultsFile = new File(jtlFailingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, DO_NOT_COUNT_FAILURES);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(2);
    }

    @Test
    public void jtlFileWithFailuresCountFailuresOnly() throws Exception {
        File resultsFile = new File(jtlFailingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(DO_NOT_COUNT_SUCCESSES, COUNT_FAILURES);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(2);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
    }

    @Test
    public void jtlFileWithNoFailuresCountSuccessAndFailures() throws Exception {
        File resultsFile = new File(jtlPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(4);
    }

    @Test
    public void jtlFileWithNoFailuresCountSuccessesOnly() throws Exception {
        File resultsFile = new File(jtlPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, DO_NOT_COUNT_FAILURES);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(4);
    }

    @Test
    public void jtlFileWithNoFailuresCountFailuresOnly() throws Exception {
        File resultsFile = new File(jtlPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(DO_NOT_COUNT_SUCCESSES, COUNT_FAILURES);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
    }

    @Test
    public void csvFileWithFailuresCountSuccessAndFailures() throws Exception {
        File resultsFile = new File(csvFailingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES, true);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(2);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(0);
    }

    @Test
    public void csvFileWithNoFailuresCountSuccessAndFailures() throws Exception {
        File resultsFile = new File(csvPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES, true);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(2);
    }

    @Test
    public void csvFileWithNoFailuresCountSuccessAndFailuresAlternateSep() throws Exception {
        File resultsFile = new File(csvWithAlternateSeparatorPassingResultsFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES, true);
        fileScanner.parseResultFile(resultsFile);

        assertThat(fileScanner.getFailureCount()).isEqualTo(0);
        assertThat(fileScanner.getSuccessCount()).isEqualTo(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyCSVFileThrowsIllegalArgumentException() throws Exception {
        File resultsFile = new File(emptyCSVFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES, true);
        fileScanner.parseResultFile(resultsFile);
    }

    @Test(expected = IllegalStateException.class)
    public void csvFileMissingDelimiterThrowsIllegalStateException() throws Exception {
        File resultsFile = new File(csvMissingDelimiterFileURL.toURI());
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES, true);
        fileScanner.parseResultFile(resultsFile);
    }

    @Test(expected = MojoExecutionException.class)
    public void fileThatDoesNotExistThrowsResultsFileNotFoundException() throws Exception {
        File resultsFile = new File("DoesNotExist.nope");
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES, true);
        fileScanner.parseResultFile(resultsFile);
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidCSVFileThrowsIOException() throws Exception {
        File resultsFile = new File("/");
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES, true);
        fileScanner.parseResultFile(resultsFile);
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidJTLileThrowsIOException() throws Exception {
        File resultsFile = new File("/");
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES, false);
        fileScanner.parseResultFile(resultsFile);
    }

}
