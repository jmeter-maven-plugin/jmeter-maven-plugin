package com.lazerycode.jmeter;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.properties.PropertyHandler;
import com.lazerycode.jmeter.reporting.ReportGenerator;
import com.lazerycode.jmeter.testrunner.TestManager;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JMeter Maven plugin.
 *
 * @author Tim McCune
 * @goal jmeter
 * @requiresProject true
 */
public class JMeterMojo extends AbstractMojo {

    /**
     * Sets the list of include patterns to use in directory scan for JMX files.
     * Relative to testFilesDirectory.
     *
     * @parameter
     */
    private List<String> testFilesIncluded;

    /**
     * Sets the list of exclude patterns to use in directory scan for Test files.
     * Relative to testFilesDirectory.
     *
     * @parameter
     */
    private List<String> testFilesExcluded;

    /**
     * Path under which JMX files are stored.
     *
     * @parameter expression="${jmeter.testfiles.basedir}"
     * default-value="${basedir}/src/test/jmeter"
     */
    private File testFilesDirectory;

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
    private boolean reportEnable;

    /**
     * Custom Xslt which is used to create the report.
     *
     * @parameter
     */
    private File reportXslt;

    /**
     * Absolute path to JMeter custom (test dependent) properties file.
     * .
     *
     * @parameter
     */
    private Map<String, String> propertiesJMeter;

    /**
     * JMeter Properties that are merged with precedence into default JMeter file in saveservice.properties
     *
     * @parameter
     */
    private Map<String, String> propertiesSaveService;

    /**
     * JMeter Properties that are merged with precedence into default JMeter file in upgrade.properties
     *
     * @parameter
     */
    private Map<String, String> propertiesUpgrade;

    /**
     * JMeter Properties that are merged with precedence into default JMeter file in user.properties
     * user.properties takes precedence over jmeter.properties
     *
     * @parameter
     */
    private Map<String, String> propertiesUser;

    /**
     * JMeter Global Properties that override those given in jmeterProps
     * This sets local and remote properties (JMeter's definition of global properties is actually remote properties)
     * This will override any local/remote properties already set
     *
     * @parameter
     */
    private Map<String, String> propertiesGlobal;

    /**
     * (Java) System properties set for the test run.
     * Properties are merged with precedence into default JMeter file system.properties
     *
     * @parameter
     */
    private Map<String, String> propertiesSystem;

    /**
     * Sets whether ErrorScanner should ignore failures in JMeter result file.
     *
     * @parameter expression="${jmeter.ignore.failure}" default-value=false
     */
    private boolean ignoreResultFailures;

    /**
     * Sets whether ErrorScanner should ignore errors in JMeter result file.
     *
     * @parameter expression="${jmeter.ignore.error}" default-value=false
     */
    private boolean ignoreResultErrors;

    /**
     * Regex of hosts that will not be proxied
     *
     * @parameter
     */
    private String proxyHostExclusions;

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
     * Suppress JMeter output
     *
     * @parameter default-value="true"
     */
    private boolean suppressJMeterOutput;

    /**
     * Use remote JMeter installation to run tests
     *
     * @parameter default-value=false
     */
    private boolean remote;

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

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * Get a list of artifacts used by this plugin
     *
     * @parameter default-value="${plugin.artifacts}"
     * @readonly
     */
    private List<Artifact> pluginArtifacts;

    private File workDir;
    private File binDir;
    private File libExt;
    private File logsDir;
    private String jmeterConfigArtifact = "ApacheJMeter_config";
    private JMeterArgumentsArray testArgs;
    private PropertyHandler pluginProperties;

    /**
     * Run all the JMeter tests.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" P E R F O R M A N C E    T E S T S");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
        generateJMeterDirectoryTree();
        propertyConfiguration();
        setJMeterClasspath();
        initialiseJMeterArgumentsArray();
        TestManager jMeterTestManager = new TestManager(this.testArgs, this.logsDir, this.testFilesDirectory, this.getLog(), this.testFilesIncluded, this.testFilesExcluded, this.suppressJMeterOutput);
        jMeterTestManager.setRemoteStartOptions(this.remoteStop, this.remoteStartAll, this.remoteStartAndStopOnce, this.remoteStart);
        getLog().info(" ");
        getLog().info(testArgs.getProxyDetails());
        List<String> testResults = jMeterTestManager.executeTests();
        new ReportGenerator(this.reportPostfix, this.reportXslt, this.reportDir, this.reportEnable).makeReport(testResults);
        checkForErrors(testResults);
    }

    //==================================================================================================================

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
        reportDir = new File(workDir + File.separator + "report");
        reportDir.mkdirs();
        //JMeter expects a <workdir>/lib/junit directory and complains if it can't find it.
        new File(this.workDir + File.separator + "lib" + File.separator + "junit").mkdirs();
        //JMeter uses the system property "user.dir" to set its base working directory
        System.setProperty("user.dir", this.binDir.getAbsolutePath());
    }

    private void propertyConfiguration() throws MojoExecutionException {
        this.pluginProperties = new PropertyHandler(this.testFilesDirectory, this.binDir, getArtifactNamed(this.jmeterConfigArtifact), getLog());
        this.pluginProperties.setJMeterProperties(this.propertiesJMeter);
        this.pluginProperties.setJMeterGlobalProperties(this.propertiesGlobal);
        this.pluginProperties.setJMeterSaveServiceProperties(this.propertiesSaveService);
        this.pluginProperties.setJMeterUpgradeProperties(this.propertiesUpgrade);
        this.pluginProperties.setJmeterUserProperties(this.propertiesUser);
        this.pluginProperties.setJMeterSystemProperties(this.propertiesSystem);
        this.pluginProperties.configureJMeterPropertiesFiles();
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
        this.testArgs.setProxyHostDetails(this.proxyHost, this.proxyPort);
        this.testArgs.setProxyUsername(this.proxyUsername);
        this.testArgs.setProxyPassword(this.proxyPassword);
        this.testArgs.setNonProxyHosts(this.proxyHostExclusions);
    }

    /**
     * Scan JMeter result files for "error" and "failure" messages
     *
     * @param results List of JMeter result files.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    private void checkForErrors(List<String> results) throws MojoExecutionException, MojoFailureException {
        ErrorScanner scanner = new ErrorScanner(this.ignoreResultErrors, this.ignoreResultFailures);
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
            getLog().info(" ");
            getLog().info("Test Results:");
            getLog().info(" ");
            getLog().info("Tests Run: " + results.size() + ", Failures: " + totalFailureCount + ", Errors: " + totalErrorCount + "");
            getLog().info(" ");
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