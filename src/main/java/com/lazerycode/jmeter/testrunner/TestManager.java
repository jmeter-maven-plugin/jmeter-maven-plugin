package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.JMeterMojo;
import com.lazerycode.jmeter.UtilityFunctions;
import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.RemoteConfiguration;
import com.lazerycode.jmeter.threadhandling.ExitException;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.jmeter.NewDriver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TestManager encapsulates functions that gather JMeter Test files and execute the tests
 */
public class TestManager extends JMeterMojo {

	private final JMeterArgumentsArray baseTestArgs;
	private final File logsDirectory;
	private final File testFilesDirectory;
	private final List<String> testFilesIncluded;
	private final List<String> testFilesExcluded;
	private final boolean suppressJMeterOutput;
	private final RemoteConfiguration remoteServerConfiguration;

	public TestManager(JMeterArgumentsArray baseTestArgs, File logsDirectory, File testFilesDirectory, List<String> testFilesIncluded, List<String> testFilesExcluded, RemoteConfiguration remoteServerConfiguration, boolean suppressJMeterOutput) {
		this.baseTestArgs = baseTestArgs;
		this.logsDirectory = logsDirectory;
		this.testFilesDirectory = testFilesDirectory;
		this.testFilesIncluded = testFilesIncluded;
		this.testFilesExcluded = testFilesExcluded;
		this.remoteServerConfiguration = remoteServerConfiguration;
		this.suppressJMeterOutput = suppressJMeterOutput;
	}

	/**
	 * Executes all tests and returns the resultFile names
	 *
	 * @return the list of resultFile names
	 * @throws MojoExecutionException
	 */
	public List<String> executeTests() throws MojoExecutionException {
		JMeterArgumentsArray thisTestArgs = baseTestArgs;
		List<String> tests = generateTestList();
		List<String> results = new ArrayList<String>();
		for (String file : tests) {
			if ((remoteServerConfiguration.isStartServersBeforeTests() && tests.get(0).equals(file)) || remoteServerConfiguration.isStartAndStopServersForEachTest()) {
				thisTestArgs.setRemoteStart();
				thisTestArgs.setRemoteStartServerList(remoteServerConfiguration.getServerList());
			}
			if ((remoteServerConfiguration.isStopServersAfterTests() && tests.get(tests.size() - 1).equals(file)) || remoteServerConfiguration.isStartAndStopServersForEachTest()) {
				thisTestArgs.setRemoteStop();
			}
			results.add(executeSingleTest(new File(testFilesDirectory, file), thisTestArgs));
		}
		return results;
	}

	//=============================================================================================

	/**
	 * Executes a single JMeter test by building up a list of command line
	 * parameters to pass to JMeter.start().
	 *
	 * @param test JMeter test XML
	 * @return the report file names.
	 * @throws org.apache.maven.plugin.MojoExecutionException
	 *          Exception
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	private String executeSingleTest(File test, JMeterArgumentsArray testArgs) throws MojoExecutionException {
		getLog().info(" ");
		testArgs.setTestFile(test);
		//Delete results file if it already exists
		new File(testArgs.getResultsLogFileName()).delete();
		getLog().debug("JMeter is called with the following command line arguments: " + UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()));
		SecurityManager originalSecurityManager = overrideSecurityManager();
		Thread.UncaughtExceptionHandler originalExceptionHandler = overrideUncaughtExceptionHandler();
		PrintStream originalOut = System.out;
		setJMeterLogFile(test.getName() + ".log");
		getLog().info("Executing test: " + test.getName());
		try {
			//Suppress JMeter's annoying System.out messages.
			if (suppressJMeterOutput) System.setOut(new PrintStream(new NullOutputStream()));
			//Start the test.
			NewDriver.main(testArgs.buildArgumentsArray());
			waitForTestToFinish(UtilityFunctions.getThreadNames(false));
		} catch (ExitException e) {
			if (e.getCode() != 0) {
				throw new MojoExecutionException("Test failed", e);
			}
		} catch (InterruptedException ex) {
			getLog().info(" ");
			getLog().info("System Exit Detected!  Stopping Test...");
			getLog().info(" ");
		} finally {
			//TODO wait for child thread shutdown here?
			//TODO kill child threads if waited too long?
			//Reset everything back to normal
			System.setSecurityManager(originalSecurityManager);
			Thread.setDefaultUncaughtExceptionHandler(originalExceptionHandler);
			System.setOut(originalOut);
			getLog().info("Completed Test: " + test.getName());
		}
		return testArgs.getResultsLogFileName();
	}

	/**
	 * Create the jmeter.log file and set the log_file system property for JMeter to pick up
	 *
	 * @param value String
	 */
	private void setJMeterLogFile(String value) {
		System.setProperty("log_file", new File(this.logsDirectory + File.separator + value).getAbsolutePath());
	}

	/**
	 * Scan Project directories for JMeter Test Files according to includes and excludes
	 *
	 * @return found JMeter tests
	 */
	private List<String> generateTestList() {
		List<String> jmeterTestFiles = new ArrayList<String>();
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(this.testFilesDirectory);
		scanner.setIncludes(this.testFilesIncluded == null ? new String[]{"**/*.jmx"} : this.testFilesIncluded.toArray(new String[jmeterTestFiles.size()]));
		if (this.testFilesExcluded != null) {
			scanner.setExcludes(this.testFilesExcluded.toArray(new String[testFilesExcluded.size()]));
		}
		scanner.scan();
		final List<String> includedFiles = Arrays.asList(scanner.getIncludedFiles());
		jmeterTestFiles.addAll(includedFiles);
		return jmeterTestFiles;
	}
}