package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.exceptions.IOException;
import com.lazerycode.jmeter.json.TestConfig;
import com.lazerycode.jmeter.testrunner.TestManager;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * JMeter Maven plugin.
 */
@Mojo(name = "jmeter", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
@Execute(goal = "configure")
public class RunJMeterMojo extends AbstractJMeterMojo {

	/**
	 * Run all the JMeter tests.
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException {
		getLog().info(" ");
		getLog().info("-------------------------------------------------------");
		getLog().info(" P E R F O R M A N C E    T E S T S");
		getLog().info("-------------------------------------------------------");

		if (!testFilesDirectory.exists()) {
			getLog().info("<testFilesDirectory>" + testFilesDirectory.getAbsolutePath() + "</testFilesDirectory> does not exist...");
			getLog().info("Performance tests are skipped.");
			return;
		}

		TestConfig testConfig = new TestConfig(new File(testConfigFile));
		initialiseJMeterArgumentsArray(true, testConfig.getResultsOutputIsCSVFormat());

		if (null != remoteConfig) {
			remoteConfig.setPropertiesMap(propertiesMap);
		}

		CopyFilesInTestDirectory(testFilesDirectory, testFilesBuildDirectory);

		TestManager jMeterTestManager = 
		        new TestManager(testArgs, testFilesBuildDirectory, testFilesIncluded, testFilesExcluded, 
		                remoteConfig, suppressJMeterOutput, workingDirectory, jMeterProcessJVMSettings, 
		                runtimeJarName, resultsDirectory, generateReports);
		jMeterTestManager.setPostTestPauseInSeconds(postTestPauseInSeconds);
		getLog().info(" ");
		if (proxyConfig != null) {
			getLog().info(this.proxyConfig.toString());
		}

		testConfig.setResultsFileLocations(jMeterTestManager.executeTests());
		testConfig.writeResultFilesConfigTo(testConfigFile);
	}
}