package com.lazerycode.jmeter;

import com.lazerycode.jmeter.testrunner.TestManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * JMeter Maven plugin.
 */
@Mojo(name = "jmeter")
public class JMeterMojo extends JMeterAbstractMojo {

	/**
	 * Run all the JMeter tests.
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skipTests) {
			getLog().info(" ");
			getLog().info("-------------------------------------------------------");
			getLog().info(" S K I P P I N G    P E R F O R M A N C E    T E S T S ");
			getLog().info("-------------------------------------------------------");
			getLog().info(" ");
			return;
		}
		getLog().info(" ");
		getLog().info("-------------------------------------------------------");
		getLog().info(" P E R F O R M A N C E    T E S T S");
		getLog().info("-------------------------------------------------------");
		getLog().info(" ");
		generateJMeterDirectoryTree();
		setJMeterResultFileFormat();
		configureAdvancedLogging();
		propertyConfiguration();
		populateJMeterDirectoryTree();
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
	 * Scan JMeter result files for "error" and "failure" messages
	 *
	 * @param resultFilesLocations List of JMeter result files.
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	void parseTestResults(List<String> resultFilesLocations) throws MojoExecutionException, MojoFailureException {
		if (scanResultsForSuccessfulRequests || scanResultsForFailedRequests) {
			ResultScanner resultScanner = new ResultScanner(scanResultsForSuccessfulRequests, scanResultsForFailedRequests);
			for (String resultFileLocation : resultFilesLocations) {
				try {
					resultScanner.parseResultFile(new File(resultFileLocation));
				} catch (IOException e) {
					throw new MojoExecutionException(e.getMessage());
				}
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