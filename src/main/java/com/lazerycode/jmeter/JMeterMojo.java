package com.lazerycode.jmeter;

import com.lazerycode.jmeter.enums.JMeterPropertiesFiles;
import com.lazerycode.jmeter.reporting.ReportGenerator;
import com.lazerycode.jmeter.testExecution.TestManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.jar.JarFile;

/**
 * JMeter Maven plugin.
 *
 * @author Tim McCune
 * @goal jmeter
 * @requiresProject true
 */
public class JMeterMojo extends AbstractMojo {


    /**
     * Get a list of artifacts used by this plugin
     *
     * @parameter default-value="${plugin.artifacts}"
     */
    private List<Artifact> pluginArtifacts;

    /**
     * Sets the list of include patterns to use in directory scan for JMX files.
     * Relative to srcDir.
     *
     * @parameter
     */
    private List<String> jMeterTestFiles;

    /**
     * Sets the list of exclude patterns to use in directory scan for Test files.
     * Relative to srcDir.
     *
     * @parameter
     */
    private List<String> excludeJMeterTestFiles;

    /**
     * Path under which JMX files are stored.
     *
     * @parameter expression="${jmeter.testfiles.basedir}"
     * default-value="${basedir}/src/test/jmeter"
     */
    private File srcDir;

    /**
     * Directory in which the reports are stored.
     *
     * @parameter expression="${jmeter.reports.dir}"
     * default-value="${basedir}/target/jmeter-reports"
     */
    private File reportDir;

    /**
     * Postfix to add to report file.
     *
     * @parameter default-value="-report.html"
     */
    private String reportPostfix;

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
     * Absolute path to File to log results to.
     *
     * @parameter
     */
    private String resultsFileName;

    /**
     * Absolute path to JMeter custom (test dependent) properties file.
     * .
     *
     * @parameter
     */
    private File jmeterCustomPropertiesFile;

    /**
     * JMeter Properties that override those given in jmeter.properties
     *
     * @parameter
     */
    @SuppressWarnings("rawtypes")
    private Map<String, String> jmeterUserProperties;

    /**
     * JMeter Remote Properties that override those given in jmeterProps
     *
     * @parameter
     */
    @SuppressWarnings("rawtypes")
    private Map<String, String> jmeterRemoteProperties;

    /**
     * JMeter Global Properties file
     *
     * @parameter
     */
    private File jmeterRemotePropertiesFile;

    /**
     * JMeter Global Properties that override those given in jmeterProps
     * This sets local and remote properties (JMeter's definition of global properties is actually remote properties)
     * This will override any local/remote properties already set
     *
     * @parameter
     */
    @SuppressWarnings("rawtypes")
    private Map<String, String> jmeterGlobalProperties;

    /**
     * JMeter Global Properties file
     * This sets local and remote properties (JMeter's definition of global properties is actually remote properties)
     * This will override any local/remote properties already set
     *
     * @parameter
     */
    private File jmeterGlobalPropertiesFile;

    /**
     * System properties set by JMeter
     *
     * @parameter
     */
    @SuppressWarnings("rawtypes")
    private Map<String, String> systemProperties;

    /**
     * Override JMeter logging categories
     *
     * @parameter
     */
    @SuppressWarnings("rawtypes")
    private Map<String, String> overrideLogCategories;

    /**
     * Override JMeter root log level
     *
     * @parameter
     */
    private String overrideRootLogLevel;

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
     * @parameter expression="${project}"
     * @required
     */
    @SuppressWarnings("unused")
    private MavenProject mavenProject;

    /**
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     */
    private ArtifactResolver artifactResolver;

    /**
     * @parameter expression="${localRepository}"
     */
    private ArtifactRepository localRepository;

    /**
     * Regex of nonproxy hosts.
     *
     * @parameter
     */
    private String nonProxyHosts;

    /**
     * HTTP proxy host name.
     *
     * @parameter
     */
    private String proxyHost;

    /**
     * HTTP proxy port.
     *
     * @parameter expression="80"
     */
    private Integer proxyPort;

    /**
     * HTTP proxy username.
     *
     * @parameter
     */
    private String proxyUsername;

    /**
     * HTTP proxy user password.
     *
     * @parameter
     */
    private String proxyPassword;

    /**
     * Sets whether the test execution shall preserve the order of tests in jMeterTestFiles clauses.
     *
     * @parameter expression="${jmeter.preserve.includeOrder}" default-value=false
     */
    private boolean jmeterPreserveIncludeOrder;

    /**
     * Suppress JMeter output
     *
     * @parameter default-value="true"
     */
    private boolean suppressJMeterOutput;

    /**
     * Stop remote servers when the test finishes
     *
     * @parameter default-value="false"
     */
    private boolean remoteStop;

    /**
     * Start all remote servers as defined in jmeter.properties when the test starts
     *
     * @parameter default-value="false"
     */
    private boolean remoteStartAll;

    /**
     * Comma separated list of servers to start when starting tests
     *
     * @parameter
     */
    private String remoteStart;

    /**
     * Remote start and stop for every test, or once for the entire test suite of tests.
     * (Defaults to once for the entire suite of tests)
     *
     * @parameter default-value="true"
     */
    private boolean remoteStartAndStopOnce;

    private Log log = getLog();
    private File workDir;
    private File binDir;
    private File libExt;
    private File logsDir;
    private String jmeterConfigArtifact = "ApacheJMeter_config";
    private JMeterArgumentsArray testArgs;

    /**
     * Run all the JMeter tests.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info(" ");
        log.info("-------------------------------------------------------");
        log.info(" P E R F O R M A N C E    T E S T S");
        log.info("-------------------------------------------------------");
        log.info(" ");
        validateInput();
        generateJMeterDirectoryTree();
        configureJMeterPropertiesFiles();
        setJMeterClasspath();
        initialiseJMeterArgumentsArray();
        TestManager jMeterTestManager = new TestManager(this.testArgs, this.logsDir, this.srcDir, this.log, this.jmeterPreserveIncludeOrder, this.jMeterTestFiles, this.excludeJMeterTestFiles, this.suppressJMeterOutput);
        jMeterTestManager.setRemoteStartOptions(this.remoteStop, this.remoteStartAll, this.remoteStartAndStopOnce, this.remoteStart);
        log.info(" ");
        log.info(testArgs.getProxyDetails());
        List<String> testResults = jMeterTestManager.executeTests();
        new ReportGenerator(this.reportPostfix, this.reportXslt, this.reportDir, this.enableReports, this.log).makeReport(testResults);
        checkForErrors(testResults);
    }

    /**
     * Validate the data passed into this plugin by the POM file and fail early if there are any obvious problems.
     *
     * @throws MojoExecutionException
     */
    private void validateInput() throws MojoExecutionException {
        if (!UtilityFunctions.isNotSet(this.jmeterRemotePropertiesFile)) {
            if (!UtilityFunctions.isNotSet(this.jmeterRemoteProperties)) {
                throw new MojoExecutionException("You cannot specify a remote properties file and individual remote properties!");
            }
        }
        if (!UtilityFunctions.isNotSet(this.jmeterRemoteProperties)) {
            if (!UtilityFunctions.isNotSet(this.jmeterRemotePropertiesFile)) {
                throw new MojoExecutionException("You cannot specify a remote properties file and individual remote properties!");
            }
        }
        if (!UtilityFunctions.isNotSet(this.jmeterGlobalPropertiesFile)) {
            if (!UtilityFunctions.isNotSet(this.jmeterGlobalProperties)) {
                throw new MojoExecutionException("You cannot specify a global properties file and individual global properties!");
            }
        }
        if (!UtilityFunctions.isNotSet(this.jmeterGlobalProperties)) {
            if (!UtilityFunctions.isNotSet(this.jmeterGlobalPropertiesFile)) {
                throw new MojoExecutionException("You cannot specify a global properties file and individual global properties!");
            }
        }
        if (!UtilityFunctions.isNotSet(this.overrideLogCategories)) {
            if (!UtilityFunctions.isNotSet(this.overrideRootLogLevel)) {
                throw new MojoExecutionException("You cannot override both the root log level and individual log categories!");
            }
        }
        if (!UtilityFunctions.isNotSet(this.overrideRootLogLevel)) {
            if (!UtilityFunctions.isNotSet(this.overrideLogCategories)) {
                throw new MojoExecutionException("You cannot override both the root log level and individual log categories!");
            }
        }
    }

    /**
     * Generate the directory tree utilised by JMeter.
     */
    private void generateJMeterDirectoryTree() {
        this.workDir = new File(mavenProject.getBasedir() + File.separator + "target" + File.separator + "jmeter");
        this.workDir.mkdirs();
        this.logsDir = new File(this.workDir + File.separator + "jmeter-logs");
        this.logsDir.mkdirs();
        this.binDir = new File(this.workDir + File.separator + "bin");
        this.binDir.mkdirs();
        this.libExt = new File(this.workDir + File.separator + "lib" + File.separator + "ext");
        this.libExt.mkdirs();
        //JMeter expects a <workdir>/lib/junit directory and complains if it can't find it.
        new File(this.workDir + File.separator + "lib" + File.separator + "junit").mkdirs();
        //JMeter uses the system property "user.dir" to set its base working directory
        System.setProperty("user.dir", this.binDir.getAbsolutePath());
    }

    /**
     * Create/Copy the properties files used by JMeter into the JMeter directory tree.
     *
     * @throws MojoExecutionException
     */
    private void configureJMeterPropertiesFiles() throws MojoExecutionException {
        for (JMeterPropertiesFiles propertyFile : JMeterPropertiesFiles.values()) {
            if (!usedCustomPropertiesFile(propertyFile.getPropertiesFileName())) {
                if (propertyFile.createFileIfItDoesntExist()) {
                    log.warn("Custom " + propertyFile.getPropertiesFileName() + " not found, using the default version supplied with JMeter.");
                    try {
                        FileWriter out = new FileWriter(new File(this.binDir + File.separator + propertyFile.getPropertiesFileName()));
                        JarFile propertyJar = new JarFile(getArtifactNamed(this.jmeterConfigArtifact).getFile());
                        InputStream in = propertyJar.getInputStream(propertyJar.getEntry("bin/" + propertyFile.getPropertiesFileName()));
                        IOUtils.copy(in, out);
                        in.close();
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        throw new MojoExecutionException("Could not create temporary property file " + propertyFile.getPropertiesFileName() + " in directory " + this.workDir, e);
                    }
                } else {
                    log.warn("Custom " + propertyFile.getPropertiesFileName() + " not found.");
                }
            }
        }
    }

    /**
     * Copy a user created properties file to the JMeter bin directory
     * (Bin dir must have been initialised before this is called)
     *
     * @param filename
     * @return
     */
    private boolean usedCustomPropertiesFile(String filename) {
        File propFile = new File(this.srcDir + File.separator + filename);
        if (propFile.exists()) {
            File destinationFile = new File(this.binDir + File.separator + filename);
            try {
                FileUtils.copyFile(propFile, destinationFile);
                return true;
            } catch (IOException ex) {
                log.warn("Unable to copy " + filename + " to " + this.binDir);
            }
        }
        return false;
    }

    /**
     * Copy jars to JMeter ext dir for JMeter function search and set the classpath.
     *
     * @throws MojoExecutionException
     */
    @SuppressWarnings("unchecked")
    private void setJMeterClasspath() throws MojoExecutionException {
        List<String> classPath = new ArrayList<String>();
        for (Artifact artifact : this.pluginArtifacts) {
            try {
                //This assumes that all JMeter components are named "ApacheJMeter_<component>" in their POM files
                if (artifact.getArtifactId().startsWith("ApacheJMeter_")) {
                    FileUtils.copyFile(artifact.getFile(), new File(this.libExt + File.separator + artifact.getFile().getName()));
                }
                classPath.add(artifact.getFile().getCanonicalPath());
            } catch (IOException mx) {
                throw new MojoExecutionException("Unable to get the canonical path for " + artifact);
            }
        }
        //Set the JMeter classpath
        System.setProperty("java.class.path", StringUtils.join(classPath.iterator(), File.pathSeparator));
    }

    /**
     * Search the list of plugin artifacts for an artifact with a specific name
     *
     * @param artifactName
     * @return
     * @throws MojoExecutionException
     */
    private Artifact getArtifactNamed(String artifactName) throws MojoExecutionException {
        for (Artifact artifact : this.pluginArtifacts) {
            if (artifact.getArtifactId().equals(artifactName)) {
                return artifact;
            }
        }
        throw new MojoExecutionException("Unable to find artifact '" + artifactName + "'!");
    }

    /**
     * Generate the initial JMeter Arguments array that is used to create the command line that we pass to JMeter.
     *
     * @throws MojoExecutionException
     */
    private void initialiseJMeterArgumentsArray() throws MojoExecutionException {
        this.testArgs = new JMeterArgumentsArray(this.reportDir.getAbsolutePath());
        this.testArgs.setJMeterHome(this.workDir.getAbsolutePath());
        this.testArgs.setACustomPropertiesFile(this.jmeterCustomPropertiesFile);
        this.testArgs.setUserProperties(this.jmeterUserProperties);
        this.testArgs.setRemoteProperties(this.jmeterRemoteProperties);
        this.testArgs.setRemotePropertiesFile(this.jmeterRemotePropertiesFile);
        this.testArgs.setGlobalProperties(this.jmeterGlobalProperties);
        this.testArgs.setProxyHostDetails(this.proxyHost, this.proxyPort);
        this.testArgs.setProxyUsername(this.proxyUsername);
        this.testArgs.setProxyPassword(this.proxyPassword);
        this.testArgs.setNonProxyHosts(this.nonProxyHosts);
        this.testArgs.setSystemProperties(this.systemProperties);
        this.testArgs.setLogCategoriesOverrides(this.overrideLogCategories);
        this.testArgs.setLogRootOverride(this.overrideRootLogLevel);
        testArgs.setResultsFileName(resultsFileName);
    }

    /**
     * Scan JMeter result files for "error" and "failure" messages
     *
     * @param results List of JMeter result files.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    private void checkForErrors(List<String> results) throws MojoExecutionException, MojoFailureException {
        ErrorScanner scanner = new ErrorScanner(this.jmeterIgnoreError, this.jmeterIgnoreFailure);
        int totalErrorCount = 0;
        int totalFailureCount = 0;
        boolean failed = false;
        try {
            for (String file : results) {
                if (!scanner.hasTestPassed(new File(file))) {
                    totalErrorCount = +scanner.getErrorCount();
                    totalFailureCount = +scanner.getFailureCount();
                    failed = true;
                }
            }
            log.info(" ");
            log.info("Test Results:");
            log.info(" ");
            log.info("Tests Run: " + results.size() + ", Failures: " + totalFailureCount + ", Errors: " + totalErrorCount + "");
            log.info(" ");
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read log file", e);
        }
        if (failed) {
            if (totalErrorCount == 0) {
                throw new MojoFailureException("There were test failures.  See the jmeter logs for details.");
            } else if (totalFailureCount == 0) {
                throw new MojoFailureException("There were test errors.  See the jmeter logs for details.");
            } else {
                throw new MojoFailureException("There were test errors and failures.  See the jmeter logs for details.");
            }
        }
    }
}