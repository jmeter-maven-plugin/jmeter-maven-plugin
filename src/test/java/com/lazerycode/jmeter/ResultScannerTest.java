package com.lazerycode.jmeter;

import com.lazerycode.jmeter.testrunner.ResultScanner;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ResultScannerTest {

	private static final boolean COUNT_FAILURES = true;
	private static final boolean DO_NOT_COUNT_FAILURES = false;
	private static final boolean COUNT_SUCCESSES = true;
	private static final boolean DO_NOT_COUNT_SUCCESSES = false;
	private final URL failingResultsFileURL = this.getClass().getResource("/jtl2-1-fail.jtl");
	private final URL passingResultsFileURL = this.getClass().getResource("/jtl2-1-pass.jtl");

	@Test
	public void jtlFileWithFailuresCountSuccessAndFailures() throws Exception {
		File resultsFile = new File(failingResultsFileURL.toURI());
		ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES);
		fileScanner.parseResultFile(resultsFile);

		assertThat(fileScanner.getFailureCount(),
				is(equalTo(2)));
		assertThat(fileScanner.getSuccessCount(),
				is(equalTo(2)));
	}

	@Test
	public void jtlFileWithFailuresCountSuccessesOnly() throws Exception {
		File resultsFile = new File(failingResultsFileURL.toURI());
		ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, DO_NOT_COUNT_FAILURES);
		fileScanner.parseResultFile(resultsFile);

		assertThat(fileScanner.getFailureCount(),
				is(equalTo(0)));
		assertThat(fileScanner.getSuccessCount(),
				is(equalTo(2)));
	}

	@Test
	public void jtlFileWithFailuresCountFailuresOnly() throws Exception {
		File resultsFile = new File(failingResultsFileURL.toURI());
		ResultScanner fileScanner = new ResultScanner(DO_NOT_COUNT_SUCCESSES, COUNT_FAILURES);
		fileScanner.parseResultFile(resultsFile);

		assertThat(fileScanner.getFailureCount(),
				is(equalTo(2)));
		assertThat(fileScanner.getSuccessCount(),
				is(equalTo(0)));
	}

	@Test
	public void jtlFileWithNoFailuresCountSuccessAndFailures() throws Exception {
		File resultsFile = new File(passingResultsFileURL.toURI());
		ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES);
		fileScanner.parseResultFile(resultsFile);

		assertThat(fileScanner.getFailureCount(),
				is(equalTo(0)));
		assertThat(fileScanner.getSuccessCount(),
				is(equalTo(4)));
	}

	@Test
	public void jtlFileWithNoFailuresCountSuccessesOnly() throws Exception {
		File resultsFile = new File(passingResultsFileURL.toURI());
		ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, DO_NOT_COUNT_FAILURES);
		fileScanner.parseResultFile(resultsFile);

		assertThat(fileScanner.getFailureCount(),
				is(equalTo(0)));
		assertThat(fileScanner.getSuccessCount(),
				is(equalTo(4)));
	}

	@Test
	public void jtlFileWithNoFailuresCountFailuresOnly() throws Exception {
		File resultsFile = new File(passingResultsFileURL.toURI());
		ResultScanner fileScanner = new ResultScanner(DO_NOT_COUNT_SUCCESSES, COUNT_FAILURES);
		fileScanner.parseResultFile(resultsFile);

		assertThat(fileScanner.getFailureCount(),
				is(equalTo(0)));
		assertThat(fileScanner.getSuccessCount(),
				is(equalTo(0)));
	}
}
