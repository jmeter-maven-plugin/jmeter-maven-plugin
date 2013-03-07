package com.lazerycode.jmeter;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.ProxyConfiguration;
import com.lazerycode.jmeter.configuration.RemoteConfiguration;
import com.lazerycode.jmeter.properties.PropertyHandler;
import com.lazerycode.jmeter.threadhandling.JMeterPluginSecurityManager;
import com.lazerycode.jmeter.threadhandling.JMeterPluginUncaughtExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.joda.time.format.DateTimeFormat;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * JMeter Maven plugin.
 * This is a base class for the JMeter mojos.
 *
 * @author Tim McCune
 */
@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal", "JavaDoc"}) // Mojos get their fields set via reflection
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
	 * Append the results timestamp to the filename
	 * (It will be prepended by default if testResultsTimestamp is set to true)
	 *
	 * @parameter default-value="false"
	 */
	protected boolean appendResultsTimestamp;
	/**
	 * Set the format of the timestamp that is appended to the results filename.
	 * (This assumes that testResultsTimestamp is set to 'true')
	 * For formatting see http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html
	 *
	 * @parameter default-value=""
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
	 * @description JMeter Properties that are merged with precedence into default JMeter file in 'upgrade.properties'.
	 */
	protected Map<String, String> propertiesUpgrade;

	/**
	 * JMeter Properties that are merged with precedence into default JMeter file in user.properties
	 * user.properties takes precedence over jmeter.properties
	 *
	 * @parameter
	 * @description JMeter Properties that are merged with precedence into default JMeter file in 'user.properties'
	 * user.properties takes precedence over 'jmeter.properties'.
	 */
	protected Map<String, String> propertiesUser;

	/**
	 * JMeter Global Properties that override those given in jmeterProps. <br>
	 * This sets local and remote properties (JMeter's definition of global properties is actually remote properties)
	 * and overrides any local/remote properties already set
	 *
	 * @description JMeter Global Properties that override those given in jmeterProps. <br>
	 * This sets local and remote properties (JMeter's definition of global properties is actually remote properties)
	 * and overrides any local/remote properties already set.
	 */
	protected Map<String, String> propertiesGlobal;

	/**
	 * (Java) System properties set for the test run.
	 * Properties are merged with precedence into default JMeter file system.properties
	 *
	 * @parameter Java merged with precedence into default JMeter file system.properties.
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
	 *
	 * @parameter
	 */
	protected File customPropertiesFile;

	/**
	 * Sets whether FailureScanner should ignore failures in JMeter result file.
	 * <p/>
	 * Failures are for example failed requests
	 *
	 * @parameter expression="${jmeter.ignore.failure}" default-value=false
	 */
	protected boolean ignoreResultFailures;

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
	 * Suppress JMeter output
	 *
	 * @parameter default-value="true"
	 */
	protected boolean suppressJMeterOutput;

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

	/**
	 * Skip the JMeter tests
	 *
	 * @parameter default-value="false"
	 */
	protected boolean skipTests;

	//---------------------------------------------------

	/**
	 * JMeter outputs.
	 *
	 * @parameter expression="${project.build.directory}/jmeter"
	 * @description Place where the JMeter files will be generated.
	 */
	protected transient File workDir;

	/**
	 * Other directories will be created by this plugin and used by JMeter
	 */
	protected File binDir;
	protected File libDir;
	protected File libExtDir;
	protected File logsDir;
	protected File resultsDir;

	/**
	 * All property files are stored in this artifact, comes with JMeter library
	 */
	protected final String jmeterConfigArtifact = "ApacheJMeter_config";
	protected JMeterArgumentsArray testArgs;
	protected PropertyHandler pluginProperties;

	//==================================================================================================================

	/**
	 * Generate the directory tree utilised by JMeter.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	protected void generateJMeterDirectoryTree() {
		this.logsDir = new File(this.workDir, "logs");
		this.logsDir.mkdirs();
		this.binDir = new File(this.workDir, "bin");
		this.binDir.mkdirs();
		this.libDir = new File(this.workDir, "lib");
		this.resultsDir = new File(workDir, "results");
		this.resultsDir.mkdirs();
		this.libExtDir = new File(libDir, "ext");
		this.libExtDir.mkdirs();

		//JMeter expects a <workdir>/lib/junit directory and complains if it can't find it.
		new File(libDir, "junit").mkdirs();
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
		this.pluginProperties.setDefaultPluginProperties(this.binDir.getAbsolutePath());
	}

	/**
	 * Copy jars/files to correct place in the JMeter directory tree.
	 *
	 * @throws MojoExecutionException
	 */
	protected void populateJMeterDirectoryTree() throws MojoExecutionException {
		for (Artifact artifact : this.pluginArtifacts) {
			try {
				if (artifact.getArtifactId().startsWith("ApacheJMeter_")) {
					if (artifact.getArtifactId().startsWith("ApacheJMeter_config")) {
						JarFile configSettings = new JarFile(artifact.getFile());
						Enumeration<JarEntry> entries = configSettings.entries();
						while (entries.hasMoreElements()) {
							JarEntry jarFileEntry = entries.nextElement();
							// Only interested in files in the /bin directory that are not properties files
							if (jarFileEntry.getName().startsWith("bin") && !jarFileEntry.getName().endsWith(".properties")) {
								if (!jarFileEntry.isDirectory()) {
									InputStream is = configSettings.getInputStream(jarFileEntry); // get the input stream
									OutputStream os = new FileOutputStream(new File(this.workDir.getCanonicalPath() + File.separator + jarFileEntry.getName()));
									while (is.available() > 0) {
										os.write(is.read());
									}
									os.close();
									is.close();
								}
							}
						}
						configSettings.close();
					} else {
						FileUtils.copyFile(artifact.getFile(), new File(this.libExtDir + File.separator + artifact.getFile().getName()));
					}
				} else {
					/**
					 * TODO: exclude jars that maven put in #pluginArtifacts for maven run? (e.g. plexus jars, the plugin artifact itself)
					 * Need more info on above, how do we know which ones to exclude??
					 * Most of the files pulled down by maven are required in /lib to match standard JMeter install
					 */
					if (Artifact.SCOPE_RUNTIME.equals(artifact.getScope())) {
						FileUtils.copyFile(artifact.getFile(), new File(this.libExtDir + File.separator + artifact.getFile().getName()));
					} else {
						FileUtils.copyFile(artifact.getFile(), new File(this.libDir + File.separator + artifact.getFile().getName()));
					}
				}
			} catch (IOException e) {
				throw new MojoExecutionException("Unable to populate the JMeter directory tree: " + e);
			}
		}
		//TODO Check if we really need to do this
		//empty classpath, JMeter will automatically assemble and add all JARs in #libDir and #libExtDir and add them to the classpath. Otherwise all jars will be in the classpath twice.
		System.setProperty("java.class.path", "");
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
	protected void initialiseJMeterArgumentsArray(boolean disableGUI) throws MojoExecutionException {
		testArgs = new JMeterArgumentsArray(disableGUI, workDir.getAbsolutePath());
		testArgs.setResultsDirectory(resultsDir.getAbsolutePath());
		if (testResultsTimestamp) {
			testArgs.setResultsTimestamp(testResultsTimestamp);
			testArgs.appendTimestamp(appendResultsTimestamp);
			try {
				testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern(resultsFileNameDateFormat));
			} catch (Exception ex) {
				getLog().error("'" + resultsFileNameDateFormat + "' is an invalid DateTimeFormat.  Defaulting to Standard ISO_8601.");
			}
		}
		testArgs.setProxyConfig(proxyConfig);
		testArgs.setACustomPropertiesFile(customPropertiesFile);

	}

	/**
	 * Wait for one of the JMeterThreads in the list to stop.
	 */
	protected void waitForTestToFinish(List<String> threadNames) throws InterruptedException {
		Thread waitThread = null;
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread thread : threadSet) {
			for (String threadName : threadNames) {
				if (threadName.equals(thread.getName())) {
					waitThread = thread;
					break;
				}
			}
		}
		if (waitThread != null) {
			waitThread.setUncaughtExceptionHandler(new JMeterPluginUncaughtExceptionHandler());
			waitThread.join();
		}
	}

	/**
	 * Capture System.exit commands so that we can check to see if JMeter is trying to kill us without warning.
	 *
	 * @return old SecurityManager so that we can switch back to normal behaviour.
	 */
	protected SecurityManager overrideSecurityManager() {
		SecurityManager oldManager = System.getSecurityManager();
		System.setSecurityManager(new JMeterPluginSecurityManager());
		return oldManager;
	}

	/**
	 * Override System.exit(0) to ensure JMeter doesn't kill us without warning.
	 *
	 * @return old UncaughtExceptionHandler so that we can switch back to normal behaviour.
	 */
	protected Thread.UncaughtExceptionHandler overrideUncaughtExceptionHandler() {
		Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new JMeterPluginUncaughtExceptionHandler());
		return oldHandler;
	}
}
