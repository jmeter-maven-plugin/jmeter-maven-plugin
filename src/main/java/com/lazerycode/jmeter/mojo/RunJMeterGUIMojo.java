package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.json.TestConfigurationWrapper;
import com.lazerycode.jmeter.testrunner.JMeterProcessBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Goal that runs JMeter in GUI mode.<br>
 * This goal runs within Lifecycle phase {@link LifecyclePhase#INTEGRATION_TEST}.
 *
 * @author Jarrod Ribble
 */
@Mojo(name = "gui", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class RunJMeterGUIMojo extends AbstractJMeterMojo {

    /**
     * Run the process in the background.
     * This process will continue to run once the maven build has completed unless you manually find it and kill it!
     */
    @Parameter(defaultValue = "false")
    private boolean runInBackground;

    /**
     * Supply a test file to open in the GUI once it is loaded.
     */
    @Parameter(defaultValue = "${guiTestFile}")
    private File guiTestFile;

    /**
     * Load the JMeter GUI
     *
     * @throws MojoExecutionException MojoExecutionException
     */
    @Override
    public void doExecute() throws MojoExecutionException {
        getLog().info(" ");
        getLog().info(LINE_SEPARATOR);
        getLog().info(" S T A R T I N G    J M E T E R    G U I ");
        getLog().info(LINE_SEPARATOR);
        testConfig = new TestConfigurationWrapper(new File(testConfigFile), this.mojoExecution.getExecutionId());
        startJMeterGUI(initialiseJMeterArgumentsArray());
    }

    private JMeterArgumentsArray initialiseJMeterArgumentsArray() throws MojoExecutionException {
        return computeJMeterArgumentsArray(false, testConfig.getCurrentTestConfiguration().getResultsOutputIsCSVFormat(), testConfig.getCurrentTestConfiguration().getJmeterDirectoryPath()).setTestFile(guiTestFile, testFilesDirectory);
    }

    private void startJMeterGUI(JMeterArgumentsArray testArgs) throws MojoExecutionException {
        JMeterProcessBuilder jmeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, testConfig.getCurrentTestConfiguration().getRuntimeJarName())
                .setWorkingDirectory(new File(testConfig.getCurrentTestConfiguration().getJmeterWorkingDirectoryPath()))
                .addArguments(testArgs.buildArgumentsArray());
        try {
            final Process process = jmeterProcessBuilder.build().start();
            if (runInBackground) {
                getLog().info(" ");
                getLog().info(" Starting JMeter GUI process in the background...");
                //TODO log process using process.pid() when Java 9 is the minimum supported version
            } else {
                process.waitFor();
            }
        } catch (InterruptedException ex) {
            getLog().info(" ");
            getLog().info("System Exit detected!  Stopping GUI process...");
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
