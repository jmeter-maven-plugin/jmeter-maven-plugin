package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.json.TestConfigurationWrapper;
import com.lazerycode.jmeter.testrunner.JMeterProcessBuilder;
import com.lazerycode.jmeter.utility.StreamRedirector;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Goal that runs JMeter in server mode.<br>
 * This goal runs within Lifecycle phase {@link LifecyclePhase#INTEGRATION_TEST}.
 *
 * @author Philippe Mouawad
 * @since 2.5.0
 */
@Mojo(name = "remote-server", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class RunJMeterServerMojo extends AbstractJMeterMojo {

    /**
     * Run the process in the background.
     * This process will continue to run once the maven build has completed unless you manually find it and kill it!
     */
    @Parameter(defaultValue = "false", property = "background")
    private boolean runInBackground;

    /**
     * Port JMeter Server will listen on
     */
    @Parameter(defaultValue = "1099", property = "serverPort")
    private Integer serverPort;

    /**
     * Exported RMI host name
     */
    @Parameter(defaultValue = "localhost", property = "rmiHostname")
    private String exportedRmiHostname;

    /**
     * Disable SSL
     */
    @Parameter(defaultValue = "false", property = "rmiSSLDisable")
    private Boolean disableSSL;

    public static final String CLI_CONFIG_EXECUTION_ID = "default-cli";

    /**
     * Load the JMeter server
     *
     * @throws MojoExecutionException MojoExecutionException
     */
    @Override
    public void doExecute() throws MojoExecutionException {
        getLog().info(" ");
        getLog().info(LINE_SEPARATOR);
        getLog().info(" S T A R T I N G    J M E T E R    S E R V E R ");
        getLog().info(LINE_SEPARATOR);
        getLog().info(String.format(" Host: %s", exportedRmiHostname));
        getLog().info(String.format(" Port: %s", serverPort));
        if (this.mojoExecution.getExecutionId().equals(CLI_CONFIG_EXECUTION_ID)) {
            testConfig = new TestConfigurationWrapper(new File(testConfigFile), CLI_CONFIG_EXECUTION_ID);
        } else {
            testConfig = new TestConfigurationWrapper(new File(testConfigFile), selectedConfiguration);
        }
        startJMeterServer(initializeJMeterArgumentsArray());
    }

    private JMeterArgumentsArray initializeJMeterArgumentsArray() throws MojoExecutionException {
        return new JMeterArgumentsArray(false, testConfig.getCurrentTestConfiguration().getJmeterDirectoryPath())
                .setProxyConfig(proxyConfig)
                .addACustomPropertiesFiles(customPropertiesFiles)
                .setLogRootOverride(overrideRootLogLevel)
                .setLogsDirectory(logsDirectory.getAbsolutePath())
                .setServerMode(exportedRmiHostname, serverPort);
    }

    private void startJMeterServer(JMeterArgumentsArray testArgs) throws MojoExecutionException {
        jMeterProcessJVMSettings.setHeadlessDefaultIfRequired()
                .addArgument(String.format("-Djava.rmi.server.hostname=%s", exportedRmiHostname))
                .addArgument(String.format("-Dserver.rmi.ssl.disable=%s", disableSSL))
                .addArgument(String.format("-Dserver_port=%s", serverPort));

        JMeterProcessBuilder jmeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, testConfig.getCurrentTestConfiguration().getRuntimeJarName())
                .setWorkingDirectory(new File(testConfig.getCurrentTestConfiguration().getJmeterDirectoryPath(), "bin"))
                .addArguments(testArgs.buildArgumentsArray());
        try {
            final Process process = jmeterProcessBuilder.build().start();
            if (runInBackground) {
                getLog().info(" ");
                getLog().info(" Starting JMeter server process in the background...");
                //TODO log process using process.pid() when Java 9 is the minimum supported version
            } else {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    getLog().info("Shutdown detected, destroying JMeter server process...");
                    getLog().info(" ");
                    process.destroy();
                }));
                new Thread(new StreamRedirector(process.getInputStream(), (suppressJMeterOutput ? getLog()::debug : getLog()::info))).start();
                new Thread(new StreamRedirector(process.getErrorStream(), getLog()::error)).start();
                int jMeterExitCode = process.waitFor();
                if (jMeterExitCode != 0) {
                    throw new MojoExecutionException("Starting JMeter server in background failed with exit code: " + jMeterExitCode);
                }
            }
        } catch (InterruptedException ex) {
            getLog().info(" ");
            getLog().info("System Exit detected!  Stopping JMeter server process...");
            getLog().info(" ");
            Thread.currentThread().interrupt();
        } catch (IOException ioException) {
            getLog().error(String.format(
                    "Error starting JMeter with args %s, in working directory: %s",
                    testArgs.buildArgumentsArray(),
                    testConfig.getCurrentTestConfiguration().getJmeterDirectoryPath()
            ), ioException);
        }
    }
}
