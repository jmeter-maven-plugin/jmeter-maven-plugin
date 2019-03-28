package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.testrunner.JMeterProcessBuilder;
import com.lazerycode.jmeter.utility.UtilityFunctions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

/**
 * Goal that runs JMeter in server mode.<br/>
 * This goal runs within Lifecycle phase {@link LifecyclePhase#TEST}.
 *
 * @author Philippe Mouawad
 * @since 2.5.0
 */
@Mojo(name = "remote-server", defaultPhase = LifecyclePhase.TEST)
@Execute(goal = "configure")
public class RunJMeterServerMojo extends AbstractJMeterMojo {

    /**
     * Run the process in the background.
     * This process will continue to run once the maven build has completed unless you manually find it and kill it!
     */
    @Parameter(defaultValue = "false")
    private boolean runInBackground;

    /**
     * Port JMeter Server will listen on
     */
    @Parameter(defaultValue = "1099")
    private Integer serverPort;

    /**
     * Exported RMI host name
     */
    @Parameter(defaultValue = "localhost")
    private String exportedRmiHostname;

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
        getLog().info(" Host:" + exportedRmiHostname);
        getLog().info(" Port:" + serverPort);
        JMeterArgumentsArray testArgs = initializeJMeterArgumentsArray();
        getLog().debug(String.format("JMeter is called with the following command line arguments: %s",
                UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray())));
        startJMeterServer(testArgs);
    }

    private JMeterArgumentsArray initializeJMeterArgumentsArray() throws MojoExecutionException {
        return new JMeterArgumentsArray(false, jmeterDirectory.getAbsolutePath())
                .setProxyConfig(proxyConfig)
                .addACustomPropertiesFiles(customPropertiesFiles)
                .setLogRootOverride(overrideRootLogLevel)
                .setLogsDirectory(logsDirectory.getAbsolutePath())
                .setServerMode(exportedRmiHostname, serverPort);
    }

    private void startJMeterServer(JMeterArgumentsArray testArgs) throws MojoExecutionException {
        jMeterProcessJVMSettings.setHeadlessDefaultIfRequired()
                .addArgument("-Djava.rmi.server.hostname=" + exportedRmiHostname)
                .addArgument("-Dserver_port=" + serverPort);

        JMeterProcessBuilder jmeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, JMeterConfigurationHolder.getInstance().getRuntimeJarName())
                .setWorkingDirectory(JMeterConfigurationHolder.getInstance().getWorkingDirectory())
                .addArguments(testArgs.buildArgumentsArray());
        try {
            final Process process = jmeterProcessBuilder.build().start();
            if (runInBackground) {
                getLog().info(" ");
                getLog().info(" Starting JMeter server process in the background...");
                //TODO log process using process.pid() when Java 9 is the minimum supported version
            } else {
                process.waitFor();
            }
        } catch (InterruptedException ex) {
            getLog().info(" ");
            getLog().info("System Exit detected!  Stopping server process...");
            getLog().info(" ");
            Thread.currentThread().interrupt();
        } catch (IOException ioException) {
            getLog().error(
                    String.format("Error starting JMeter with args %s, in working directory: %s",
                            testArgs.buildArgumentsArray(),
                            JMeterConfigurationHolder.getInstance().getWorkingDirectory()),
                    ioException);
        }
    }
}