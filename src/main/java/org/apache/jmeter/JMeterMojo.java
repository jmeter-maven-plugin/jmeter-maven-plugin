package org.apache.jmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.Permission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tools.ant.DirectoryScanner;

/**
 * JMeter Maven plugin.
 *
 * @author Tim McCune
 * @goal jmeter
 */
public class JMeterMojo extends AbstractMojo {

    private static final Pattern PAT_ERROR = Pattern.compile(".*\\s+ERROR\\s+.*");

    // TODO Provide the same for excludes!
    /**
     * @parameter expression="${jmeter.includefiles}"
     */
    private String includeFiles;

    /**
     * @parameter 
     */
    private List<String> includes;

    /**
     * @parameter
     */
    private List<String> excludes;

    /**
     * @parameter expression="${basedir}/src/test/jmeter"
     */
    private File srcDir;

    /**
     * @parameter expression="jmeter-reports"
     */
    private File reportDir;

    /**
     * @parameter expression="${basedir}/src/test/jmeter/jmeter.properties"
     */
    private File jmeterProps;

    /**
     * @parameter expression="${settings.localRepository}"
     */
    private File repoDir;

    /**
     * JMeter Properties to be overridden
     *
     * @parameter
     */
    private Map jmeterUserProperties;

    /**
     * @parameter
     */
    private boolean remote;

    /**
     * @parameter expression="${project}"
     */
    private org.apache.maven.project.MavenProject mavenProject;


    private File workDir;
    private File saveServiceProps;
    private File upgradeProps;
    private File jmeterLog;
    private DateFormat fmt = new SimpleDateFormat("yyMMdd");

    /**
     * Run all JMeter tests.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        initSystemProps();
        try {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(srcDir);
            if (null != includeFiles) {
            	if (null != includes) {
            		getLog().debug("Overwriting configured includes list by includefiles = '" + includeFiles + "'");
            	} else {
            		getLog().debug("Using includefiles = '" + includeFiles + "'");
            	}
            	scanner.setIncludes(new String[]{includeFiles});
            } else if (null == includes) {
            	getLog().debug("Using default includes");
            	scanner.setIncludes(new String[]{"**/*.jmx"});
            } else {
            	getLog().debug("Using configured includes");
            	scanner.setIncludes(includes.toArray(new String[]{}));
            }
            if (excludes != null) {
                scanner.setExcludes(excludes.toArray(new String[]{}));
            }
            scanner.scan();
            String[] finalIncludes = scanner.getIncludedFiles();
            getLog().debug("Finally using test files" + StringUtils.join(finalIncludes, ", "));
            for (String file : finalIncludes) {
                executeTest(new File(srcDir, file));
            }
            checkForErrors();
        } finally {
            saveServiceProps.delete();
            upgradeProps.delete();
        }
    }

    private void checkForErrors() throws MojoExecutionException, MojoFailureException {
        try {
            BufferedReader in = new BufferedReader(new FileReader(jmeterLog));
            String line;
            while ((line = in.readLine()) != null) {
                if (PAT_ERROR.matcher(line).find()) {
                    throw new MojoFailureException("There were test errors, see logfile '" + jmeterLog + "' for further information");
                }
            }
            in.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read log file", e);
        }
    }

    private void initSystemProps() throws MojoExecutionException {
        workDir = new File("target" + File.separator + "jmeter");
        workDir.mkdirs();
        createSaveServiceProps();
        jmeterLog = new File(workDir, "jmeter.log");
        try {
            System.setProperty("log_file", jmeterLog.getCanonicalPath());
        } catch (IOException e) {
            throw new MojoExecutionException("Can't get canonical path for log file", e);
        }
    }

    /**
     * This mess is necessary because JMeter must load this info from a file.
     * Do the same for the upgrade.properties
     * Resources won't work.
     */
    private void createSaveServiceProps() throws MojoExecutionException {
        saveServiceProps = new File(workDir, "saveservice.properties");
        upgradeProps = new File(workDir, "upgrade.properties");
        try {
            FileWriter out = new FileWriter(saveServiceProps);
            IOUtils.copy(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("saveservice.properties"), out);
            out.flush();
            out.close();
            System.setProperty("saveservice_properties",
                    File.separator + "target" + File.separator + "jmeter" + File.separator +
                            "saveservice.properties");

            out = new FileWriter(upgradeProps);
            IOUtils.copy(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("upgrade.properties"), out);
            out.flush();
            out.close();
            System.setProperty("upgrade_properties",
                    File.separator + "target" + File.separator + "jmeter" + File.separator +
                            "upgrade.properties");


            System.setProperty("search_paths", repoDir.toString() + "/org/apache/jmeter/jmeter/2.3/jmeter-2.3.jar");
        } catch (IOException e) {
            throw new MojoExecutionException("Could not create temporary saveservice.properties", e);
        }
    }

    /**
     * Executes a single JMeter test by building up a list of command line
     * parameters to pass to JMeter.start().
     */
    private void executeTest(File test) throws MojoExecutionException {
        try {
            getLog().info("Executing test: " + test.getCanonicalPath());
            String reportFileName = test.getName().substring(0,
                    test.getName().lastIndexOf(".")) + "-" +
                    fmt.format(new Date()) + ".xml";
            List<String> argsTmp = Arrays.asList("-n",
                    "-t", test.getCanonicalPath(),
                    "-l", reportDir.toString() + File.separator + reportFileName,
                    "-p", jmeterProps.toString(),
                    "-d", System.getProperty("user.dir"));

            List<String> args = new ArrayList<String>();
            args.addAll(argsTmp);
            args.addAll(getUserProperties());
            if (remote) {
                args.add("-r");
            }
            // This mess is necessary because JMeter likes to use System.exit.
            // We need to trap the exit call.
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
            UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    if (e instanceof ExitException && ((ExitException) e).getCode() == 0) {
                        return;    //Ignore
                    }
                    getLog().error("Error in thread " + t.getName());
                }
            });
            try {
                // This mess is necessary because the only way to know when JMeter
                // is done is to wait for its test end message!
            	logParamsAndProps(args);
                new JMeter().start(args.toArray(new String[]{}));
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
        } catch (IOException e) {
            throw new MojoExecutionException("Can't execute test", e);
        }
    }

	private void logParamsAndProps(List<String> args) {
		getLog().debug ("Starting JMeter with the following parameters:");
		for (String arg : args) {
			getLog().debug(arg);
		}
		Properties props = System.getProperties();
		Set<Object> keysUnsorted = props.keySet();
		SortedSet<Object> keys = new TreeSet<Object> (keysUnsorted);
		getLog().debug("... and the following properties:");
		for (Object k : keys) {
		        String key = (String) k;
		        String value = props.getProperty(key);
		        getLog().debug(key + " = " + value);
		}
	}

    private boolean checkForEndOfTest(BufferedReader in) throws MojoExecutionException {
        boolean testEnded = false;
        try {
                String line;
                while ( (line = in.readLine()) != null) {
                        if (line.indexOf("Test has ended") != -1) {
                                testEnded = true;
                                break;
                        }
                }
        } catch (IOException e) {
                throw new MojoExecutionException("Can't read log file", e);
        }
        return testEnded;
    }

    private ArrayList<String> getUserProperties() {
        ArrayList<String> propsList = new ArrayList<String>();
        if (jmeterUserProperties == null) {
            return propsList;
        }
        Set<String> keySet = (Set<String>) jmeterUserProperties.keySet();

        for (String key : keySet) {

            propsList.add("-J");
            propsList.add(key + "=" + jmeterUserProperties.get(key));
        }

        return propsList;
    }

    private static class ExitException extends SecurityException {
        private static final long serialVersionUID = 5544099211927987521L;

        public int _rc;

        public ExitException(int rc) {
            super(Integer.toString(rc));
            _rc = rc;
        }

        public int getCode() {
            return _rc;
        }
    }

}
