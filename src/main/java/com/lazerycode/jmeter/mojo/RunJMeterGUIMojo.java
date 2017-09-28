package com.lazerycode.jmeter.mojo;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.lazerycode.jmeter.json.TestConfig;
import com.lazerycode.jmeter.testrunner.JMeterProcessBuilder;
import com.lazerycode.jmeter.utility.UtilityFunctions;

/**
 * JMeter Maven plugin.
 *
 * @author Jarrod Ribble
 */
@Mojo(name = "gui", defaultPhase = LifecyclePhase.TEST)
@Execute(goal = "configure")
public class RunJMeterGUIMojo extends AbstractJMeterMojo {

	@Parameter(defaultValue = "false")
	private boolean runInBackground;

	/**
	 * Supply a test file to open in the GUI once it is loaded.
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
	public void doExecute() throws MojoExecutionException, MojoFailureException {
		getLog().info(" ");
		getLog().info("-------------------------------------------------------");
		getLog().info(" S T A R T I N G    J M E T E R    G U I ");
		getLog().info("-------------------------------------------------------");
		initialiseJMeterArgumentsArray(false);
		getLog().debug("JMeter is called with the following command line arguments: " + UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()));
		startJMeterGUI();
	}

	protected void initialiseJMeterArgumentsArray(boolean disableGUI) throws MojoExecutionException {
		TestConfig testConfig = new TestConfig(new File(testConfigFile));
		super.initialiseJMeterArgumentsArray(disableGUI, testConfig.getResultsOutputIsCSVFormat());
		testArgs.setTestFile(guiTestFile, testFilesDirectory);
	}

	private void startJMeterGUI() throws MojoExecutionException {
		JMeterProcessBuilder JMeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName);
		JMeterProcessBuilder.setWorkingDirectory(workingDirectory);
		JMeterProcessBuilder.addArguments(testArgs.buildArgumentsArray());
		try {
			final Process process = JMeterProcessBuilder.startProcess();
			if (!runInBackground) {
				process.waitFor();
			}
		} catch (InterruptedException ex) {
			getLog().info(" ");
			getLog().info("System Exit Detected!  Stopping GUI...");
			getLog().info(" ");
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			getLog().error("Error starting JMeter with args "+testArgs.buildArgumentsArray()
			    + ", in working directory:"+workingDirectory, e);
		}
	}
}