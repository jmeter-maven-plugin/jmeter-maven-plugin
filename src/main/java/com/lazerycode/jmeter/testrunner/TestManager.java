package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;
import com.lazerycode.jmeter.configuration.RemoteConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lazerycode.jmeter.configuration.RemoteArgumentsArrayBuilder.buildRemoteArgumentsArray;
import static com.lazerycode.jmeter.utility.UtilityFunctions.isNotSet;

/**
 * TestManager encapsulates functions that gather JMeter Test files and execute the tests
 */
public class TestManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestManager.class);
    private JMeterArgumentsArray baseTestArgs;
    private File binDir;
    private File testFilesDirectory;
    private String[] testFilesIncluded = new String[]{"**/*.jmx"};
    private String[] testFilesExcluded = new String[0];
    private boolean suppressJMeterOutput;
    private boolean ignoreJVMKilledExitCode;
    private RemoteConfiguration remoteServerConfiguration;
    private JMeterProcessJVMSettings jMeterProcessJVMSettings;
    private long postTestPauseInSeconds = 0L;
    private String runtimeJarName;
    private File reportDirectory;
    private boolean generateReports = false;

    private static final int EXIT_CODE_FOR_JVM_KILLED = 143;

    public TestManager setBinDir(File file) {
        this.binDir = file;

        return this;
    }

    public TestManager setBaseTestArgs(JMeterArgumentsArray argumentsArray) {
        this.baseTestArgs = argumentsArray;

        return this;
    }

    public TestManager setTestFilesDirectory(File file) {
        this.testFilesDirectory = file;

        return this;
    }

    public TestManager setRemoteServerConfiguration(RemoteConfiguration configuration) {
        this.remoteServerConfiguration = configuration;

        return this;
    }

    public TestManager setSuppressJMeterOutput(Boolean value) {
        this.suppressJMeterOutput = value;

        return this;
    }

    public TestManager setJMeterProcessJVMSettings(JMeterProcessJVMSettings settings) {
        this.jMeterProcessJVMSettings = settings;

        return this;
    }

    public TestManager setRuntimeJarName(String value) {
        this.runtimeJarName = value;

        return this;
    }

    public TestManager setReportDirectory(File file) {
        this.reportDirectory = file;

        return this;
    }

    public TestManager setGenerateReports(Boolean value) {
        this.generateReports = value;

        return this;
    }

    public TestManager setTestFilesExcluded(List<String> values) {
        this.testFilesExcluded = values.toArray(new String[0]);

        return this;
    }

    public TestManager setTestFilesIncluded(List<String> values) {
        if (!values.isEmpty()) {
            this.testFilesIncluded = values.toArray(new String[0]);
        }

        return this;
    }

    public TestManager setIgnoreJVMKilled(Boolean ignoreJVMKillExitCode) {
        this.ignoreJVMKilledExitCode = ignoreJVMKillExitCode;

        return this;
    }

    /**
     * Sets a pause after each test has been executed.
     *
     * @param postTestPauseInSeconds Number of seconds to pause after a test has completed
     */
    public TestManager setPostTestPauseInSeconds(String postTestPauseInSeconds) {
        try {
            this.postTestPauseInSeconds = Long.parseLong(postTestPauseInSeconds);
        } catch (NumberFormatException ex) {
            LOGGER.error("Error parsing <postTestPauseInSeconds>{}</postTestPauseInSeconds> to Long, will default to 0L", postTestPauseInSeconds);
        }

        return this;
    }

    JMeterArgumentsArray getBaseTestArgs() {
        return baseTestArgs;
    }

    File getBinDir() {
        return binDir;
    }

    File getTestFilesDirectory() {
        return testFilesDirectory;
    }

    String[] getTestFilesIncluded() {
        return testFilesIncluded;
    }

    String[] getTestFilesExcluded() {
        return testFilesExcluded;
    }

    boolean isSuppressJMeterOutput() {
        return suppressJMeterOutput;
    }

    RemoteConfiguration getRemoteServerConfiguration() {
        return remoteServerConfiguration;
    }

    JMeterProcessJVMSettings getJMeterProcessJVMSettings() {
        return jMeterProcessJVMSettings;
    }

    long getPostTestPauseInSeconds() {
        return postTestPauseInSeconds;
    }

    String getRuntimeJarName() {
        return runtimeJarName;
    }

    File getReportDirectory() {
        return reportDirectory;
    }

    boolean isGenerateReports() {
        return generateReports;
    }

    /**
     * Executes all tests and returns the resultFile names
     *
     * @return the list of resultFile names
     * @throws MojoExecutionException MojoExecutionException
     */
    public List<String> executeTests() throws MojoExecutionException {
        JMeterArgumentsArray thisTestArgs = baseTestArgs;
        List<String> tests = generateTestList();
        List<String> results = new ArrayList<>();
        for (String file : tests) {
            if (generateReports) {
                File outputReportFolder = new File(reportDirectory + File.separator + FilenameUtils.removeExtension(file));
                LOGGER.info("Will generate HTML report in {}", outputReportFolder.getAbsolutePath());
                if (outputReportFolder.exists() || outputReportFolder.mkdirs()) {
                    thisTestArgs.setReportsDirectory(outputReportFolder.getAbsolutePath());
                } else {
                    throw new MojoExecutionException("Unable to create report output folder:" + outputReportFolder.getAbsolutePath());
                }
            }
            if ((remoteServerConfiguration.isStartServersBeforeTests() && tests.get(0).equals(file)) || remoteServerConfiguration.isStartAndStopServersForEachTest()) {
                thisTestArgs.setRemoteStart();
                thisTestArgs.setRemoteStartServerList(remoteServerConfiguration.getServerList());
            }
            if ((remoteServerConfiguration.isStopServersAfterTests() && tests.get(tests.size() - 1).equals(file)) || remoteServerConfiguration.isStartAndStopServersForEachTest()) {
                thisTestArgs.setRemoteStop();
            }
            results.add(executeSingleTest(new File(testFilesDirectory, file), thisTestArgs));
            try {
                TimeUnit.SECONDS.sleep(postTestPauseInSeconds);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
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
     * @throws org.apache.maven.plugin.MojoExecutionException Exception
     */
    private String executeSingleTest(File test, JMeterArgumentsArray testArgs) throws MojoExecutionException {
        testArgs.setTestFile(test, testFilesDirectory);
        File currentResultsFile = new File(testArgs.getResultsLogFileName());
        if (currentResultsFile.exists()) {
            LOGGER.info("{} already exists!, deleting file in preparation for new test run...", currentResultsFile);
            if (!currentResultsFile.delete()) {
                throw new MojoExecutionException("Failed to delete existing results file:" + currentResultsFile.getAbsolutePath());
            }
        }
        List<String> argumentsArray = testArgs.buildArgumentsArray();
        argumentsArray.addAll(buildRemoteArgumentsArray(remoteServerConfiguration.getPropertiesMap()));
        LOGGER.info("Executing test: {}", test.getName());
        JMeterProcessBuilder jmeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName);
        jmeterProcessBuilder.setWorkingDirectory(binDir);
        jmeterProcessBuilder.addArguments(argumentsArray);
        try {
            final Process process = jmeterProcessBuilder.build().start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutdown detected, destroying JMeter process...");
                LOGGER.info(" ");
                process.destroy();
            }));
            try (InputStreamReader isr = new InputStreamReader(process.getInputStream());
                 BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (suppressJMeterOutput) {
                        LOGGER.debug(line);
                    } else {
                        LOGGER.info(line);
                    }
                }
                int jMeterExitCode = process.waitFor();
                if (jMeterExitCode != 0) {
                    if (ignoreJVMKilledExitCode && jMeterExitCode == EXIT_CODE_FOR_JVM_KILLED) {
                        LOGGER.warn("JVM has been force killed!");
                        LOGGER.warn("Build failure not triggered due to config settings, however you may want to investigate this");
                    } else {
                        throw new MojoExecutionException("Test failed with exit code:" + jMeterExitCode);
                    }
                }
                LOGGER.info("Completed Test: {}", test.getAbsolutePath());
                LOGGER.info(" ");
            }
        } catch (InterruptedException ex) {
            LOGGER.info(" ");
            LOGGER.info("System Exit Detected!  Stopping Test...");
            LOGGER.info(" ");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return testArgs.getResultsLogFileName();
    }

    /**
     * Scan Project directories for JMeter Test Files according to includes and excludes
     *
     * @return found JMeter tests
     */
    List<String> generateTestList() {
        if (isNotSet(this.testFilesDirectory)) {
            return Collections.emptyList();
        }
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(this.testFilesDirectory);
        scanner.setIncludes(this.testFilesIncluded);
        scanner.setExcludes(this.testFilesExcluded);
        scanner.scan();

        return Arrays.asList(scanner.getIncludedFiles());
    }
}
