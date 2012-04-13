package com.lazerycode.jmeter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.lazerycode.jmeter.properties.JMeterPropertiesFiles;
import com.lazerycode.jmeter.reporting.ReportGenerator;
import com.lazerycode.jmeter.testrunner.TestManager;

/**
 * JMeter Maven plugin.
 *
 * @author Tim McCune
 * @goal jmeter
 * @requiresProject true
 */
public class JMeterMojo extends JMeterAbstractMojo {

    /**
     * Run all the JMeter tests.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" P E R F O R M A N C E    T E S T S");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
        generateJMeterDirectoryTree();
        propertyConfiguration();
        setJMeterClasspath();
        initialiseJMeterArgumentsArray();
        TestManager jMeterTestManager = new TestManager(this.testArgs, this.logsDir, this.testFilesDirectory, this.testFilesIncluded, this.testFilesExcluded, this.suppressJMeterOutput);
        jMeterTestManager.setRemoteConfig(this.remoteConfig);
        try {
            jMeterTestManager.setExitCheckPause(Integer.parseInt(this.pluginProperties.getPropertyObject(JMeterPropertiesFiles.JMETER_PROPERTIES).getProperty("jmeter.exit.check.pause")));
        } catch (Exception ex) {
            getLog().warn("Unable to parse the 'jmeter.exit.check.pause' entry in jmeter.properties!  Falling back to a default value of '" + jMeterTestManager.getExitCheckPause() + "'.");
        }
        getLog().info(" ");
        getLog().info(this.proxyConfig.toString());
        List<String> testResults = jMeterTestManager.executeTests();
        new ReportGenerator(this.reportConfig).makeReport(testResults);
        parseTestResults(testResults);
    }

    /**
     * Scan JMeter result files for "error" and "failure" messages
     *
     * @param results List of JMeter result files.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    protected void parseTestResults(List<String> results) throws MojoExecutionException, MojoFailureException {
        ErrorScanner scanner = new ErrorScanner(this.ignoreResultErrors, this.ignoreResultFailures);
        int totalErrorCount = 0;
        int totalFailureCount = 0;
        boolean failed = false;
        try {
            for (String file : results) {
                if (!scanner.hasTestPassed(new File(file))) {
                    totalErrorCount = +scanner.getErrorCount();
                    totalFailureCount = +scanner.getFailureCount();
                    failed = true;
                }
            }
            getLog().info(" ");
            getLog().info("Test Results:");
            getLog().info(" ");
            getLog().info("Tests Run: " + results.size() + ", Failures: " + totalFailureCount + ", Errors: " + totalErrorCount + "");
            getLog().info(" ");
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read log file", e);
        }
        if (failed) {
            if (totalErrorCount == 0) {
                throw new MojoFailureException("There were test failures.  See the jmeter logs for details.");
            } else if (totalFailureCount == 0) {
                throw new MojoFailureException("There were test errors.  See the jmeter logs for details.");
            } else {
                throw new MojoFailureException("There were test errors and failures.  See the jmeter logs for details.");
            }
        }
    }
}