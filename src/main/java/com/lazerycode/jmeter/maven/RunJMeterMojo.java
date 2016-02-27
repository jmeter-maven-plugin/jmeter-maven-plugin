package com.lazerycode.jmeter.maven;

import com.lazerycode.jmeter.testrunner.ResultScanner;
import com.lazerycode.jmeter.testrunner.TestManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.List;

/**
 * JMeter Maven plugin.
 */
@Mojo(name = "configure-jmeter", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class RunJMeterMojo extends AbstractJMeterMojo {

	/**
	 * Configure a local instance of JMeter
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skipTests) {
			getLog().info(" Performance tests are skipped.");
			return;
		}
		getLog().info(" ");
		getLog().info("-------------------------------------------------------");
		getLog().info(" P E R F O R M A N C E    T E S T S");
		getLog().info("-------------------------------------------------------");

		initialiseJMeterArgumentsArray(true);
		if (null != remoteConfig) {
			remoteConfig.setMasterPropertiesMap(pluginProperties.getMasterPropertiesMap());
		}
		TestManager jMeterTestManager = new TestManager(testArgs, testFilesDirectory, testFilesIncluded, testFilesExcluded, remoteConfig, suppressJMeterOutput, binDir, jMeterProcessJVMSettings);
		jMeterTestManager.setPostTestPauseInSeconds(postTestPauseInSeconds);
		getLog().info(" ");
		if (proxyConfig != null) {
			getLog().info(this.proxyConfig.toString());
		}
		List<String> testResults = jMeterTestManager.executeTests();
		parseTestResults(testResults);
	}

	/**
	 * Scan JMeter result files for successful, and failed requests
	 *
	 * @param resultFilesLocations List of JMeter result files.
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	protected void parseTestResults(List<String> resultFilesLocations) throws MojoExecutionException, MojoFailureException {
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