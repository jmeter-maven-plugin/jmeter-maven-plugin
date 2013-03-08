package com.lazerycode.jmeter;

import org.apache.jmeter.NewDriver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * JMeter Maven plugin.
 *
 * @author Jarrod Ribble
 * @goal gui
 * @requiresProject true
 */
@SuppressWarnings("JavaDoc")
class JMeterGUIMojo extends JMeterAbstractMojo {

	/**
	 * Load the JMeter GUI
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info(" ");
		getLog().info("-------------------------------------------------------");
		getLog().info(" STARTING JMETER GUI");
		getLog().info("-------------------------------------------------------");
		getLog().info(" ");
		generateJMeterDirectoryTree();
		propertyConfiguration();
		populateJMeterDirectoryTree();
		initialiseJMeterArgumentsArray(false);
		SecurityManager originalSecurityManager = overrideSecurityManager();
		try {
			getLog().info("JMeter is called with the following command line arguments: " + UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()));
			//start GUI
			NewDriver.main(testArgs.buildArgumentsArray());
			waitForTestToFinish(UtilityFunctions.getThreadNames(true));
		} catch (InterruptedException e) {
			getLog().info(" ");
			getLog().info("Thread Interrupt Detected!  Shutting GUI Down...");
			getLog().info("(Any interrupt stack trace after this point is expected)");
			getLog().info(" ");
		} finally {
			//Reset everything back to normal
			System.setSecurityManager(originalSecurityManager);
		}

	}
}