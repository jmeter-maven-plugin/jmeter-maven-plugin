package com.lazerycode.jmeter;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import com.lazerycode.jmeter.testrunner.TestManager;

/**
 * JMeter Maven plugin.
 *
 * @author Tim McCune
 * @goal jmeter
 * @requiresProject true
 */
@SuppressWarnings("JavaDoc")
public class JMeterMojo extends JMeterAbstractMojo {

    /**
     * Run all the JMeter tests.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(skipTests){
            getLog().info(" ");
            getLog().info("-------------------------------------------------------");
            getLog().info(" S K I P P I N G    P E R F O R M A N C E    T E S T S ");
            getLog().info("-------------------------------------------------------");
            getLog().info(" ");
            return;
        }
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" P E R F O R M A N C E    T E S T S");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
        generateJMeterDirectoryTree();
        propertyConfiguration();
        populateJMeterDirectoryTree();
        initialiseJMeterArgumentsArray();
        TestManager jMeterTestManager = new TestManager(this.testArgs, this.logsDir, this.testFilesDirectory, this.testFilesIncluded, this.testFilesExcluded, this.suppressJMeterOutput);
        jMeterTestManager.setRemoteConfig(this.remoteConfig);
        getLog().info(" ");
        getLog().info(this.proxyConfig.toString());
        List<String> testResults = jMeterTestManager.executeTests();
        parseTestResults(testResults);

        // JMeter sets this system property. to "org.apache.commons.logging.impl.LogKitLogger".
        // If another plugin is executed after this plugin that also uses (a third-party library that uses) commons-logging, but doesn't supply the same logger, execution will fail.
        // TODO: may not work if SecurityManager is enabled. Needs PropertyPermission "key", "read,write" to work.
        System.clearProperty("org.apache.commons.logging.Log");
    }

    /**
     * Scan JMeter result files for "error" and "failure" messages
     *
     * @param results List of JMeter result files.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    protected void parseTestResults(List<String> results) throws MojoExecutionException, MojoFailureException {
        ErrorScanner scanner = new ErrorScanner(this.ignoreResultErrors, this.ignoreResultFailures, getLog());
        int totalErrorCount = 0;
        int totalFailureCount = 0;
        boolean failed = false;
        for (String file : results) {
            if (!scanner.hasTestPassed(new File(file))) {
                totalErrorCount += scanner.getErrorCount();
                totalFailureCount += scanner.getFailureCount();
                failed = true;
            }
        }
        getLog().info(" ");
        getLog().info("Test Results:");
        getLog().info(" ");
        getLog().info("Tests Run: " + results.size() + ", Failures: " + totalFailureCount + ", Errors: " + totalErrorCount + "");
        getLog().info(" ");
        if (failed) {
            throw new MojoFailureException("There were " + totalErrorCount + " test errors " + "and " + totalFailureCount + " test failures.  See the JMeter logs for details.");
        }
    }
}