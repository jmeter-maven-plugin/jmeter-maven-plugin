package org.apache.jmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.Permission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;
import java.util.Set;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DirectoryScanner;

/**
 * JMeter Maven plugin.
 * 
 * @author Tim McCune 
 * @goal jmeter
 * @requiresProject true
 */
public class JMeterMojo extends AbstractMojo {

    /**
     * Path to a Jmeter test XML file.
     * Relative to srcDir.
     * May be declared instead of the parameter includes.
     *
     * @parameter
     */
    private File jmeterTestFile;
    
    /**
     * Sets the list of include patterns to use in directory scan for JMeter Test XML files.
     * Relative to srcDir.
     * May be declared instead of a single jmeterTestFile.
     * Ignored if parameter jmeterTestFile is given.
     *
     * @parameter
     */
    private List<String> includes;
    
    /**
     * Sets the list of exclude patterns to use in directory scan for Test files.
     * Relative to srcDir.
     * Ignored if parameter jmeterTestFile file is given.
     *
     * @parameter
     */
    private List<String> excludes;
    
    /**
     * Path under which JMeter test XML files are stored.
     *
     * @parameter expression="${jmeter.testfiles.basedir}"
     *          default-value="${basedir}/src/test/jmeter"
     */
    private File srcDir;
    
    /**
     * Directory in which the reports are stored.
     *
     * @parameter expression="${jmeter.reports.dir}"
     *          default-value="${basedir}/target/jmeter-report"
     */
    private File reportDir;
    
    /**
     * Whether or not to generate reports after measurement.
     * 
     * @parameter default-value="true"
     */
    private boolean enableReports;
    
    /**
     * Custom Xslt which is used to create the report.
     *
     * @parameter
     */
    private File reportXslt;
    
    /**
     * Absolute path to JMeter default properties file.
     * The default properties file is part of a JMeter installation and sets basic properties needed for running JMeter.
     *
     * @parameter expression="${jmeter.properties}"
     *          default-value="${basedir}/src/test/jmeter/jmeter.properties"
     * @required
     */
    private File jmeterDefaultPropertiesFile;
    
    /**
     * Absolute path to JMeter custom (test dependent) properties file.
     *
     * @parameter
     */
    private File jmeterCustomPropertiesFile;
    /**
     * JMeter Properties that override those given in jmeterProps
     * 
     * @parameter
     */
    @SuppressWarnings("rawtypes")
    private Map jmeterUserProperties;
    
    /**
     * Use remote JMeter installation to run tests
     *
     * @parameter default-value=false
     */
    private boolean remote;
    
    /**
     * Sets whether ErrorScanner should ignore failures in JMeter result file.
     *
     * @parameter expression="${jmeter.ignore.failure}" default-value=false
     */
    private boolean jmeterIgnoreFailure;
    
    /**
     * Sets whether ErrorScanner should ignore errors in JMeter result file.
     *
     * @parameter expression="${jmeter.ignore.error}" default-value=false
     */
    private boolean jmeterIgnoreError;
    
    /**
     * Absolute path to File to log results to.
     *
     * @parameter
     */
    private String resultFileName;
    
    /**
     * @parameter expression="${project}"
     * @required
     */
    @SuppressWarnings("unused")
    private MavenProject mavenProject;
       
    /**
     * HTTP proxy host name.
     * @parameter
     */
    private String proxyHost;
    
    /**
     * HTTP proxy port.
     * @parameter expression="80"
     */
    private Integer proxyPort;
    
    /**
     * HTTP proxy username.
     * @parameter
     */
    private String proxyUsername;
    
    /**
     * HTTP proxy user password.
     * @parameter
     */
    private String proxyPassword;
    
    /**
     * Postfix to add to report file.
     *
     * @parameter default-value="-report.html"
     */
    private String reportPostfix;
    
    /**
     * Add date to the report file 
     * @parameter default-value=true
     */
    private Boolean reportDated;
    
    private File workDir;
    private File jmeterLog;
    private DateFormat fmt = new SimpleDateFormat("yyMMdd");
    
    /**
     * Run all JMeter tests.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        initSystemProps();

        List<String> jmeterTestFiles = new ArrayList<String>();
        List<String> results = new ArrayList<String>();
        if (jmeterTestFile != null) {
            jmeterTestFiles.add(jmeterTestFile.getName());
        } else {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(srcDir);
            scanner.setIncludes(includes == null ? new String[]{"**/*.jmx"} : includes.toArray(new String[]{}));
            if (excludes != null) {
                scanner.setExcludes(excludes.toArray(new String[]{}));
            }
            scanner.scan();
            jmeterTestFiles.addAll(Arrays.asList(scanner.getIncludedFiles()));
        }

        for (String file : jmeterTestFiles) {
            results.add(executeTest(new File(srcDir, file)));
        }
        if (this.enableReports) {
            makeReport(results);
        }
        checkForErrors(results);
    }

    private void makeReport(List<String> results) throws MojoExecutionException {
        try {
            ReportTransformer transformer;
            transformer = new ReportTransformer(getXslt());
            getLog().info("Building JMeter Report.");
            for (String resultFile : results) {
                final String outputFile = toOutputFileName(resultFile);
                getLog().info("transforming: " + resultFile + " to " + outputFile);
                transformer.transform(resultFile, outputFile);
            }
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Error writing report file jmeter file.", e);
        } catch (TransformerException e) {
            throw new MojoExecutionException("Error transforming jmeter results", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Error copying resources to jmeter results", e);
        }
    }

    /**
     * returns the fileName with the configured reportPostfix
     * 
     * @param fileName the String to modify
     *
     * @return modified fileName
     */
    private String toOutputFileName(String fileName) {
        if (fileName.endsWith(".xml")) {
            return fileName.replace(".xml", this.reportPostfix);
        } else {
            return fileName + this.reportPostfix;
        }
    }

    private InputStream getXslt() throws IOException {
        if (this.reportXslt == null) {
            //if we are using the default report, also copy the images out.
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/collapse.jpg"), new FileOutputStream(this.reportDir.getPath() + File.separator + "collapse.jpg"));
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/expand.jpg"), new FileOutputStream(this.reportDir.getPath() + File.separator + "expand.jpg"));
            return Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/jmeter-results-detail-report_21.xsl");
        } else {
            return new FileInputStream(this.reportXslt);
        }
    }

    /**
     * Scan JMeter result files for "error" and "failure" messages
     *
     * @param results List of JMeter result files.
     *
     * @throws MojoExecutionException exception
     * @throws MojoFailureException exception
     */
    private void checkForErrors(List<String> results) throws MojoExecutionException, MojoFailureException {
        ErrorScanner scanner = new ErrorScanner(this.jmeterIgnoreError, this.jmeterIgnoreFailure);
        try {
            for (String file : results) {
                if (scanner.scanForProblems(new File(file))) {
                    getLog().warn("There were test errors.  See the jmeter logs for details");
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read log file", e);
        }
    }

    /**
     * Initialize System Properties needed for JMeter run.
     *
     * @throws MojoExecutionException exception
     */
    private void initSystemProps() throws MojoExecutionException {
        workDir = new File("target" + File.separator + "jmeter");
        workDir.mkdirs();
        createTemporaryProperties();

        jmeterLog = new File(workDir, "jmeter.log");
        try {
            System.setProperty("log_file", jmeterLog.getCanonicalPath());
        } catch (IOException e) {
            throw new MojoExecutionException("Can't get canonical path for log file", e);
        }
    }

    /**
     * Create temporary property files and set necessary System Properties.
     *
     * This mess is necessary because JMeter must load this info from a file.
     * Loading resources from classpath won't work.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          Exception
     */
    private void createTemporaryProperties() throws MojoExecutionException {
        List<File> temporaryPropertyFiles = new ArrayList<File>();

        String jmeterTargetDir = File.separator + "target" + File.separator + "jmeter" + File.separator;
        File saveServiceProps = new File(workDir, "saveservice.properties");
        System.setProperty("saveservice_properties", jmeterTargetDir + saveServiceProps.getName());
        temporaryPropertyFiles.add(saveServiceProps);
        File upgradeProps = new File(workDir, "upgrade.properties");
        System.setProperty("upgrade_properties", jmeterTargetDir + upgradeProps.getName());
        temporaryPropertyFiles.add(upgradeProps);

        for (File propertyFile : temporaryPropertyFiles) {
            try {
                FileWriter out = new FileWriter(propertyFile);
                IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile.getName()), out);
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new MojoExecutionException("Could not create temporary property file " + propertyFile.getName() + " in directory " + jmeterTargetDir, e);
            }
        }
    }

    /**
     * Executes a single JMeter test by building up a list of command line
     * parameters to pass to JMeter.start().
     * 
     * @param test JMeter test XML
     *
     * @return the report file names.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          Exception
     */
    private String executeTest(File test) throws MojoExecutionException {

        try {
            getLog().info("Executing test: " + test.getCanonicalPath());

            if (resultFileName == null) {
            	resultFileName = reportDir.toString() + File.separator + test.getName().substring(0, test.getName().lastIndexOf("."));            	
            	if ( reportDated ) resultFileName += "-" + fmt.format(new Date());
            	resultFileName += ".xml";
            }
            //delete file if it already exists
            new File(resultFileName).delete();
            List<String> argsTmp = Arrays.asList("-n", "-t",
                    test.getCanonicalPath(),
                    "-l", resultFileName,
                    "-p", jmeterDefaultPropertiesFile.toString(),
                    "-d", System.getProperty("user.dir"));

            List<String> args = new ArrayList<String>();
            args.addAll(argsTmp);
            /* 
             * The user properties are passed across OK, but JMeter refuses to use them! Even after
             * I rewrote the method to stop passing the arguments as one long quoted string.
             * So, gonna have to do this the hard way and replace them in the jmx here. 
             */
            //args.addAll(getUserProperties());
            expandParameters(getUserProperties(),test);
            
            if (jmeterCustomPropertiesFile != null) {
                args.add("-q");
                args.add(jmeterCustomPropertiesFile.toString());
            }
            if (remote) {
                args.add("-r");
            }

            if (proxyHost != null && !proxyHost.equals("")) {
                args.add("-H");
                args.add(proxyHost);
                args.add("-P");
                args.add(proxyPort.toString());
                getLog().info("Setting HTTP proxy to " + proxyHost + ":" + proxyPort);
            }

            if (proxyUsername != null && !proxyUsername.equals("")) {
                args.add("-u");
                args.add(proxyUsername);
                args.add("-a");
                args.add(proxyPassword);
                getLog().info("Logging with " + proxyUsername + ":" + proxyPassword);
            }

            if (getLog().isDebugEnabled()) {
                getLog().debug("JMeter is called with the following command line arguments: " + args.toString());
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
                        return; // Ignore
                    }
                    getLog().error("Error in thread " + t.getName());
                }
            });
            try {
                // This mess is necessary because the only way to know when
                // JMeter
                // is done is to wait for its test end message!
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
                // Replace the test with expanded parameters with the original
                File origtest = new File(test.getCanonicalFile() + ".old");
                if (test.delete()) {
                	if ( !origtest.renameTo(test.getCanonicalFile()) ) {
                		throw new MojoExecutionException("Can't rename test file: " + test.getName() );
                	}
                }
            }

            return resultFileName;
        } catch (IOException e) {
            throw new MojoExecutionException("Can't execute test", e);
        }
    }

    /**
     * Replace the -J parameters that JMeter can use directly in the jmx file.
     * 
     * @param replacements - list of replacements to make
     * 
     * @param test - The full path and filename of the test to alter
     * 
     * @throws MojoExecutionException exception
     */
    private void expandParameters(ArrayList<String> replacements,File test) throws MojoExecutionException {
    	Map<String,String> m = new HashMap<String, String>();
    	String strline;
    	
    	for (String s : replacements) {
    		if( s.toLowerCase().startsWith("-j") == false ) {
    			if ( ! s.substring(s.indexOf("=")+1).equals("null") ) 
    				m.put(s.substring(0,s.indexOf("=")),s.substring(s.indexOf("=")+1));
    		}
    	}
    	
    	try {
        	BufferedReader input = new BufferedReader(new FileReader(test));
        	FileWriter output = new FileWriter(test.getCanonicalFile() + ".new");
    		while (null != ((strline = input.readLine())))
    		{
    			strline = tokenReplace(strline,m);
    			output.write(strline.concat("\n"));
    		}
    		
    		input.close();
    		output.close();
    		File oldtest = new File( test.getCanonicalFile() + ".old" );
    		if ( test.renameTo(oldtest.getCanonicalFile()) ) {
    			File newtest = new File( test.getCanonicalFile() + ".new" );
    			if ( !newtest.renameTo( test.getCanonicalFile() ) ) {
    				throw new MojoExecutionException("Can't rename test file: " + newtest.getName() );
    			}
    		} else {
    			throw new MojoExecutionException("Can't rename test file: " + test.getName() );
    		}
    	} catch (IOException e) {
    		throw new MojoExecutionException("Can't read test file: " + test.getName(), e);
    	}
    }
    
    /**
     * Replaces the parameter tokens with their values
     * @param template The string to replace tokens in
     * @param map The list of token keys and their associated replacement values
     * @return A string with all tokens replaced
     */
    private static String tokenReplace ( final String template, final Map<String,String> map ) { 
    	final StringBuilder list = new StringBuilder( "\\$\\{__P\\((" );
    	for( final String key: map.keySet() ) {
    		list.append( key ); list.append( "|" );
    	}
    	list.deleteCharAt(list.length()-1);
    	list.append( ").*?\\)\\}" );
    	Pattern pattern = Pattern.compile( list.toString() );
    	Matcher matcher = pattern.matcher( template );
    	final StringBuffer stringBuffer = new StringBuffer();
    	while ( matcher.find() ) {
    		final String string = matcher.group( 1 );
    		matcher.appendReplacement( stringBuffer, map.get( string ) );
    	}
    	matcher.appendTail( stringBuffer );
    	return tokenReplace( stringBuffer.toString() );
    }
    
    /**
     * Replaces the parameter tokens with their default values
     * @param template The string to replace tokens in
     * @return A string with tokens replaced with their default values
     */
    private static String tokenReplace ( final String template ) {
    	final String regex = "\\$\\{__P\\(.*?,(.*?)\\)\\}";
    	Pattern pattern = Pattern.compile( regex );
    	Matcher matcher = pattern.matcher( template );
    	final StringBuffer stringBuffer = new StringBuffer();
    	while ( matcher.find() ) {
    		final String string = matcher.group( 1 );
    		matcher.appendReplacement( stringBuffer, string );
    	}
    	matcher.appendTail( stringBuffer );
    	return stringBuffer.toString();
    }
    
    /**
     * Check JMeter logfile (provided as a BufferedReader) for End message.
     *
     * @param in JMeter logfile
     *
     * @return true if test ended
     *
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
     * Translates map of jmeterUserProperties to List of JMeter compatible commandline flags.
     *
     * @return List of JMeter compatible commandline flags
     */
    @SuppressWarnings("unchecked")
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
