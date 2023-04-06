package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;
import com.lazerycode.jmeter.configuration.ProxyConfiguration;
import com.lazerycode.jmeter.configuration.RemoteConfiguration;
import com.lazerycode.jmeter.json.TestConfigurationWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JMeter Maven plugin.
 * This is a base class for the JMeter mojos.
 */
@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal", "JavaDoc"}) // Mojos get their fields set via reflection
public abstract class AbstractJMeterMojo extends AbstractMojo {
    public static final String DEFAULT_CONFIG_EXECUTION_ID = "configuration";
    protected static final String LINE_SEPARATOR = "-------------------------------------------------------";
    protected static final String JMETER_ARTIFACT_PREFIX = "ApacheJMeter_";
    protected static final String JMETER_ARTIFACT_NAME = "ApacheJMeter";
    protected static final String JMETER_CONFIG_ARTIFACT_NAME = "ApacheJMeter_config";
    protected static final String JORPHAN_ARTIFACT_NAME = "jorphan";

    /**
     * Sets the list of include patterns to use in directory scan for JMX files.
     * Relative to testFilesDirectory.
     */
    @Parameter
    protected List<String> testFilesIncluded = new ArrayList<>();

    /**
     * Sets the list of exclude patterns to use in directory scan for JMX files.
     * Relative to testFilesDirectory.
     */
    @Parameter
    protected List<String> testFilesExcluded = new ArrayList<>();

    /**
     * Path under which .conf files are stored.
     */
    @Parameter(defaultValue = "${basedir}/src/test/conf")
    protected File confFilesDirectory;

    /**
     * Path under which JMX files are stored.
     */
    @Parameter(defaultValue = "${basedir}/src/test/jmeter")
    protected File testFilesDirectory;

    /**
     * Timestamp the test results.
     */
    @Parameter(defaultValue = "true")
    protected boolean testResultsTimestamp;

    /**
     * Append the results timestamp to the filename
     * (It will be prepended by default if testResultsTimestamp is set to true)
     */
    @Parameter(defaultValue = "false")
    protected boolean appendResultsTimestamp;

    /**
     * Set the format of the timestamp that is appended to the results filename.
     * (This assumes that testResultsTimestamp is set to 'true')
     * For formatting see http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html
     */
    @Parameter()
    protected String resultsFileNameDateFormat;

    /**
     * Generate JMeter Reports (this will force your .jtl's into .csv mode)
     */
    @Parameter(defaultValue = "false")
    protected boolean generateReports;

    /**
     * Do not fail the build if the JMeter JVM is force killed.
     * The JVM is normally killed forcibly because you ran out of memory, or the process running inside it stopped responding.
     * Make sure you investigate the above before reaching for this option!
     */
    @Parameter(defaultValue = "false")
    protected boolean doNotFailBuildIfJVMIsKilled;

    /**
     * TODO Dynamic
     * Set the directory that JMeter results are saved to.
     */
    @Parameter(defaultValue = "${project.build.directory}/jmeter/results")
    protected File resultsDirectory;

    /**
     * TODO Dynamic
     * Set the directory that JMeter reports are saved to.
     */
    @Parameter(defaultValue = "${project.build.directory}/jmeter/reports")
    protected File reportDirectory;

    /**
     * TODO Dynamic
     * Set the directory that JMeter logs are saved to.
     */
    @Parameter(defaultValue = "${project.build.directory}/jmeter/logs")
    protected File logsDirectory;

    /**
     * TODO Dynamic
     * Set the directory that JMeter test files are copied into as part of the build.
     */
    @Parameter(defaultValue = "${project.build.directory}/jmeter/testFiles")
    protected File testFilesBuildDirectory;

    /**
     * Absolute path to JMeter custom (test dependent) properties file.
     */
    @Parameter
    protected List<File> customPropertiesFiles = new ArrayList<>();

    /**
     * Use maven proxy configuration if no specific proxy configuration provided
     */
    @Parameter
    protected boolean useMavenProxy;

    /**
     * Maven settings
     */
    @Parameter(defaultValue = "${settings}", readonly = true)
    protected Settings settings;

    /**
     * Value class that wraps all proxy configurations.
     */
    @Parameter
    protected ProxyConfiguration proxyConfig;

    /**
     * Value class that wraps all remote configurations.
     */
    @Parameter(defaultValue = "${remoteConfig}")
    protected RemoteConfiguration remoteConfig;

    /**
     * Value class that wraps all JMeter Process JVM settings.
     */
    @Parameter(defaultValue = "${jMeterProcessJVMSettings}")
    protected JMeterProcessJVMSettings jMeterProcessJVMSettings;

    /**
     * Set a root log level to override all log levels used by JMeter
     * Valid log levels are: ERROR, WARN, INFO, DEBUG (They are not case sensitive);
     * If you try to set an invalid log level it will be ignored
     */
    @Parameter
    protected String overrideRootLogLevel;

    /**
     * Suppress JMeter output
     */
    @Parameter(defaultValue = "false")
    protected boolean suppressJMeterOutput;

    /**
     * The information extracted from the Mojo being currently executed
     */
    @Parameter(defaultValue = "${mojoExecution}", required = true, readonly = true)
    protected MojoExecution mojoExecution;

    @Parameter(defaultValue = "${session}", readonly = true)
    protected MavenSession session;

    /**
     * Skip the JMeter tests
     */
    @Parameter(defaultValue = "${skipTests}")
    protected boolean skipTests;

    /**
     * Set a pause in seconds after each test that is run.
     */
    @Parameter(defaultValue = "0")
    protected String postTestPauseInSeconds;

    /**
     * The filename used to store the results config
     */
    @Parameter(defaultValue = "${project.build.directory}/config.json")
    protected String testConfigFile;

    /**
     * The filename used to store the results config
     */
    @Parameter(property = "selectConfiguration" ,defaultValue = DEFAULT_CONFIG_EXECUTION_ID)
    protected String selectedConfiguration;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * The project build directory
     */
    @Parameter(defaultValue = "${project.build.directory}")
    protected File projectBuildDirectory;
    protected TestConfigurationWrapper testConfig;

    //==================================================================================================================

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTests) {
            if (session.getGoals().contains("jmeter:gui") || session.getGoals().contains("jmeter:remote-server")) {
                if (!"default-cli".equals(mojoExecution.getExecutionId()) && !"compile".equals(mojoExecution.getLifecyclePhase())) {
                    getLog().info("Performance tests are skipped.");

                    return;
                }
            } else {
                getLog().info("Performance tests are skipped.");

                return;
            }
        }

        if (useMavenProxy && proxyConfig == null) {
            loadMavenProxy();
        }

        doExecute();
    }


    protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;

    /**
     * Generate the initial JMeter Arguments array that is used to create the command line that we pass to JMeter.
     *
     * @param disableGUI Prevent JMeter gGUI from starting up
     * @param isCSVFormat Determines if results output is in CSV formate of legacy JTL format
     * @param jmeterDirectoryPath Path to the JMeter directory
     * @return
     * @throws MojoExecutionException If unable to generate arguments array
     */
    protected JMeterArgumentsArray computeJMeterArgumentsArray(boolean disableGUI, boolean isCSVFormat, String jmeterDirectoryPath) throws MojoExecutionException {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, jmeterDirectoryPath)
                .setResultsDirectory(resultsDirectory.getAbsolutePath())
                .setResultFileOutputFormatIsCSV(isCSVFormat)
                .setProxyConfig(proxyConfig)
                .setLogRootOverride(overrideRootLogLevel)
                .setLogsDirectory(logsDirectory.getAbsolutePath())
                .addACustomPropertiesFiles(customPropertiesFiles);
        if (generateReports && disableGUI) {
            testArgs.setReportsDirectory(reportDirectory.getAbsolutePath());
        }
        if (testResultsTimestamp) {
            testArgs.setResultsTimestamp(true)
                    .appendTimestamp(appendResultsTimestamp)
                    .setResultsFileNameDateFormat(resultsFileNameDateFormat);
        }

        return testArgs;
    }

    /**
     * Try to load the active maven proxy.
     */
    protected void loadMavenProxy() {
        if (null == settings) {
            return;
        }

        Proxy mvnProxy = settings.getActiveProxy();
        if (mvnProxy != null) {
            ProxyConfiguration newProxyConfiguration = new ProxyConfiguration();
            newProxyConfiguration.setHost(mvnProxy.getHost());
            newProxyConfiguration.setPort(mvnProxy.getPort());
            newProxyConfiguration.setUsername(mvnProxy.getUsername());
            newProxyConfiguration.setPassword(mvnProxy.getPassword());
            newProxyConfiguration.setHostExclusions(mvnProxy.getNonProxyHosts());
            proxyConfig = newProxyConfiguration;
            getLog().info("Maven proxy loaded successfully");
        } else {
            getLog().warn("No maven proxy found, however useMavenProxy is set to true!");
        }
    }

    static void copyFilesInTestDirectory(File sourceDirectory, File destinationDirectory) throws MojoExecutionException {
        try {
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
