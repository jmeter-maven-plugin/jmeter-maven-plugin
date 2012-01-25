package com.lazerycode.jmeter.testExecution;

import com.lazerycode.jmeter.JMeterArgumentsArray;
import com.lazerycode.jmeter.Utilities;
import org.apache.jmeter.JMeter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class TestManager {

    private JMeterArgumentsArray testArgs;
    private Log log;
    private File jmeterLog;
    private File logsDir;
    private File srcDir;
    private static Utilities util = new Utilities();

    public TestManager(JMeterArgumentsArray testArgs, File logsDir, File srcDir, Log log) {
        this.testArgs = testArgs;
        this.logsDir = logsDir;
        this.srcDir = srcDir;
        this.log = log;
    }

    public List<String> executeTests(List<String> tests) throws MojoExecutionException {
        List<String> results = new ArrayList<String>();
        for (String file : tests) {
            results.add(executeSingleTest(new File(srcDir, file)));
        }
        return results;
    }

    /**
     * Executes a single JMeter test by building up a list of command line
     * parameters to pass to JMeter.start().
     *
     * @param test JMeter test XML
     * @return the report file names.
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          Exception
     */
    private String executeSingleTest(File test) throws MojoExecutionException {

        try {
            log.info("Executing test: " + test.getCanonicalPath());
            testArgs.setTestFile(test);
            //Delete results file if it already exists
            new File(testArgs.getResultsFilename()).delete();
            log.info(testArgs.getProxyDetails());
            if (log.isDebugEnabled()) {
                log.debug("JMeter is called with the following command line arguments: " + util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()));
            }

            // This mess is necessary because JMeter likes to use System.exit.
            // We need to trap the exit call.

            //TODO Investigate the use of a listener here (Looks like JMeter reports startup and shutdown to a listener when it finishes a test...
            SecurityManager oldManager = System.getSecurityManager();
            System.setSecurityManager(new SecurityManager() {

                @Override
                public void checkExit(int status) {
                    throw new ExitException(status);
                }

                @Override
                public void checkPermission(Permission perm, Object context) {
                }

                @Override
                public void checkPermission(Permission perm) {
                }
            });
            Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                public void uncaughtException(Thread t, Throwable e) {
                    if (e instanceof ExitException && ((ExitException) e).getCode() == 0) {
                        return; // Ignore
                    }
                    log.error("Error in thread " + t.getName());
                }
            });
            try {
                // This mess is necessary because the only way to know when
                // JMeter is done is to wait for its test end message!                
                setJMeterLogFile(test.getName() + ".log");
                new JMeter().start(testArgs.buildArgumentsArray());
                BufferedReader in = new BufferedReader(new FileReader(jmeterLog));
                while (!checkForEndOfTest(in)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } catch (ExitException e) {
                if (e.getCode() != 0) {
                    throw new MojoExecutionException("Test failed", e);
                }
            } finally {
                System.setSecurityManager(oldManager);
                Thread.setDefaultUncaughtExceptionHandler(oldHandler);
            }

            return testArgs.getResultsFilename();
        } catch (IOException e) {
            throw new MojoExecutionException("Can't execute test", e);
        }
    }

    /**
     * Check JMeter logfile (provided as a BufferedReader) for End message.
     *
     * @param in JMeter logfile
     * @return true if test ended
     * @throws MojoExecutionException exception
     */
    private boolean checkForEndOfTest(BufferedReader in) throws MojoExecutionException {
        boolean testEnded = false;
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("Test has ended")) {
                    testEnded = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read log file", e);
        }
        return testEnded;
    }

    /**
     * Create the jmeter.log file and set the log_file system property for JMeter to pick up
     *
     * @param value
     */
    private void setJMeterLogFile(String value) {
        this.jmeterLog = new File(this.logsDir + File.separator + value);
        System.setProperty("log_file", this.jmeterLog.getAbsolutePath());
    }


}
