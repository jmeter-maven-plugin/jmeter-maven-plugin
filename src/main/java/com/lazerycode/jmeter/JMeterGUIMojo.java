package com.lazerycode.jmeter;

import org.apache.jmeter.NewDriver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * JMeter Maven plugin.
 *
 * @author Jarrod Ribble
 * @goal gui
 * @requiresProject true
 */
public class JMeterGUIMojo extends JMeterAbstractMojo {

    /**
     * Load the JMeter GUI
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
        populateJMeterDirectoryTree();
        initialiseJMeterArgumentsArray();
        SecurityManager originalSecurityManager = overrideSecurityManager();
        try {
            getLog().info("JMeter is called with the following command line arguments: " + UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray(false)));
            //start GUI
            NewDriver.main(testArgs.buildArgumentsArray(false));
            waitForTestToFinish(UtilityFunctions.getThreadNames(true));
        } catch (InterruptedException e) {
            getLog().info(" ");
            getLog().info("Thread Interrupt Detected!  Shutting GUI Down...");
            getLog().info("(Any interrupt stack trace after this point is expected)");
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