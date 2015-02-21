package com.lazerycode.jmeter;

import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FailureScannerTest {

	private final boolean ignoreAllFailures = true;
	private final boolean reportAllFailures = false;
	private final URL failingResultsFileURL = this.getClass().getResource("/jtl2-1-fail.jtl");
	private final URL passingResultsFileURL = this.getClass().getResource("/jtl2-1-pass.jtl");

	@Test
	public void jtlFileWithFailures() throws Exception {
		File resultsFile = new File(failingResultsFileURL.toURI());
		FailureScanner fileScanner = new FailureScanner(reportAllFailures);
		fileScanner.parseResults(resultsFile);

		assertThat(fileScanner.hasTestFailed(),
				is(equalTo(true)));
		assertThat(fileScanner.getFailureCount(),
				is(equalTo(2)));
		assertThat(fileScanner.getRequestCount(),
				is(equalTo(4)));
	}

	@Test
	public void jtlFileWithNoFailures() throws Exception {
		File resultsFile = new File(passingResultsFileURL.toURI());
		FailureScanner fileScanner = new FailureScanner(reportAllFailures);
		fileScanner.parseResults(resultsFile);

		assertThat(fileScanner.hasTestFailed(),
				is(equalTo(false)));
		assertThat(fileScanner.getFailureCount(),
				is(equalTo(0)));
		assertThat(fileScanner.getRequestCount(),
				is(equalTo(4)));
	}

	@Test
	public void jtlFileWithFailuresIgnored() throws Exception {
		File resultsFile = new File(failingResultsFileURL.toURI());
		FailureScanner fileScanner = new FailureScanner(ignoreAllFailures);
		fileScanner.parseResults(resultsFile);

		assertThat(fileScanner.hasTestFailed(),
				is(equalTo(false)));
		assertThat(fileScanner.getFailureCount(),
				is(equalTo(0)));
		assertThat(fileScanner.getRequestCount(),
				is(equalTo(4)));
	}


}
