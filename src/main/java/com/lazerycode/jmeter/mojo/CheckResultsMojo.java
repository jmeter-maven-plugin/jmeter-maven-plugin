package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.json.TestConfig;
import com.lazerycode.jmeter.testrunner.ResultScanner;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * JMeter Maven plugin.
 */
@Mojo(name = "results", defaultPhase = LifecyclePhase.VERIFY)
public class CheckResultsMojo extends AbstractJMeterMojo {

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
			ResultScanner resultScanner = new ResultScanner(scanResultsForSuccessfulRequests, scanResultsForFailedRequests);
			TestConfig testConfig = new TestConfig(new File(testConfigFile));
			for (String resultFileLocation : testConfig.getResultsFileLocations()) {
				resultScanner.parseResultFile(new File(resultFileLocation));
			}
			getLog().info(" ");
			getLog().info("Performance Test Results");
			getLog().info(" ");
			getLog().info("Result (.jtl) files scanned:	" + testConfig.getResultsFileLocations().size());
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
}