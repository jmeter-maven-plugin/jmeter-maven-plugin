package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;
import com.lazerycode.jmeter.configuration.ProxyConfiguration;
import com.lazerycode.jmeter.configuration.RemoteConfiguration;
import com.lazerycode.jmeter.json.TestConfigurationWrapper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface Executable {

    public static final String DEFAULT_CONFIG_EXECUTION_ID = "configuration";
    public static final String LINE_SEPARATOR = "-------------------------------------------------------";
    public static final String JMETER_ARTIFACT_PREFIX = "ApacheJMeter_";
    public static final String JMETER_ARTIFACT_NAME = "ApacheJMeter";
    public static final String JMETER_CONFIG_ARTIFACT_NAME = "ApacheJMeter_config";
    public static final String JORPHAN_ARTIFACT_NAME = "jorphan";

    /**
     * Sets the list of include patterns to use in directory scan for JMX files.
     * Relative to testFilesDirectory.
     */
    @Parameter
    public List<String> testFilesIncluded = new ArrayList<>();

    /**
     * Sets the list of exclude patterns to use in directory scan for JMX files.
     * Relative to testFilesDirectory.
     */
    @Parameter
    public List<String> testFilesExcluded = new ArrayList<>();

    /**
     * Path under which .conf files are stored.
     */
    @Parameter(defaultValue = "${basedir}/src/test/conf")
    public File confFilesDirectory = null;

    /**
     * Path under which JMX files are stored.
     */
    @Parameter(defaultValue = "${basedir}/src/test/jmeter")
    public File testFilesDirectory = null;

    /**
     * Timestamp the test results.
     */
    @Parameter(defaultValue = "true")
    public boolean testResultsTimestamp = false;

    /**
     * Append the results timestamp to the filename
     * (It will be prepended by default if testResultsTimestamp is set to true)
     */
    @Parameter(defaultValue = "false")
    public boolean appendResultsTimestamp = false;

    /**
     * Set the format of the timestamp that is appended to the results filename.
     * (This assumes that testResultsTimestamp is set to 'true')
     * For formatting see http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html
     */
    @Parameter()
    public String resultsFileNameDateFormat = null;

    /**
     * Generate JMeter Reports (this will force your .jtl's into .csv mode)
     */
    @Parameter(defaultValue = "false")
    public boolean generateReports = false;

    /**
     * Do not fail the build if the JMeter JVM is force killed.
     * The JVM is normally killed forcibly because you ran out of memory, or the process running inside it stopped responding.
     * Make sure you investigate the above before reaching for this option!
     */
    @Parameter(defaultValue = "false")
    public boolean doNotFailBuildIfJVMIsKilled = false;

    /**
     * TODO Dynamic
     * Set the directory that JMeter results are saved to.
     */
    @Parameter(defaultValue = "${project.build.directory}/jmeter/results")
    public File resultsDirectory = null;

    /**
     * TODO Dynamic
     * Set the directory that JMeter reports are saved to.
     */
    @Parameter(defaultValue = "${project.build.directory}/jmeter/reports")
    public File reportDirectory = null;

    /**
     * TODO Dynamic
     * Set the directory that JMeter logs are saved to.
     */
    @Parameter(defaultValue = "${project.build.directory}/jmeter/logs")
    public File logsDirectory = null;

    /**
     * TODO Dynamic
     * Set the directory that JMeter test files are copied into as part of the build.
     */
    @Parameter(defaultValue = "${project.build.directory}/jmeter/testFiles")
    public File testFilesBuildDirectory = null;

    /**
     * Absolute path to JMeter custom (test dependent) properties file.
     */
    @Parameter
    public List<File> customPropertiesFiles = new ArrayList<>();

    /**
     * Use maven proxy configuration if no specific proxy configuration provided
     */
    @Parameter
    public boolean useMavenProxy = false;

    /**
     * Maven settings
     */
    @Parameter(defaultValue = "${settings}", readonly = true)
    public Settings settings = null;

    /**
     * Value class that wraps all proxy configurations.
     */
    @Parameter
    public ProxyConfiguration proxyConfig = null;

    /**
     * Value class that wraps all remote configurations.
     */
    @Parameter(defaultValue = "${remoteConfig}")
    public RemoteConfiguration remoteConfig = null;

    /**
     * Value class that wraps all JMeter Process JVM settings.
     */
    @Parameter(defaultValue = "${jMeterProcessJVMSettings}")
    public JMeterProcessJVMSettings jMeterProcessJVMSettings = null;

    /**
     * Set a root log level to override all log levels used by JMeter
     * Valid log levels are: ERROR, WARN, INFO, DEBUG (They are not case sensitive);
     * If you try to set an invalid log level it will be ignored
     */
    @Parameter
    public String overrideRootLogLevel = null;

    /**
     * Suppress JMeter output
     */
    @Parameter(defaultValue = "false")
    public boolean suppressJMeterOutput = false;

    /**
     * The information extracted from the Mojo being currently executed
     */
    @Parameter(defaultValue = "${mojoExecution}", required = true, readonly = true)
    public MojoExecution mojoExecution = null;

    @Parameter(defaultValue = "${session}", readonly = true)
    public MavenSession session = null;

    /**
     * Skip the JMeter tests
     */
    @Parameter(defaultValue = "${skipTests}")
    public boolean skipTests = false;

    /**
     * Set a pause in seconds after each test that is run.
     */
    @Parameter(defaultValue = "0")
    public String postTestPauseInSeconds = null;

    /**
     * The filename used to store the results config
     */
    @Parameter(defaultValue = "${project.build.directory}/config.json")
    public String testConfigFile = null;

    /**
     * The filename used to store the results config
     */
    @Parameter(property = "selectConfiguration" ,defaultValue = DEFAULT_CONFIG_EXECUTION_ID)
    public String selectedConfiguration = null;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * The project build directory
     */
    @Parameter(defaultValue = "${project.build.directory}")
    public File projectBuildDirectory = null;
    public TestConfigurationWrapper testConfig = null;


    public void doExecute() throws MojoExecutionException, MojoFailureException;
}
