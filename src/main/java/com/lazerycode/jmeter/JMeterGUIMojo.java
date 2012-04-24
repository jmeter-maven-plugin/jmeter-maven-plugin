package com.lazerycode.jmeter;

import com.lazerycode.jmeter.testrunner.ExitException;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.jmeter.NewDriver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.PrintStream;

/**
 * JMeter Maven plugin.
 *
 * @author Jarrod Ribble
 * @goal gui
 * @requiresProject true
 */
public class JMeterGUIMojo extends JMeterAbstractMojo {

    /**
     * Constructor will be called by maven
     */
    public JMeterGUIMojo() {
        threadNames.add(GUI_THREAD_WINDOWS);
        threadNames.add(GUI_THREAD_MACOSX);
    }

    /**
     * Run all the JMeter tests.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" STARTING JMETER GUI");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
        generateJMeterDirectoryTree();
        propertyConfiguration();
        setJMeterClasspath();
        initialiseJMeterArgumentsArray();
        SecurityManager originalSecurityManager = overrideSecurityManager();
        try {
            getLog().info("JMeter is called with the following command line arguments: " + UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray(false)));
            //start GUI
            NewDriver.main(testArgs.buildArgumentsArray(false));
            waitForTestToFinish(threadNames);
        } catch (InterruptedException e) {
            getLog().info(" ");
            getLog().info("System Exit Detected!  Shutting GUI Down...");
            getLog().info(" ");
        } finally {
            //Reset everything back to normal
            System.setSecurityManager(originalSecurityManager);
        }

    }

    /**
     * Generate the initial JMeter Arguments array that is used to create the command line that we pass to JMeter.
     *
     * @throws MojoExecutionException
     */
    @Override
    protected void initialiseJMeterArgumentsArray() throws MojoExecutionException {
        super.initialiseJMeterArgumentsArray();
        this.testArgs.setShowGUI(true);
    }
}