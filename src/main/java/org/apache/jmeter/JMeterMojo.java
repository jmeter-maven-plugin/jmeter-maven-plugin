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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
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
     * default-value="${basedir}/target/jmeter-report"
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
     * default-value="${basedir}/src/test/jmeter/jmeter.properties"
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
    private Map<String, String> jmeterUserProperties;

    /**
     * JMeter Global Properties that override those given in jmeterProps
     *
     * @parameter
     */
    @SuppressWarnings("rawtypes")
    private Map<String, String> jmeterGlobalProperties;

    /**
     * JMeter Global Properties file
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
     * Postfix to add to report file.
     *
     * @parameter default-value="-report.html"
     */
    private String reportPostfix;

    /**
     * Sets whether the test execution shall preserve the order of tests in jMeterTestFiles clauses.
     *
     * @parameter expression="${jmeter.preserve.includeOrder}" default-value=false
     */
    private boolean jmeterPreserveIncludeOrder;

    private File workDir;
    private File jmeterLog;
    private static final String JMETER_ARTIFACT_GROUPID = "org.apache.jmeter";
    private JMeterArgumentsArray testArgs;

    /**
     * Run all JMeter tests.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        validateInput();
        createWorkingFiles();
        createTemporaryProperties();
        resolveJmeterArtifact();
        initialiseJMeterArgumentsArray();
        List<String> results = new ArrayList<String>();
        for (String file : generateTestList()) {
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
                    totalErrorCount =+ scanner.getErrorCount();
                    totalFailureCount =+ scanner.getFailureCount();
                    failed = true;
                }
            }
            getLog().info("\n\nResults :\n\n");
            getLog().info("Tests Run: " + results.size() + ", Failures: " + totalFailureCount + ", Errors: " + totalErrorCount + "\n\n");
        } catch (IOException e) {
            throw new MojoExecutionException("Can't read log file", e);
        }
        if (failed) {
            if (totalErrorCount == 0) {
                throw new MojoExecutionException("There were test failures.  See the jmeter logs for details.");
            } else if (totalFailureCount == 0) {
                throw new MojoExecutionException("There were test errors.  See the jmeter logs for details.");
            } else {
                throw new MojoExecutionException("There were test errors and failures.  See the jmeter logs for details.");
            }
        }
    }

    private void validateInput() throws MojoExecutionException {
        if (this.jmeterGlobalPropertiesFile != null || !this.jmeterGlobalPropertiesFile.equals("")) {
            if (this.jmeterGlobalProperties != null || !this.jmeterGlobalProperties.equals("")) {
                throw new MojoExecutionException("You cannot specify a global properties file and individual global properties!");
            }
        }
        if (this.jmeterGlobalProperties != null || !this.jmeterGlobalProperties.equals("")) {
            if (this.jmeterGlobalPropertiesFile != null || !this.jmeterGlobalPropertiesFile.equals("")) {
                throw new MojoExecutionException("You cannot specify a global properties file and individual global properties!");
            }
        }
        if (this.overrideLogCategories != null || !this.overrideLogCategories.equals("")) {
            if (this.overrideRootLogLevel != null || !this.overrideRootLogLevel.equals("")) {
                throw new MojoExecutionException("You cannot override both the root log level and individual log categories!");
            }
        }
        if (this.overrideRootLogLevel != null || !this.overrideRootLogLevel.equals("")) {
            if (this.overrideLogCategories != null || !this.overrideRootLogLevel.equals("")) {
                throw new MojoExecutionException("You cannot override both the root log level and individual log categories!");
            }
        }
    }

    private void createWorkingFiles() throws MojoExecutionException {
        workDir = new File("target" + File.separator + "jmeter");
        workDir.mkdirs();
        jmeterLog = new File(workDir, "jmeter.log");
        try {
            System.setProperty("log_file", jmeterLog.getCanonicalPath());
        } catch (IOException e) {
            throw new MojoExecutionException("Can't get canonical path for log file", e);
        }
    }

    /**
     * Resolve JMeter artifact, set necessary System Property.
     * <p/>
     * This mess is necessary because JMeter must load this info from a file.
     * Loading resources from classpath won't work.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          exception
     */
    private void resolveJmeterArtifact() throws MojoExecutionException {
        try {
            String searchPath = "";
            for (Object oDep : mavenProject.getDependencyArtifacts()) {
                if (oDep instanceof Dependency) {
                    Dependency dep = (Dependency) oDep;
                    if (JMETER_ARTIFACT_GROUPID.equals(dep.getGroupId())) {
                        //VersionRange needed for Maven 2.x compatibility.
                        VersionRange versionRange = VersionRange.createFromVersionSpec(dep.getVersion());
                        Artifact jmeterArtifact = new DefaultArtifact(JMETER_ARTIFACT_GROUPID, dep.getArtifactId(), versionRange, "", "jar", "", new DefaultArtifactHandler());
                        List remoteArtifactRepositories = mavenProject.getRemoteArtifactRepositories();
                        artifactResolver.resolve(jmeterArtifact, remoteArtifactRepositories, localRepository);
                        searchPath += jmeterArtifact.getFile().getAbsolutePath() + ";";
                    }
                }
                if (oDep instanceof Artifact) {
                    Artifact jmeterArtifact = (Artifact) oDep;
                    if (JMETER_ARTIFACT_GROUPID.equals(jmeterArtifact.getGroupId())) {
                        List remoteArtifactRepositories = mavenProject.getRemoteArtifactRepositories();
                        artifactResolver.resolve(jmeterArtifact, remoteArtifactRepositories, localRepository);
                        searchPath += jmeterArtifact.getFile().getAbsolutePath() + ";";
                    }
                }
            }
            System.setProperty("search_paths", searchPath);
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Could not resolve JMeter artifact. ", e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Could not find JMeter artifact. ", e);
        } catch (InvalidVersionSpecificationException e) {
            throw new MojoExecutionException("Invalid version declaration. ", e);
        }
    }

    /**
     * Create temporary property files and set necessary System Properties.
     * <p/>
     * This mess is necessary because JMeter must load this info from a file.
     * Loading resources from classpath won't work.
     * <p/>
     * ARGH JMeter assumes all paths set in system properties are relative to jmeter_home.
     * Double ARGH It also assumes that upgrade.properties is relative to jmeter_home/bin.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     *          Exception
     */
    @SuppressWarnings("unchecked")
    private void createTemporaryProperties() throws MojoExecutionException {
        List<File> temporaryPropertyFiles = new ArrayList<File>();
        File saveServiceProps = new File(workDir, "saveservice.properties");
        System.setProperty("saveservice_properties", "/" + saveServiceProps.getName());
        temporaryPropertyFiles.add(saveServiceProps);
        //Seems JMeter
        File binDir = new File(workDir, "bin");
        binDir.mkdirs();
        File upgradeProps = new File(binDir, "upgrade.properties");
        System.setProperty("upgrade_properties", "/" + upgradeProps.getName());
        temporaryPropertyFiles.add(upgradeProps);

        for (File propertyFile : temporaryPropertyFiles) {
            try {
                FileWriter out = new FileWriter(propertyFile);
                IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile.getName()), out);
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new MojoExecutionException("Could not create temporary property file " + propertyFile.getName() + " in directory " + workDir, e);
            }
        }
    }

    private void initialiseJMeterArgumentsArray() {
        testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setJMeterHome(this.workDir.getAbsolutePath());
        testArgs.setJMeterDefaultPropertiesFile(this.jmeterDefaultPropertiesFile);
        testArgs.setACustomPropertiesFile(this.jmeterCustomPropertiesFile);
        testArgs.setUserProperties(this.jmeterUserProperties);
        testArgs.setGlobalProperties(this.jmeterGlobalProperties);
        testArgs.setGlobalPropertiesFile(this.jmeterGlobalPropertiesFile);
        testArgs.setUseRemoteHost(this.remote);
        testArgs.setProxyHostDetails(this.proxyHost, this.proxyPort);
        testArgs.setProxyUsername(this.proxyUsername);
        testArgs.setProxyPassword(this.proxyPassword);
        testArgs.setSystemProperties(this.systemProperties);
        testArgs.setLogCategoriesOverrides(this.overrideLogCategories);
        testArgs.setLogRootOverride(this.overrideRootLogLevel);
    }

    private List<String> generateTestList() {
        List<String> jmeterTestFiles = new ArrayList<String>();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(srcDir);
        scanner.setIncludes(this.jMeterTestFiles == null ? new String[]{"**/*.jmx"} : this.jMeterTestFiles.toArray(new String[]{}));
        if (excludeJMeterTestFiles != null) {
            scanner.setExcludes(excludeJMeterTestFiles.toArray(new String[]{}));
        }
        scanner.scan();
        final List<String> includedFiles = Arrays.asList(scanner.getIncludedFiles());
        if (jmeterPreserveIncludeOrder) {
            Collections.sort(includedFiles, new IncludesComparator(this.jMeterTestFiles));
        }
        jmeterTestFiles.addAll(includedFiles);

        return jmeterTestFiles;
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
    private String executeTest(File test) throws MojoExecutionException {

        try {
            getLog().info("Executing test: " + test.getCanonicalPath());
            testArgs.setTestFile(test);
            //Delete results file if it already exists
            new File(testArgs.getResultsFilename()).delete();
            getLog().info(testArgs.getProxyDetails());
            if (getLog().isDebugEnabled()) {
                String[] debugTestArgs = testArgs.buildArgumentsArray();
                String debugOutput = ("JMeter is called with the following command line arguments: ");
                for (int i = 0; i < debugTestArgs.length; i++) {
                    debugOutput += debugTestArgs[i] + " ";
                }
                getLog().debug(debugOutput);
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
                // JMeter is done is to wait for its test end message!
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
