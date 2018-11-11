package com.lazerycode.jmeter.mojo;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.lazerycode.jmeter.json.TestConfig;
import com.lazerycode.jmeter.testrunner.ResultScanner;
import com.lazerycode.jmeter.testrunner.TestFailureDecider;

/**
 * Goal that computes successes/failures from CSV or XML results files.<br/>
 * This goal runs within Lifecycle phase {@link LifecyclePhase#VERIFY}.<br/>
 * Ensure you set 'scanResultsForSuccessfulRequests' and 'scanResultsForFailedRequests' to true.
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
	@Parameter(defaultValue = "true")
	protected boolean scanResultsForFailedRequests;
	
	/**
     * Sets the error rate threshold limit for build to get failed, i.e if its set to 3 then build fails only if
     * the % of failed requests are above 3
     * defaults to 0
     */
	@Parameter(defaultValue = "0")
	protected float errorRateThresholdInPercent;

	/**
	 * Sets whether ResultScanner should search for Successful requests in the JMeter result file.
	 * Defaults to false
	 */
	@Parameter(defaultValue = "true")
	protected boolean scanResultsForSuccessfulRequests;

	/**
	 * Scan JMeter result files for successful, and failed requests/
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException {
	    if(!ignoreResultFailures && !scanResultsForFailedRequests) {
	        getLog().warn("current value of scanResultsForFailedRequests("
	                +scanResultsForFailedRequests+") is incompatible with ignoreResultFailures("
	                +ignoreResultFailures+", setting scanResultsForFailedRequests to true");
	        scanResultsForFailedRequests = true;
	    }
		if (scanResultsForSuccessfulRequests || scanResultsForFailedRequests) {
		    TestConfig testConfig = new TestConfig(new File(testConfigFile));
		    getLog().info("Will scan results using format:"+testConfig.getFullConfig());
			ResultScanner resultScanner = new ResultScanner(scanResultsForSuccessfulRequests, scanResultsForFailedRequests,
			        testConfig.getResultsOutputIsCSVFormat());
			for (String resultFileLocation : testConfig.getResultsFileLocations()) {
				resultScanner.parseResultFile(new File(resultFileLocation));
			}
			getLog().info(" ");
			getLog().info("Performance Test Results");
			getLog().info(" ");
			getLog().info("Result (.jtl) files scanned: " + testConfig.getResultsFileLocations().size());
			getLog().info("Successful requests:         " + resultScanner.getSuccessCount());
			getLog().info("Failed requests:             " + resultScanner.getFailureCount());
			TestFailureDecider decider = new TestFailureDecider(ignoreResultFailures, errorRateThresholdInPercent, resultScanner);
            decider.runChecks();
			getLog().info("Failures:                    " + decider.getErrorPercentage() + "% (" + decider.getErrorPercentageThreshold() + "% accepted)" );
			if (decider.failBuild()) {
			    throw new MojoFailureException("Failing build because error percentage "+decider.getErrorPercentage()
			        +" is above accepted threshold "+decider.getErrorPercentageThreshold()
			        +". JMeter logs are available at: '" + logsDirectory.getAbsolutePath() + "'");
			}
		} else {
			getLog().info("Results of Performance Test(s) have not been scanned.");
		}
	}
}