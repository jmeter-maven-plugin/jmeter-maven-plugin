package com.lazerycode.jmeter;

import java.io.PrintStream;
import com.lazerycode.jmeter.properties.JMeterPropertiesFiles;
import com.lazerycode.jmeter.testrunner.ExitException;
import org.apache.commons.io.output.NullOutputStream;
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

        try {
            setExitCheckPause(Integer.parseInt(this.pluginProperties.getPropertyObject(JMeterPropertiesFiles.JMETER_PROPERTIES).getProperty("jmeter.exit.check.pause")));
        }
        catch (Exception ex) {
            //TODO: is this really worth a warning if the property isn't set by the user?
            getLog().warn("Unable to parse the 'jmeter.exit.check.pause' entry in jmeter.properties!  Falling back to a default value of '" + getExitCheckPause() + "'.");
        }


      SecurityManager originalSecurityManager = overrideSecurityManager();
      Thread.UncaughtExceptionHandler originalExceptionHandler = overrideUncaughtExceptionHandler();
      PrintStream originalOut = System.out;
      try {
          //Suppress JMeter's annoying System.out messages.
          if (suppressJMeterOutput) System.setOut(new PrintStream(new NullOutputStream()));
          getLog().info("JMeter is called with the following command line arguments: " + UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray(false)));

          //start GUI
          NewDriver.main(testArgs.buildArgumentsArray(false));

          waitForTestToFinish(threadNames);

          //TODO: for some reason, catching the ExitException doesn't work like it does for the TestManager. Or maybe it never did...
        /**
         *  at the moment, the plugin dies after closing the GUI with the message:
         * "Uncaught Exception com.lazerycode.jmeter.testrunner.ExitException: 0. See log file for details."
         * output from jmeter.log:
         *
         *  ERROR - jmeter.JMeter: Uncaught exception:  com.lazerycode.jmeter.testrunner.ExitException: 0
         	at com.lazerycode.jmeter.JMeterAbstractMojo$1.checkExit(JMeterAbstractMojo.java:388)
         	at java.lang.Runtime.exit(Runtime.java:88)
         	at java.lang.System.exit(System.java:921)
         	at com.apple.eawt._AppEventHandler.performQuit(_AppEventHandler.java:124)
         	at com.apple.eawt.QuitResponse.performQuit(QuitResponse.java:31)
         	at com.apple.eawt._AppEventHandler$_QuitDispatcher.performDefaultAction(_AppEventHandler.java:382)
         	at com.apple.eawt._AppEventHandler$_AppEventDispatcher$1.run(_AppEventHandler.java:487)
         	at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:209)
         	at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:682)
         	at java.awt.EventQueue.access$000(EventQueue.java:85)
         	at java.awt.EventQueue$1.run(EventQueue.java:643)
         	at java.awt.EventQueue$1.run(EventQueue.java:641)
         	at java.security.AccessController.doPrivileged(Native Method)
         	at java.security.AccessControlContext$1.doIntersectionPrivilege(AccessControlContext.java:87)
         	at java.awt.EventQueue.dispatchEvent(EventQueue.java:652)
         	at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:296)
         	at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:211)
         	at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:201)
         	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:196)
         	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:188)
         	at java.awt.EventDispatchThread.run(EventDispatchThread.java:122)
         */
      }
      catch (ExitException e) {
          if (e.getCode() != 0) {
              throw new MojoExecutionException("Test failed", e);
          }
      }
      finally {
          try {
              //Wait for JMeter to clean up threads.
              Thread.sleep(this.exitCheckPause);
          }
          catch (InterruptedException e) {
              getLog().warn("Something went wrong during Thread cleanup, we may be leaving something running...");
          }
          //Reset everything back to normal
          System.setSecurityManager(originalSecurityManager);
          Thread.setDefaultUncaughtExceptionHandler(originalExceptionHandler);
          System.setOut(originalOut);
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