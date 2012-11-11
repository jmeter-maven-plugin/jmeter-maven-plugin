package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.*;
import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.RemoteConfiguration;
import com.lazerycode.jmeter.threadhandling.ExitException;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.jmeter.NewDriver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.DirectoryScanner;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TestManager encapsulates functions that gather JMeter Test files and execute the tests
 */
public class TestManager extends JMeterMojo {

    private JMeterArgumentsArray testArgs;
    private File jmeterLog;
    private File logsDir;
    private File testFilesDirectory;
    private List<String> testFilesIncluded;
    private List<String> testFilesExcluded;
    private boolean suppressJMeterOutput;
    private boolean remoteStop = false;
    private boolean remoteStartAll = false;
    private boolean remoteStartAndStopOnce = true;
    private String remoteStart = null;

    public TestManager(JMeterArgumentsArray testArgs, File logsDir, File testFilesDirectory, List<String> testFiles, List<String> excludeTestFiles, boolean suppressJMeterOutput) {
        this.testArgs = testArgs;
        this.logsDir = logsDir;
        this.testFilesDirectory = testFilesDirectory;
        this.testFilesIncluded = testFiles;
        this.testFilesExcluded = excludeTestFiles;
        this.suppressJMeterOutput = suppressJMeterOutput;
    }

    /**
     * Set remote configuration
     *
     * @param remoteConfig
     */
    public void setRemoteConfig(RemoteConfiguration remoteConfig) {
        this.remoteStop = remoteConfig.isStop();
        this.remoteStartAll = remoteConfig.isStartAll();
        this.remoteStartAndStopOnce = remoteConfig.isStartAndStopOnce();
        if (!UtilityFunctions.isNotSet(remoteConfig.getStart())) {
            this.remoteStart = remoteConfig.getStart();
        }
    }

    /**
     * Executes all tests and returns the resultFile names
     *
     * @return the list of resultFile names
     * @throws MojoExecutionException
     */
    public List<String> executeTests() throws MojoExecutionException {
        List<String> tests = generateTestList();
        List<String> results = new ArrayList<String>();
        for (String file : tests) {
            if (!this.remoteStartAndStopOnce || tests.get(tests.size() - 1).equals(file)) {
                testArgs.setRemoteStop(this.remoteStop);
            }
            if (!this.remoteStartAndStopOnce || tests.get(0).equals(file)) {
                testArgs.setRemoteStartAll(this.remoteStartAll);
                testArgs.setRemoteStart(this.remoteStart);
            }
            results.add(executeSingleTest(new File(testFilesDirectory, file)));
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
    private String executeSingleTest(File test) throws MojoExecutionException {
        getLog().info(" ");
        testArgs.setTestFile(test);
        //Delete results file if it already exists
        new File(testArgs.getResultsFileName()).delete();
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
        }
        catch (ExitException e) {
            if (e.getCode() != 0) {
                throw new MojoExecutionException("Test failed", e);
            }
        }
        catch (InterruptedException ex){
            getLog().info(" ");
            getLog().info("System Exit Detected!  Stopping Test...");
            getLog().info(" ");
        }
        finally {
            //TODO wait for child thread shutdown here?
            //TODO kill child threads if waited too long?
            //Reset everything back to normal
            System.setSecurityManager(originalSecurityManager);
            Thread.setDefaultUncaughtExceptionHandler(originalExceptionHandler);
            System.setOut(originalOut);
            getLog().info("Completed Test: " + test.getName());
        }
        return testArgs.getResultsFileName();
    }

    /**
     * Create the jmeter.log file and set the log_file system property for JMeter to pick up
     *
     * @param value
     */
    private void setJMeterLogFile(String value) {
        this.jmeterLog = new File(this.logsDir + File.separator + value);
        System.setProperty("log_file", this.jmeterLog.getAbsolutePath());
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
        Collections.sort(includedFiles, new IncludesComparator(this.testFilesIncluded));
        jmeterTestFiles.addAll(includedFiles);
        return jmeterTestFiles;
    }
}