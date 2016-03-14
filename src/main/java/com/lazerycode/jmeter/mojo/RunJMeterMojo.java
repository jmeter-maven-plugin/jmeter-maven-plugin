package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.testrunner.TestManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * JMeter Maven plugin.
 */
@Mojo(name = "jmeter", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
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

		initialiseJMeterArgumentsArray(true);
		if (null != remoteConfig) {
			remoteConfig.setPropertiesMap(propertiesMap);
		}
		TestManager jMeterTestManager = new TestManager(testArgs, testFilesDirectory, testFilesIncluded, testFilesExcluded, remoteConfig, suppressJMeterOutput, workingDirectory, jMeterProcessJVMSettings, runtimeJarName);
		jMeterTestManager.setPostTestPauseInSeconds(postTestPauseInSeconds);
		getLog().info(" ");
		if (proxyConfig != null) {
			getLog().info(this.proxyConfig.toString());
		}
		resultFilesLocations = jMeterTestManager.executeTests();
	}
}