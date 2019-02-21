package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;
import com.lazerycode.jmeter.testrunner.JMeterProcessBuilder;
import com.lazerycode.jmeter.utility.UtilityFunctions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
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
public class RunJMeterServer extends AbstractJMeterMojo {

    @Parameter(defaultValue = "false")
    private boolean runInBackground;

    /**
     * Port to listen on
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
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void doExecute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" ");
        getLog().info(LINE_SEPARATOR);
        getLog().info(" STARTING JMETER SERVER ON PORT:" + serverPort + " WITH EXPORTED HOSTNAME:" + exportedRmiHostname);
        getLog().info(LINE_SEPARATOR);
        JMeterArgumentsArray testArgs = initializeJMeterArgumentsArray();
        getLog().debug("JMeter is called with the following command line arguments: " +
                UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()));
        startJMeterServer(testArgs);
    }

    private JMeterArgumentsArray initializeJMeterArgumentsArray() throws MojoExecutionException {
        JMeterArgumentsArray currentTestArgs = new JMeterArgumentsArray(false, jmeterDirectory.getAbsolutePath());
        currentTestArgs.setProxyConfig(proxyConfig);
        for (File customPropertiesFile : customPropertiesFiles) {
            currentTestArgs.setACustomPropertiesFile(customPropertiesFile);
        }
        currentTestArgs.setLogRootOverride(overrideRootLogLevel);
        currentTestArgs.setLogsDirectory(logsDirectory.getAbsolutePath());
        currentTestArgs.setServerMode(exportedRmiHostname, serverPort);

        return currentTestArgs;
    }

    private void startJMeterServer(JMeterArgumentsArray testArgs) throws MojoExecutionException {
        JMeterProcessJVMSettings jMeterProcessJVMSettings;
        if (null == this.jMeterProcessJVMSettings) {
            jMeterProcessJVMSettings = new JMeterProcessJVMSettings();
        } else {
            jMeterProcessJVMSettings = new JMeterProcessJVMSettings(this.jMeterProcessJVMSettings);
        }
        if (null != exportedRmiHostname&& !exportedRmiHostname.isEmpty()) {
            jMeterProcessJVMSettings.getArguments().add("-Djava.rmi.server.hostname=" + exportedRmiHostname);
        }
        if (!containsHeadless(jMeterProcessJVMSettings)) {
            jMeterProcessJVMSettings.getArguments().add(RUN_HEADLESS_OPT);
        }
        if (null == serverPort) {
            throw new MojoExecutionException("serverPort is null, cannot start jmeter server");
        }
        jMeterProcessJVMSettings.getArguments().add("-Dserver_port=" + serverPort);

        JMeterProcessBuilder jmeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings,
                JMeterConfigurationHolder.getInstance().getRuntimeJarName());
        jmeterProcessBuilder.setWorkingDirectory(JMeterConfigurationHolder.getInstance().getWorkingDirectory());
        jmeterProcessBuilder.addArguments(testArgs.buildArgumentsArray());
        try {
            final Process process = jmeterProcessBuilder.startProcess();
            if (!runInBackground) {
                process.waitFor();
            }
        } catch (InterruptedException ex) {
            getLog().info(" ");
            getLog().info("System Exit Detected!  Stopping GUI...");
            getLog().info(" ");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            getLog().error("Error starting JMeter with args " + testArgs.buildArgumentsArray()
                            + ", in working directory:" + JMeterConfigurationHolder.getInstance().getWorkingDirectory()
                    , e);
        }
    }
}