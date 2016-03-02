package com.lazerycode.jmeter.maven;

import com.lazerycode.jmeter.testrunner.ResultScanner;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * JMeter Maven plugin.
 */
@Mojo(name = "results", defaultPhase = LifecyclePhase.VERIFY)
public class CheckResultsMojo extends AbstractJMeterMojo {

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
			for (String resultFileLocation : resultFilesLocations) {
				resultScanner.parseResultFile(new File(resultFileLocation));
			}
			getLog().info(" ");
			getLog().info("Performance Test Results");
			getLog().info(" ");
			getLog().info("Result (.jtl) files scanned:	" + resultFilesLocations.size());
			getLog().info("Successful requests: 		" + resultScanner.getSuccessCount());
			getLog().info("Failed requests: 			" + resultScanner.getFailureCount());
			getLog().info(" ");
			if (!ignoreResultFailures && resultScanner.getFailureCount() > 0) {
				throw new MojoFailureException("Failing build because failed requests have been detected.  JMeter logs are available at: '" + logsDir.getAbsolutePath() + "'");
			}
		} else {
			getLog().info(" ");
			getLog().info("Results of Performance Test(s) have not been scanned.");
			getLog().info(" ");
		}
	}
}