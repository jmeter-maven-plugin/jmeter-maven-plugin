package com.lazerycode.jmeter;

import com.lazerycode.jmeter.testrunner.JMeterProcessBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * JMeter Maven plugin.
 *
 * @author Jarrod Ribble
 */
@Mojo(name = "gui")
public class JMeterGUIMojo extends JMeterAbstractMojo {

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
		configureAdvancedLogging();
		propertyConfiguration();
		populateJMeterDirectoryTree();
		initialiseJMeterArgumentsArray(false);
		getLog().info("JMeter is called with the following command line arguments: " + UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()));
		//Start The GUI
		JMeterProcessBuilder JMeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings);
		JMeterProcessBuilder.setWorkingDirectory(binDir);
		JMeterProcessBuilder.addArguments(testArgs.buildArgumentsArray());
		try {
			final Process process = JMeterProcessBuilder.startProcess();
			process.waitFor();
		} catch (InterruptedException ex) {
			getLog().info(" ");
			getLog().info("System Exit Detected!  Stopping GUI...");
			getLog().info(" ");
		} catch (IOException e) {
			getLog().error(e.getMessage());
		}
	}
}