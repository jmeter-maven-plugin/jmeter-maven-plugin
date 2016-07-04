package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.testrunner.TestManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

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

		initialiseJMeterArgumentsArray(true);
		if (null != remoteConfig) {
			remoteConfig.setPropertiesMap(propertiesMap);
		}

		//TODO copy files from <testFilesDirectory> to somewhere in build directory and flatten the sirectory structure while making sure test names are unique
		//TODO pass this new directory into TestManager

		TestManager jMeterTestManager = new TestManager(testArgs, testFilesDirectory, testFilesIncluded, testFilesExcluded, remoteConfig, suppressJMeterOutput, workingDirectory, jMeterProcessJVMSettings, runtimeJarName);
		jMeterTestManager.setPostTestPauseInSeconds(postTestPauseInSeconds);
		getLog().info(" ");
		if (proxyConfig != null) {
			getLog().info(this.proxyConfig.toString());
		}
		resultFilesLocations = jMeterTestManager.executeTests();
	}
}