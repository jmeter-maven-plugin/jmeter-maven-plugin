package com.lazerycode.jmeter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.ProxyConfiguration;
import com.lazerycode.jmeter.configuration.RemoteConfiguration;
import com.lazerycode.jmeter.configuration.ReportConfiguration;
import com.lazerycode.jmeter.properties.PropertyHandler;

/**
 * JMeter Maven plugin.
 * This is a base class for the JMeter mojos.
 *
 * @author Tim McCune
 */
public abstract class JMeterAbstractMojo extends AbstractMojo {

    /**
     * Sets the list of include patterns to use in directory scan for JMX files.
     * Relative to testFilesDirectory.
     *
     * @parameter
     */
    protected List<String> testFilesIncluded;

    /**
     * Sets the list of exclude patterns to use in directory scan for Test files.
     * Relative to testFilesDirectory.
     *
     * @parameter
     */
    protected List<String> testFilesExcluded;

    /**
     * Path under which JMX files are stored.
     *
     * @parameter expression="${jmeter.testfiles.basedir}"
     * default-value="${basedir}/src/test/jmeter"
     */
    protected File testFilesDirectory;

    /**
     * Timestamp the test results.
     *
     * @parameter default-value="true"
     */
    protected boolean testResultsTimestamp;

    /**
     * Set the SimpleDateFormat appended to the results filename.
     * (This assumes that testResultsTimestamp is set to 'true')
     *
     * @parameter default-value="yyMMdd"
     */
    protected String resultsFileNameDateFormat;

    /**
     * Absolute path to JMeter custom (test dependent) properties file.
     * .
     *
     * @parameter
     */
    protected Map<String, String> propertiesJMeter;

    /**
     * JMeter Properties that are merged with precedence into default JMeter file in saveservice.properties
     *
     * @parameter
     */
    protected Map<String, String> propertiesSaveService;

    /**
     * JMeter Properties that are merged with precedence into default JMeter file in upgrade.properties
     *
     * @parameter
     */
    protected Map<String, String> propertiesUpgrade;

    /**
     * JMeter Properties that are merged with precedence into default JMeter file in user.properties
     * user.properties takes precedence over jmeter.properties
     *
     * @parameter
     */
    protected Map<String, String> propertiesUser;

    /**
     * JMeter Global Properties that override those given in jmeterProps
     * This sets local and remote properties (JMeter's definition of global properties is actually remote properties)
     * This will override any local/remote properties already set
     *
     * @parameter
     */
    protected Map<String, String> propertiesGlobal;

    /**
     * (Java) System properties set for the test run.
     * Properties are merged with precedence into default JMeter file system.properties
     *
     * @parameter
     */
    protected Map<String, String> propertiesSystem;

    /**
     * Replace the default JMeter properties with any custom properties files supplied.
     * (If set to false any custom properties files will be merged with the default JMeter properties files, custom properties will overwrite default ones)
     *
     * @parameter default-value="true"
     */
    protected boolean propertiesReplacedByCustomFiles;

    /**
     * Absolute path to JMeter custom (test dependent) properties file.
     * .
     *
     * @parameter
     */
    protected File customPropertiesFile;

    /**
     * Sets whether ErrorScanner should ignore failures in JMeter result file.
     *
     * @parameter expression="${jmeter.ignore.failure}" default-value=false
     */
    protected boolean ignoreResultFailures;

    /**
     * Sets whether ErrorScanner should ignore errors in JMeter result file.
     *
     * @parameter expression="${jmeter.ignore.error}" default-value=false
     */
    protected boolean ignoreResultErrors;

    /**
     * Value class that wraps all proxy configurations.
     *
     * @parameter default-value="${proxyConfig}"
     */
    protected ProxyConfiguration proxyConfig;

    /**
     * Value class that wraps all remote configurations.
     *
     * @parameter default-value="${remoteConfig}"
     */
    protected RemoteConfiguration remoteConfig;

    /**
     * Value class that wraps all report configuration.
     *
     * @parameter default-value="${reportConfig}"
     */
    protected ReportConfiguration reportConfig;

    /**
     * Suppress JMeter output
     *
     * @parameter default-value="true"
     */
    protected boolean suppressJMeterOutput;

    /**
     * Path(s) to add to the classpath used by the plugin.
     *
     * @parameter
     */
    protected String addToClassPath;
    
    /**
     * Dependency artifacts to add to extensions directory.
     *
     * @parameter
     */
    private List<String> extensions;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject mavenProject;

    /**
     * Get a list of artifacts used by this plugin
     *
     * @parameter default-value="${plugin.artifacts}"
     * @required
     * @readonly
     */
    protected List<Artifact> pluginArtifacts;

    protected File workDir;
    protected File binDir;
    protected File libExt;
    protected File logsDir;
    protected String jmeterConfigArtifact = "ApacheJMeter_config";
    protected JMeterArgumentsArray testArgs;
    protected PropertyHandler pluginProperties;


    //==================================================================================================================

    /**
     * Generate the directory tree utilised by JMeter.
     */
    protected void generateJMeterDirectoryTree() {
        this.workDir = new File(this.mavenProject.getBasedir() + File.separator + "target" + File.separator + "jmeter");
        this.workDir.mkdirs();
        this.logsDir = new File(this.workDir + File.separator + "jmeter-logs");
        this.logsDir.mkdirs();
        this.binDir = new File(this.workDir + File.separator + "bin");
        this.binDir.mkdirs();
        this.libExt = new File(this.workDir + File.separator + "lib" + File.separator + "ext");
        this.libExt.mkdirs();
        if (!this.reportConfig.isOutputDirectorySet()) {
            this.reportConfig.createOutputDirectory(new File(workDir + File.separator + "report"));
        }
        //JMeter expects a <workdir>/lib/junit directory and complains if it can't find it.
        new File(this.workDir + File.separator + "lib" + File.separator + "junit").mkdirs();
        //JMeter uses the system property "user.dir" to set its base working directory
        System.setProperty("user.dir", this.binDir.getAbsolutePath());
    }

    protected void propertyConfiguration() throws MojoExecutionException {
        this.pluginProperties = new PropertyHandler(this.testFilesDirectory, this.binDir, getArtifactNamed(this.jmeterConfigArtifact), this.propertiesReplacedByCustomFiles);
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
    protected void setJMeterClasspath() throws MojoExecutionException {
        List<String> classPath = new ArrayList<String>();
        for (Artifact artifact : this.pluginArtifacts) {
            try {
                //This assumes that all JMeter components are named "ApacheJMeter_<component>" in their POM files
                if (artifact.getArtifactId().startsWith("ApacheJMeter_")) {
                    FileUtils.copyFile(artifact.getFile(), new File(this.libExt + File.separator + artifact.getFile().getName()));
                }
                else if ( extensions.contains ( artifact.getArtifactId ( ) ) ) {
                   FileUtils.copyFile(artifact.getFile(), new File(this.libExt + File.separator + artifact.getFile().getName()));                   
                }
                classPath.add(artifact.getFile().getCanonicalPath());
            } catch (IOException mx) {
                throw new MojoExecutionException("Unable to get the canonical path for " + artifact);
            }
        }
        //Add any additional classpath paths supplied by end user.
        if (!UtilityFunctions.isNotSet(this.addToClassPath)) classPath.add(this.addToClassPath);
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
    protected Artifact getArtifactNamed(String artifactName) throws MojoExecutionException {
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
    protected void initialiseJMeterArgumentsArray() throws MojoExecutionException {
        this.testArgs = new JMeterArgumentsArray(this.reportConfig.getOutputDirectoryAbsolutePath());
        this.testArgs.setResultsTimestamp(this.testResultsTimestamp);
        this.testArgs.setJMeterHome(this.workDir.getAbsolutePath());
        this.testArgs.setProxyConfig(this.proxyConfig);
        this.testArgs.setACustomPropertiesFile(this.customPropertiesFile);
        try {
            this.testArgs.setResultsFileNameDateFormat(new SimpleDateFormat(this.resultsFileNameDateFormat));
        } catch (Exception ex) {
            getLog().error("'" + this.resultsFileNameDateFormat + "' is an invalid date format.  Defaulting to 'yyMMdd'.");
        }
    }
}