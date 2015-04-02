package com.lazerycode.jmeter;

import com.lazerycode.jmeter.testrunner.PrePostProcessingConfig;
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

		TestManager jMeterTestManager = new TestManager(testArgs, testFilesDirectory, testFilesIncluded, testFilesExcluded, remoteConfig, suppressJMeterOutput, binDir, jMeterProcessJVMSettings,getPrePostProcessingConfig());
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
	 * @param results List of JMeter result files.
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	void parseTestResults(List<String> results) throws MojoExecutionException, MojoFailureException {
		FailureScanner failureScanner = new FailureScanner(ignoreResultFailures);
		int totalFailureCount = 0;
		boolean failed = false;
		for (String file : results) {
			try {
				if (failureScanner.hasTestFailed(new File(file))) {
					totalFailureCount += failureScanner.getFailureCount();
					failed = true;
				}
			} catch (IOException e) {
				throw new MojoExecutionException(e.getMessage());
			}
		}
		getLog().info(" ");
		getLog().info("Test Results:");
		getLog().info(" ");
		getLog().info("Tests Run: " + results.size() + ", Failures: " + totalFailureCount);
		getLog().info(" ");
		if (failed) {
			throw new MojoFailureException("There were " + totalFailureCount + " test failures.  See the JMeter logs at '" + logsDir.getAbsolutePath() + "' for details.");
		}
	}

	/**
	 * Construct Pre and Post Processing configuration
	 * @return
	 */
	private PrePostProcessingConfig getPrePostProcessingConfig() {
		return new PrePostProcessingConfig(preProcessingScript,postProcessingScript,preTestScript,postTestScript,addTestNameOnScript, scriptDir);
	}
}