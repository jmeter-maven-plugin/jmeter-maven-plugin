package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.json.TestConfigurationWrapper;
import com.lazerycode.jmeter.testrunner.ResultScanner;
import com.lazerycode.jmeter.testrunner.TestFailureDecider;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal that computes successes/failures from CSV or XML results files.<br/>
 * This goal runs within Lifecycle phase {@link LifecyclePhase#VERIFY}.<br/>
 * Ensure you set 'scanResultsForSuccessfulRequests' and 'scanResultsForFailedRequests' to true.
 */
@Mojo(name = "results", defaultPhase = LifecyclePhase.VERIFY)
public class CheckResultsMojo extends AbstractJMeterMojo {

    /**
     * Sets whether build should fail if there are failed requests found in the JMeter result file.
     * Failures are for example failed requests
     */
    @Parameter(defaultValue = "false")
    protected boolean ignoreResultFailures;

    /**
     * Sets whether ResultScanner should search for failed requests in the JMeter result file.
     * Defaults to false
     */
    @Parameter(defaultValue = "true")
    protected boolean scanResultsForFailedRequests;

    /**
     * Sets the error rate threshold limit for build to get failed, i.e if its set to 3 then build fails only if
     * the % of failed requests are above 3
     * defaults to 0
     */
    @Parameter(defaultValue = "0")
    protected float errorRateThresholdInPercent;

    /**
     * Sets whether ResultScanner should search for Successful requests in the JMeter result file.
     * Defaults to false
     */
    @Parameter(defaultValue = "true")
    protected boolean scanResultsForSuccessfulRequests;

    /**
     * Only search for specific failure messages when scanning results for failed requests (only applied to CSV files)
     * Defaults to false
     */
    @Parameter(defaultValue = "false")
    protected boolean onlyFailWhenMatchingFailureMessage;

    /**
     * list of case insensitive failure messages to search for.
     * (Requires <onlyFailWhenMatchingFailureMessage>true</onlyFailWhenMatchingFailureMessage> to be set)
     */
    @Parameter
    protected List<String> failureMessages = new ArrayList<>();

    /**
     * Scan JMeter result files for successful, and failed requests/
     *
     * @throws MojoExecutionException Exception
     * @throws MojoFailureException   Exception
     */
    @Override
    public void doExecute() throws MojoExecutionException, MojoFailureException {
        if (!ignoreResultFailures && !scanResultsForFailedRequests) {
            getLog().warn(String.format(
                    "current value of scanResultsForFailedRequests(%s) is incompatible with ignoreResultFailures(%s), setting scanResultsForFailedRequests to true",
                    scanResultsForFailedRequests,
                    ignoreResultFailures
            ));
            scanResultsForFailedRequests = true;
        }
        if (scanResultsForSuccessfulRequests || scanResultsForFailedRequests) {
            getLog().info(" ");
            getLog().info(LINE_SEPARATOR);
            getLog().info("S C A N N I N G    F O R    R E S U L T S");
            getLog().info(LINE_SEPARATOR);
            getLog().info(" ");
            TestConfigurationWrapper testConfig = new TestConfigurationWrapper(new File(testConfigFile), selectedConfiguration);
            String resultFormat = testConfig.getCurrentTestConfiguration().getResultsOutputIsCSVFormat() ? "CSV" : "JTL";
            getLog().info(String.format("Will scan results using format: %s", resultFormat));
            ResultScanner resultScanner = new ResultScanner(
                    scanResultsForSuccessfulRequests,
                    scanResultsForFailedRequests,
                    testConfig.getCurrentTestConfiguration().getResultsOutputIsCSVFormat(),
                    onlyFailWhenMatchingFailureMessage,
                    failureMessages
            );
            for (String resultFileLocation : testConfig.getCurrentTestConfiguration().getResultFilesLocations()) {
                resultScanner.parseResultFile(new File(resultFileLocation));
            }
            getLog().info(" ");
            getLog().info(LINE_SEPARATOR);
            getLog().info("P E R F O R M A N C E    T E S T    R E S U L T S");
            getLog().info(LINE_SEPARATOR);
            getLog().info(" ");
            getLog().info(String.format("Result (.%s) files scanned: %s", resultFormat.toLowerCase(), testConfig.getCurrentTestConfiguration().getResultFilesLocations().size()));
            getLog().info(String.format("Successful requests:         %s", resultScanner.getSuccessCount()));
            getLog().info(String.format("Failed requests:             %s", resultScanner.getFailureCount()));
            TestFailureDecider decider = new TestFailureDecider(ignoreResultFailures, errorRateThresholdInPercent, resultScanner);
            decider.runChecks();
            getLog().info(String.format("Failures:                    %s%% (%s%% accepted)", decider.getErrorPercentage(), decider.getErrorPercentageThreshold()));
            getLog().info(" ");
            if (decider.failBuild()) {
                throw new MojoFailureException(String.format(
                        "Failing build because error percentage %s is above accepted threshold %s. JMeter logs are available at: '%s'",
                        logsDirectory.getAbsolutePath(),
                        decider.getErrorPercentage(),
                        decider.getErrorPercentageThreshold()
                ));
            }
        } else {
            getLog().info(" ");
            getLog().info("Results of Performance Test(s) have not been scanned.");
            getLog().info(" ");
        }
    }
}
