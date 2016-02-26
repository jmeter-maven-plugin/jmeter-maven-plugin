package com.lazerycode.jmeter;

import com.lazerycode.jmeter.testrunner.JMeterProcessBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * JMeter Maven plugin.
 *
 * @author Jarrod Ribble
 */
@Mojo(name = "gui")
@SuppressWarnings({"UnusedDeclaration"})
public class JMeterGUIMojo extends JMeterAbstractMojo {

	@Parameter(defaultValue = "false")
	private boolean runInBackground;

	/**
	 * Supply a test file to open in the GUI once it is loaded.
	 */
	@Parameter
	private File guiTestFile;

	/**
	 * Convenient to open a test file into the GUI after it is loaded.
	 */
	@Parameter
	private File guiTestFile;

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
			if(!runInBackground) {
				process.waitFor();
			}
		} catch (InterruptedException ex) {
			getLog().info(" ");
			getLog().info("System Exit Detected!  Stopping GUI...");
			getLog().info(" ");
		} catch (IOException e) {
			getLog().error(e.getMessage());
		}
	}

	@Override
	protected void initialiseJMeterArgumentsArray(boolean disableGUI) throws MojoExecutionException {
		super.initialiseJMeterArgumentsArray(disableGUI);
		testArgs.setTestFile(guiTestFile);
	}
}