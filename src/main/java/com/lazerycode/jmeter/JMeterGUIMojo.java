package com.lazerycode.jmeter;

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
     * Run all the JMeter tests.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
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
        
        // TODO: There has to be a better way to figure out when JMeter GUI closes
        // JMeter is now running asynchronously.
        // If we just keep going it will be closed immediately because its thread is daemon.
        // Instead we want to wait until the GUI is closed, then continue.
        // So we find the AWT-Windows thread and wait for it to finish.
        Thread awtThread = null;
        Set<Thread> threadSet = Thread.getAllStackTraces ( ).keySet ( );
        for ( Thread thread : threadSet )
        {
           if ( "AWT-Windows".equals ( thread.getName ( ) ) || "AWT-AppKit".equals(thread.getName()) )
           {
              awtThread = thread;
              break;
           }
        }
        if ( awtThread != null )
        {
           try
           {
              awtThread.join ( );
           }
           catch ( InterruptedException e )
           {
              e.printStackTrace ( );
           }
        }
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