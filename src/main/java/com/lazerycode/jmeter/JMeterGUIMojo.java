package com.lazerycode.jmeter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jmeter.JMeter;
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

        getLog().info("JMeter is called with the following command line arguments: " + UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray(false)));
        NewDriver.main(testArgs.buildArgumentsArray(false));

        waitForTestToFinish(threadNames);
    }

    /**
     * Generate the initial JMeter Arguments array that is used to create the command line that we pass to JMeter.
     *
     * @throws MojoExecutionException
     */
    @Override
    protected void initialiseJMeterArgumentsArray() throws MojoExecutionException {
        super.initialiseJMeterArgumentsArray ( );
        this.testArgs.setShowGUI ( true );
    }
}