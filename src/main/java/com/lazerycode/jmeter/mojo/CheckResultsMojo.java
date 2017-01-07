package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.exceptions.ResultsFileNotFoundException;
import com.lazerycode.jmeter.testrunner.ResultScanner;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Collection;

/**
 * JMeter Maven plugin.
 */
@Mojo(name = "results", defaultPhase = LifecyclePhase.VERIFY)
public class CheckResultsMojo extends AbstractJMeterMojo {

	private static final String RESULT_FILE_EXT = "jtl";

	/**
	 * Sets whether build should fail if there are failed requests found in the JMeter result file.
	 * Failures are for example failed requests
	 */
	@Parameter(defaultValue = "false")
	protected boolean ignoreResultFailures;

	/**
	 * Sets whether ResultScanner should search for failed requests in the JMeter result file.
	 * Defaults to false
	 */
	@Parameter(defaultValue = "false")
	protected boolean scanResultsForFailedRequests;

	/**
	 * Sets whether ResultScanner should search for Successful requests in the JMeter result file.
	 * Defaults to false
	 */
	@Parameter(defaultValue = "false")
	protected boolean scanResultsForSuccessfulRequests;

	/**
	 * Scan JMeter result files for successful, and failed requests/
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException {
		if (scanResultsForSuccessfulRequests || scanResultsForFailedRequests) {
			final Collection<File> resultFiles = getResultFiles();
			final ResultScanner resultScanner = parseResultFiles(resultFiles);
			getLog().info(" ");
			getLog().info("Performance Test Results");
			getLog().info(" ");
			getLog().info("Result (.jtl) files scanned:	" + resultFiles.size());
			getLog().info("Successful requests: 		" + resultScanner.getSuccessCount());
			getLog().info("Failed requests: 			" + resultScanner.getFailureCount());
			getLog().info(" ");
			if (!ignoreResultFailures && resultScanner.getFailureCount() > 0) {
				throw new MojoFailureException("Failing build because failed requests have been detected.  JMeter logs are available at: '" + logsDirectory.getAbsolutePath() + "'");
			}
		} else {
			getLog().info(" ");
			getLog().info("Results of Performance Test(s) have not been scanned.");
			getLog().info(" ");
		}
	}

	private ResultScanner parseResultFiles(Collection<File> files) throws ResultsFileNotFoundException {
		final ResultScanner resultScanner = new ResultScanner(scanResultsForSuccessfulRequests, scanResultsForFailedRequests);
		for (File resultFile : files) {
			resultScanner.parseResultFile(resultFile);
		}
		return resultScanner;
	}

	private Collection<File> getResultFiles() {
		return FileUtils.listFiles(resultsDirectory, new String[]{RESULT_FILE_EXT}, false);
	}
}