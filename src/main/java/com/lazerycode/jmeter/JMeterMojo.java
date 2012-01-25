package com.lazerycode.jmeter;

import com.lazerycode.jmeter.reporting.ReportGenerator;
import com.lazerycode.jmeter.testExecution.TestManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jmeter.JMeter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.Permission;
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
     * Absolute path to JMeter custom (test dependent) properties file.
     * .
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

    private File workDir;
    private File binDir;
    private File libExt;
    private File libJunit;
    private File logsDir;
    private String jmeterConfigArtifact = "ApacheJMeter_config";
    private JMeterArgumentsArray testArgs;
    private static Utilities util = new Utilities();
    private Log log = getLog();

    /**
     * Run all JMeter tests.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("\n" +
                "\n-------------------------------------------------------" +
                "\n P E R F O R M A N C E    T E S T S" +
                "\n-------------------------------------------------------" +
                "\n");
        validateInput();
        generateTemporaryPropertiesAndSetClasspath();
        initialiseJMeterArgumentsArray();
        List<String> results;
        TestManager foo = new TestManager(this.testArgs, this.logsDir, this.srcDir, this.log);
        results = foo.executeTests(generateTestList());
        new ReportGenerator(this.reportPostfix, this.reportXslt, this.reportDir, this.enableReports, this.log).makeReport(results);
        checkForErrors(results);
    }

    /**
     * Scan JMeter result files for "error" and "failure" messages
     *
     * @param results List of JMeter result files.
     * @throws MojoExecutionException exception
     * @throws MojoFailureException   exception
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
            log.info("\n\nResults :\n\n");
            log.info("Tests Run: " + results.size() + ", Failures: " + totalFailureCount + ", Errors: " + totalErrorCount + "\n\n");
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

    private void validateInput() throws MojoExecutionException {
        if (!this.util.isNotSet(this.jmeterRemotePropertiesFile)) {
            if (!this.util.isNotSet(this.jmeterRemoteProperties)) {
                throw new MojoExecutionException("You cannot specify a remote properties file and individual remote properties!");
            }
        }
        if (!this.util.isNotSet(this.jmeterRemoteProperties)) {
            if (!this.util.isNotSet(this.jmeterRemotePropertiesFile)) {
                throw new MojoExecutionException("You cannot specify a remote properties file and individual remote properties!");
            }
        }
        if (!this.util.isNotSet(this.jmeterGlobalPropertiesFile)) {
            if (!this.util.isNotSet(this.jmeterGlobalProperties)) {
                throw new MojoExecutionException("You cannot specify a global properties file and individual global properties!");
            }
        }
        if (!this.util.isNotSet(this.jmeterGlobalProperties)) {
            if (!this.util.isNotSet(this.jmeterGlobalPropertiesFile)) {
                throw new MojoExecutionException("You cannot specify a global properties file and individual global properties!");
            }
        }
        if (!this.util.isNotSet(this.overrideLogCategories)) {
            if (!this.util.isNotSet(this.overrideRootLogLevel)) {
                throw new MojoExecutionException("You cannot override both the root log level and individual log categories!");
            }
        }
        if (!this.util.isNotSet(this.overrideRootLogLevel)) {
            if (!this.util.isNotSet(this.overrideLogCategories)) {
                throw new MojoExecutionException("You cannot override both the root log level and individual log categories!");
            }
        }
    }


    /**
     * Copy a properties file to the bin directory ready to be read in by JMeter
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
     * Create temporary property files, copy jars to ext dir and set necessary System Properties.
     * <p/>
     * This mess is necessary because JMeter must load this info from a file.
     * JMeter can load resources from classpath, but it checks the files in it's lib/ext dir for usable functions
     * <p/>
     * <p/>
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          Exception
     */
    @SuppressWarnings("unchecked")
    private void generateTemporaryPropertiesAndSetClasspath() throws MojoExecutionException {
        //Generate expected directory structure
        this.workDir = new File(mavenProject.getBasedir() + File.separator + "target" + File.separator + "jmeter");
        this.workDir.mkdirs();
        this.logsDir = new File(this.workDir + File.separator + "jmeter-logs");
        this.logsDir.mkdirs();
        this.binDir = new File(this.workDir + File.separator + "bin");
        this.binDir.mkdirs();
        this.libExt = new File(this.workDir + File.separator + "lib" + File.separator + "ext");
        this.libExt.mkdirs();
        this.libJunit = new File(this.workDir + File.separator + "lib" + File.separator + "junit");
        this.libJunit.mkdirs();
        System.setProperty("user.dir", this.binDir.getAbsolutePath());
        //Create/copy properties files and put them in the bin directory
        for (JMeterPropertiesFiles propertyFile : JMeterPropertiesFiles.values()) {
            if (!usedCustomPropertiesFile(propertyFile.getPropertiesFileName())) {
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
            }
        }
        //Copy JMeter components to lib/ext for JMeter function search
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

    private void initialiseJMeterArgumentsArray() throws MojoExecutionException {
        this.testArgs = new JMeterArgumentsArray(this.reportDir.getAbsolutePath());
        this.testArgs.setJMeterHome(this.workDir.getAbsolutePath());
        this.testArgs.setACustomPropertiesFile(this.jmeterCustomPropertiesFile);
        this.testArgs.setUserProperties(this.jmeterUserProperties);
        this.testArgs.setRemoteProperties(this.jmeterRemoteProperties);
        this.testArgs.setRemotePropertiesFile(this.jmeterRemotePropertiesFile);
        this.testArgs.setGlobalProperties(this.jmeterGlobalProperties);
        this.testArgs.setUseRemoteHost(this.remote);
        this.testArgs.setProxyHostDetails(this.proxyHost, this.proxyPort);
        this.testArgs.setProxyUsername(this.proxyUsername);
        this.testArgs.setProxyPassword(this.proxyPassword);
        this.testArgs.setSystemProperties(this.systemProperties);
        this.testArgs.setLogCategoriesOverrides(this.overrideLogCategories);
        this.testArgs.setLogRootOverride(this.overrideRootLogLevel);
    }

    private List<String> generateTestList() {
        List<String> jmeterTestFiles = new ArrayList<String>();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(this.srcDir);
        scanner.setIncludes(this.jMeterTestFiles == null ? new String[]{"**/*.jmx"} : this.jMeterTestFiles.toArray(new String[]{}));
        if (this.excludeJMeterTestFiles != null) {
            scanner.setExcludes(this.excludeJMeterTestFiles.toArray(new String[]{}));
        }
        scanner.scan();
        final List<String> includedFiles = Arrays.asList(scanner.getIncludedFiles());
        if (this.jmeterPreserveIncludeOrder) {
            Collections.sort(includedFiles, new IncludesComparator(this.jMeterTestFiles));
        }
        jmeterTestFiles.addAll(includedFiles);
        return jmeterTestFiles;
    }
}
