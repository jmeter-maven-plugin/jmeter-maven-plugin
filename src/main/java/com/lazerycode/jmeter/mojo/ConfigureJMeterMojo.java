package com.lazerycode.jmeter.mojo;

import static com.lazerycode.jmeter.properties.ConfigurationFiles.GLOBAL_PROPERTIES;
import static com.lazerycode.jmeter.properties.ConfigurationFiles.JMETER_PROPERTIES;
import static com.lazerycode.jmeter.properties.ConfigurationFiles.REPORT_GENERATOR_PROPERTIES;
import static com.lazerycode.jmeter.properties.ConfigurationFiles.SAVE_SERVICE_PROPERTIES;
import static com.lazerycode.jmeter.properties.ConfigurationFiles.SYSTEM_PROPERTIES;
import static com.lazerycode.jmeter.properties.ConfigurationFiles.UPGRADE_PROPERTIES;
import static com.lazerycode.jmeter.properties.ConfigurationFiles.USER_PROPERTIES;
import static com.lazerycode.jmeter.properties.ConfigurationFiles.values;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.AbstractArtifact;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;

import com.lazerycode.jmeter.exceptions.DependencyResolutionException;
import com.lazerycode.jmeter.exceptions.IOException;
import com.lazerycode.jmeter.json.TestConfig;
import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesFile;
import com.lazerycode.jmeter.properties.PropertiesMapping;
/**
 * Goal that configures Apache JMeter bundle.<br/>
 * This goal is also called by other goals.<br/>
 * This goal runs within Lifecycle phase {@link LifecyclePhase#COMPILE}.
 */
@Mojo(name = "configure", defaultPhase = LifecyclePhase.COMPILE)
public class ConfigureJMeterMojo extends AbstractJMeterMojo {
    private static final String DEPENDENCIES_DEFAULT_SEARCH_SCOPE = JavaScopes.RUNTIME;
	@Component
	private RepositorySystem repositorySystem;

	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
	private RepositorySystemSession repositorySystemSession;

	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
	private List<RemoteRepository> repositoryList;

	private static final String ARTIFACT_STAR = "*";
	/**
	 * Name of the base config json file
	 */
	private static final String BASE_CONFIG_FILE = "/config.json";

	/**
	 * The version of JMeter that this plugin will use to run tests.
	 * We use a hard coded list of artifacts to configure JMeter locally,
	 * if you change this version number the list of artifacts required to run JMeter may change.
	 * If this happens you will need to override the &lt;jmeterArtifacts&gt; element.
	 */
	@Parameter(defaultValue = "5.1")
	private String jmeterVersion;

	/**
	 * A list of artifacts that we use to configure JMeter.
	 * This list is hard coded by default, you can override this list and supply your own list of artifacts for JMeter.
	 * This would be useful if you want to use a different version of JMeter that has a different list of required artifacts.
	 * <p/>
	 * &lt;jmeterArtifacts&gt;
	 * &nbsp;&nbsp;&lt;artifact&gt;kg.apc:jmeter-plugins:1.3.1&lt;/artifact&gt;
	 * &lt;jmeterArtifacts&gt;
	 */
	@Parameter
	private List<String> jmeterArtifacts = new ArrayList<>();

    /**
     * A list of artifacts to exclude.
     * You can supply your own list of artifacts
     * This is useful if you want to exclude broken or invalid dependencies
     * <p/>
     * &lt;excludedArtifacts&gt;
     * &lt;exclusion&gt;commons-pool2:commons-pool2&lt;/exclusion&gt;
     * &lt;exclusion&gt;commons-math3:commons-math3&lt;/exclusion&gt;
     * &lt;excludedArtifacts&gt;
     */
    @Parameter
    private List<String> excludedArtifacts = new ArrayList<>();

	/**
	 * A list of artifacts that the plugin should ignore.
	 * This would be useful if you don't want specific dependencies brought down by JMeter (or any used defined artifacts) copied into the JMeter directory structure.
	 * <p/>
	 * &lt;ignoredArtifacts&gt;
	 * &nbsp;&nbsp;&lt;artifact&gt;org.bouncycastle:bcprov-jdk15on:1.49&lt;/artifact&gt;
	 * &lt;ignoredArtifacts&gt;
	 */
	@Parameter
	private List<String> ignoredArtifacts = new ArrayList<>();

	/**
	 * Download all dependencies of files you want to add to lib/ext and copy them to lib/ext too
	 * <p/>
	 * &lt;downloadExtensionDependencies&gt;
	 * &nbsp;&nbsp;&lt;true&gt;
	 * &lt;downloadExtensionDependencies&gt;
	 */
	@Parameter(defaultValue = "true")
	protected boolean downloadExtensionDependencies;

	/**
	 * A list of artifacts that should be copied into the lib/ext directory e.g.
	 * <p/>
	 * &lt;jmeterExtensions&gt;
	 * &nbsp;&nbsp;&lt;artifact&gt;kg.apc:jmeter-plugins:1.3.1&lt;/artifact&gt;
	 * &lt;jmeterExtensions&gt;
	 */
	@Parameter
	protected List<String> jmeterExtensions = new ArrayList<>();

	/**
	 * Download all transitive dependencies of the JMeter artifacts.
	 * <p/>
	 * &lt;downloadJMeterDependencies&gt;
	 * &nbsp;&nbsp;&lt;false&gt;
	 * &lt;downloadJMeterDependencies&gt;
	 */
	@Parameter(defaultValue = "false")
	protected boolean downloadJMeterDependencies;

	/**
	 * Download all optional transitive dependencies of artifacts.
	 * <p/>
	 * &lt;downloadOptionalDependencies&gt;
	 * &nbsp;&nbsp;&lt;true&gt;
	 * &lt;downloadOptionalDependencies&gt;
	 */
	@Parameter(defaultValue = "false")
	protected boolean downloadOptionalDependencies;

	/**
	 * Download all dependencies of files you want to add to lib/junit and copy them to lib/junit too
	 * <p/>
	 * &lt;downloadLibraryDependencies&gt;
	 * &nbsp;&nbsp;&lt;true&gt;
	 * &lt;downloadLibraryDependencies&gt;
	 */
	@Parameter(defaultValue = "true")
	protected boolean downloadLibraryDependencies;

	/**
	 * A list of artifacts that should be copied into the lib/junit directory e.g.
	 * <p/>
	 * &lt;junitLibraries&gt;
	 * &nbsp;&nbsp;&lt;artifact&gt;com.foo.project.junit:junit-test:1.0.0&lt;/artifact&gt;
	 * &lt;junitLibraries&gt;
	 */
	@Parameter
	protected List<String> junitLibraries = new ArrayList<>();
	
    /**
     * A list of artifacts that are used by the test like JMS activemq.
     * In a Non Maven JMeter configuration, these libraries would be copied into the lib/ directory 
     * The maven plugin will copy those in lib/ folder of the built jmeter configuration
     * e.g.
     * <p/>
     * &lt;testPlanLibraries&gt;
     * &nbsp;&nbsp;&lt;artifact&gt;org.apache.activemq:activemq-client:5.15.2&lt;/artifact&gt;
     * &lt;junitLibraries&gt;
     */
    @Parameter
    protected List<String> testPlanLibraries = new ArrayList<>();

	/**
	 * Absolute path to JMeter custom (test dependent) properties file.
	 */
	@Parameter
	protected Map<String, String> propertiesJMeter = new HashMap<>();

	/**
	 * JMeter Properties that are merged with precedence into default JMeter file in saveservice.properties
	 */
	@Parameter
	protected Map<String, String> propertiesSaveService = new HashMap<>();

	/**
	 * JMeter Properties that are merged with precedence into default JMeter file in reportgenerator.properties
	 */
	@Parameter
	protected Map<String, String> propertiesReportGenerator = new HashMap<>();

	/**
	 * JMeter Properties that are merged with precedence into default JMeter file in upgrade.properties
	 */
	@Parameter
	protected Map<String, String> propertiesUpgrade = new HashMap<>();

	/**
	 * JMeter Properties that are merged with precedence into default JMeter file in user.properties
	 * user.properties takes precedence over jmeter.properties
	 */
	@Parameter
	protected Map<String, String> propertiesUser = new HashMap<>();

	/**
	 * JMeter Global Properties that override those given in jmeterProps. <br>
	 * This sets local and remote properties (JMeter's definition of global properties is actually remote properties)
	 * and overrides any local/remote properties already set
	 */
	@Parameter
	protected Map<String, String> propertiesGlobal = new HashMap<>();

	/**
	 * (Java) System properties set for the test run.
	 * Properties are merged with precedence into default JMeter file system.properties
	 */
	@Parameter
	protected Map<String, String> propertiesSystem = new HashMap<>();

	/**
	 * Path under which .properties files are stored.
	 * Defaults to ${basedir}/src/test/jmeter
	 */
	@Parameter(defaultValue = "${basedir}/src/test/jmeter")
	protected File propertiesFilesDirectory;

	/**
	 * Replace the default JMeter properties with any custom properties files supplied.
	 * (If set to false any custom properties files will be merged with the default JMeter properties files, custom properties will overwrite default ones)
	 */
	@Parameter(defaultValue = "true")
	protected boolean propertiesReplacedByCustomFiles;

	private Set<Exclusion> parsedExcludedArtifacts = new HashSet<>();
    /**
     * Dependency graph can contain circular references. 
     * For example: dom4j:dom4j:jar:1.5.2 and jaxen:jaxen:jar:1.1-beta-4
     * To prevent endless loop and stack overflow, save processed artifacts and check we did not process them previously before processing.
     * <p>
     * May be better to use {@link Artifact}, but {@link AbstractArtifact#equals} unreliably depends on local file path. 
     * So using {@link Exclusion} items
     * <p> 
     */
    private Set<Exclusion> processedArtifacts = new HashSet<>(); 
    
    /**
     * After our rework in the lib directory, there are a lot of artifacts with the same groupid, classifier and artifactId, 
     * but different versions.
     * This breaks jmeter. 
     * We will exclude duplicates at the last moment, at the stage of copying. 
     */
    private Set<Artifact> copiedArtifacts = new HashSet<>();

//	TODO move customPropertiesFiles here;

	/**
	 * Set the format of the results generated by JMeter
	 * Valid values are: xml, csv (CSV set by default).
	 */
	@Parameter(defaultValue = "csv")
	protected String resultsFileFormat;
	protected boolean resultsOutputIsCSVFormat = false;

	protected Artifact jmeterConfigArtifact;
	protected File customPropertiesDirectory;
	protected File libDirectory;
	protected File libExtDirectory;
	protected File libJUnitDirectory;

	/**
	 * Configure a local instance of JMeter
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException {
	    processedArtifacts.clear();
	    parsedExcludedArtifacts.clear();
	    JMeterConfigurationHolder.getInstance().resetConfiguration();
	    setupExcludedArtifacts(excludedArtifacts);
		getLog().info(LINE_SEPARATOR);
		getLog().info(" Configuring JMeter...");
		getLog().info(LINE_SEPARATOR);
		getLog().info(" Building JMeter directory structure...");
		generateJMeterDirectoryTree();
		getLog().info(" Configuring JMeter artifacts :"+jmeterArtifacts);
		configureJMeterArtifacts();
		getLog().info(" Populating JMeter directory ...");
		populateJMeterDirectoryTree();
		getLog().info(" Copying extensions "+jmeterExtensions+" to JMeter lib/ext directory "
		        +libExtDirectory+" with downloadExtensionDependencies set to "+downloadExtensionDependencies+" ...");
		copyExplicitLibraries(jmeterExtensions, libExtDirectory, downloadExtensionDependencies);
        getLog().info(" Copying  JUnit libraries "+junitLibraries+" to JMeter junit lib directory "
                +libJUnitDirectory+" with downloadLibraryDependencies set to "+downloadLibraryDependencies+" ...");
		copyExplicitLibraries(junitLibraries, libJUnitDirectory, downloadLibraryDependencies);
        getLog().info(" Copying test libraries "+testPlanLibraries+" to JMeter lib directory "
                +libDirectory+" with downloadLibraryDependencies set to "+downloadLibraryDependencies+" ...");
		copyExplicitLibraries(testPlanLibraries, libDirectory, downloadLibraryDependencies);
		getLog().info(" Configuring jmeter properties ...");
		configurePropertiesFiles();
		getLog().info(" Generating JSON Test config ...");
		generateTestConfig();
		JMeterConfigurationHolder.getInstance().freezeConfiguration();
	}

	/**
	 * Parses excludedArtifactsAsString and fills parsedExcludedArtifacts
	 * @param excludedArtifactsAsString List of exclusion
	 */
	private void setupExcludedArtifacts(List<String> excludedArtifactsAsString) {
	    // Exclude broken artifacts of old JMeter pom
	    // 1/ See https://bz.apache.org/bugzilla/show_bug.cgi?id=57555
	    parsedExcludedArtifacts.add(new Exclusion("d-haven-managed-pool", "d-haven-managed-pool", null, null));
	    parsedExcludedArtifacts.add(new Exclusion("event", "event", null, null));
	    // 2/ See https://bz.apache.org/bugzilla/show_bug.cgi?id=57734
	    parsedExcludedArtifacts.add(new Exclusion("commons-pool2", "commons-pool2", null, null));
	    parsedExcludedArtifacts.add(new Exclusion("commons-math3", "commons-math3", null, null));
	    
	    // Exclude conflicting libraries since JMeter 3.2
	    parsedExcludedArtifacts.add(new Exclusion("logkit", "logkit", null, null));
	    parsedExcludedArtifacts.add(new Exclusion("avalon-logkit", "avalon-logkit", null, null));

	    for (String exclusion : excludedArtifactsAsString) {
	        String[] exclusionParts = exclusion.split(":");  
	        parsedExcludedArtifacts.add(
	                new Exclusion(exclusionParts[0], 
	                        exclusionParts[1], 
	                        exclusionParts.length>2 ? exclusionParts[2] : null, 
	                        exclusionParts.length>3 ? exclusionParts[3] : null));
        }
    }

    /**
	 * Generate the directory tree utilised by JMeter.
	 */
	private void generateJMeterDirectoryTree() {
		File workingDirectory = new File(jmeterDirectory, "bin");
		workingDirectory.mkdirs();
		JMeterConfigurationHolder.getInstance().setWorkingDirectory(workingDirectory);
		customPropertiesDirectory = new File(jmeterDirectory, "custom_properties");
		customPropertiesDirectory.mkdirs();
		libDirectory = new File(jmeterDirectory, "lib");
		libExtDirectory = new File(libDirectory, "ext");
		libExtDirectory.mkdirs();
		libJUnitDirectory = new File(libDirectory, "junit");
		libJUnitDirectory.mkdirs();
		testFilesBuildDirectory.mkdirs();
		resultsDirectory.mkdirs();
		if(generateReports) {
		    reportDirectory.mkdirs();
		}
		logsDirectory.mkdirs();
	}

	private void configurePropertiesFiles() throws MojoExecutionException {
	    Map<ConfigurationFiles, PropertiesMapping> propertiesMap = 
	            new EnumMap<>(ConfigurationFiles.class);
	    JMeterConfigurationHolder.getInstance().setPropertiesMap(propertiesMap);
		propertiesMap.put(JMETER_PROPERTIES, new PropertiesMapping(propertiesJMeter));
		propertiesMap.put(SAVE_SERVICE_PROPERTIES, new PropertiesMapping(propertiesSaveService));
		propertiesMap.put(UPGRADE_PROPERTIES, new PropertiesMapping(propertiesUpgrade));
		propertiesMap.put(SYSTEM_PROPERTIES, new PropertiesMapping(propertiesSystem));
		propertiesMap.put(REPORT_GENERATOR_PROPERTIES, new PropertiesMapping(propertiesReportGenerator));
		propertiesMap.put(USER_PROPERTIES, new PropertiesMapping(propertiesUser));
		propertiesMap.put(GLOBAL_PROPERTIES, new PropertiesMapping(propertiesGlobal));

		setJMeterResultFileFormat();

		for (ConfigurationFiles configurationFile : values()) {
			File suppliedPropertiesFile = new File(propertiesFilesDirectory, configurationFile.getFilename());
			File propertiesFileToWrite = new File(
			        JMeterConfigurationHolder.getInstance().getWorkingDirectory(), configurationFile.getFilename());

			PropertiesFile somePropertiesFile = new PropertiesFile(jmeterConfigArtifact, configurationFile);
			somePropertiesFile.loadProvidedPropertiesIfAvailable(suppliedPropertiesFile, propertiesReplacedByCustomFiles);
			somePropertiesFile.addAndOverwriteProperties(propertiesMap.get(configurationFile).getAdditionalProperties());
			somePropertiesFile.writePropertiesToFile(propertiesFileToWrite);

			propertiesMap.get(configurationFile).setPropertiesFile(somePropertiesFile);
		}

		for (File customPropertiesFile : customPropertiesFiles) {
			PropertiesFile customProperties = new PropertiesFile(customPropertiesFile);
			String customPropertiesFilename = 
			        FilenameUtils.getBaseName(customPropertiesFile.getName()) 
			        + "-" + UUID.randomUUID().toString() 
			        + "." + FilenameUtils.getExtension(customPropertiesFile.getName());
			customProperties.writePropertiesToFile(new File(customPropertiesDirectory, customPropertiesFilename));
		}

		setDefaultPluginProperties(JMeterConfigurationHolder.getInstance().getWorkingDirectory().getAbsolutePath());
	}

	protected void generateTestConfig() throws MojoExecutionException {
	    try (InputStream configFile = this.getClass().getResourceAsStream(BASE_CONFIG_FILE)) {
    		TestConfig testConfig = new TestConfig(configFile);
    		testConfig.setResultsOutputIsCSVFormat(resultsOutputIsCSVFormat);
    		testConfig.setGenerateReports(generateReports);
    		testConfig.writeResultFilesConfigTo(testConfigFile);
	    } catch(java.io.IOException ex) {
	        throw new MojoExecutionException("Exception creating TestConfig", ex);
	    }
	}

	protected void setJMeterResultFileFormat() {
		if (generateReports || "csv".equalsIgnoreCase(resultsFileFormat)) {
			propertiesJMeter.put("jmeter.save.saveservice.output_format", "csv");
			resultsOutputIsCSVFormat = true;
		} else {
			propertiesJMeter.put("jmeter.save.saveservice.output_format", "xml");
			resultsOutputIsCSVFormat = false;
		}
	}


	public void setDefaultPluginProperties(String userDirectory) {
		//JMeter uses the system property "user.dir" to set its base working directory
		System.setProperty("user.dir", userDirectory);
		//Prevent JMeter from throwing some System.exit() calls
		System.setProperty("jmeterengine.remote.system.exit", "false");
		System.setProperty("jmeterengine.stopfail.system.exit", "false");
	}

	/**
	 * This sets the default list of artifacts that we use to set up a local instance of JMeter.
	 * We only use this default list if &lt;jmeterArtifacts&gt; has not been overridden in the POM.
	 */
	private void configureJMeterArtifacts() {
		if (jmeterArtifacts.isEmpty()) {
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_components:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_config:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_core:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_ftp:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_functions:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_http:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_java:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_jdbc:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_jms:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_junit:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_ldap:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_mail:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_mongodb:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_native:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_tcp:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":jorphan:" + jmeterVersion);
		}
	}

	private void populateJMeterDirectoryTree() throws DependencyResolutionException, IOException {
		if (jmeterArtifacts.isEmpty()) {
			throw new DependencyResolutionException("No JMeter dependencies specified!, check jmeterArtifacts and jmeterVersion elements");
		}
		for (String desiredArtifact : jmeterArtifacts) {
			Artifact returnedArtifact = getArtifactResult(new DefaultArtifact(desiredArtifact));
			switch (returnedArtifact.getArtifactId()) {
				case JMETER_CONFIG_ARTIFACT_NAME:
					jmeterConfigArtifact = returnedArtifact;
					extractConfigSettings(jmeterConfigArtifact);
					break;
				case JORPHAN_ARTIFACT_NAME:
                    copyArtifact(returnedArtifact, libDirectory);
                    copyTransitiveRuntimeDependenciesToLibDirectory(returnedArtifact, downloadJMeterDependencies);
                    break;
				case JMETER_ARTIFACT_NAME:
				    JMeterConfigurationHolder.getInstance().setRuntimeJarName(returnedArtifact.getFile().getName());
					copyArtifact(returnedArtifact, JMeterConfigurationHolder.getInstance().getWorkingDirectory());
					copyTransitiveRuntimeDependenciesToLibDirectory(returnedArtifact, downloadJMeterDependencies);
					break;
				default:
					copyArtifact(returnedArtifact, libExtDirectory);
					copyTransitiveRuntimeDependenciesToLibDirectory(returnedArtifact, downloadJMeterDependencies);
			}
		}

		if (confFilesDirectory.exists()) {
			copyFilesInTestDirectory(confFilesDirectory, new File(jmeterDirectory, "bin"));
		}
	}

	/**
	 * Copy a list of libraries to a specific folder.
	 *
	 * @param desiredArtifacts A list of artifacts
	 * @param destination      A destination folder to copy these artifacts to
	 * @param downloadDependencies Do we download dependencies
	 * @throws DependencyResolutionException
	 * @throws IOException
	 */
	private void copyExplicitLibraries(List<String> desiredArtifacts, File destination, boolean downloadDependencies) throws DependencyResolutionException, IOException {
		for (String desiredArtifact : desiredArtifacts) {
            copyExplicitLibraries(desiredArtifact, destination, downloadDependencies);
        }
    }

	/**
	 * 
	 * @param desiredArtifact  Artifact to copy
     * @param destination      A destination folder to copy these artifacts to
     * @param downloadDependencies Do we download dependencies
	 * @throws DependencyResolutionException
	 * @throws IOException
	 */
    private void copyExplicitLibraries(String desiredArtifact, File destination, boolean downloadDependencies)
            throws DependencyResolutionException, IOException {
        Artifact returnedArtifact = getArtifactResult(new DefaultArtifact(desiredArtifact));
        copyArtifact(returnedArtifact, destination);
        if (downloadDependencies) {
            resolveTestDependenciesAndCopyWithTransitivity(returnedArtifact, true);
        }
    }
	
	/**
	 * Find a specific artifact in a remote repository
	 *
	 * @param desiredArtifact The artifact that we want to find
	 * @return Will return an ArtifactResult object
	 * @throws DependencyResolutionException
	 */
	private Artifact getArtifactResult(Artifact desiredArtifact) 
	        throws DependencyResolutionException {// NOSONAR
		ArtifactRequest artifactRequest = new ArtifactRequest();
		artifactRequest.setArtifact(desiredArtifact);
		artifactRequest.setRepositories(repositoryList);
		try {
			return repositorySystem.resolveArtifact(repositorySystemSession, artifactRequest).getArtifact();
		} catch (ArtifactResolutionException e) {
			throw new DependencyResolutionException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param artifact {@link Artifact}
	 * @param getDependenciesOfDependency get dependencies of dependency
	 * @throws DependencyResolutionException
	 * @throws IOException
	 */
    private void resolveTestDependenciesAndCopyWithTransitivity(Artifact artifact, boolean getDependenciesOfDependency) throws DependencyResolutionException, IOException {
        ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(artifact, repositoryList, null);
        try {
            ArtifactDescriptorResult result = repositorySystem.readArtifactDescriptor(repositorySystemSession, request);
            for (Dependency dep: result.getDependencies()){
                // Here we can not filter dependencies by scope. 
                // we need to use dependencies with any scope, because tests are needed to test, 
                // and provided, and especially compile-scoped dependencies  
                ArtifactResult artifactResult = repositorySystem.resolveArtifact(repositorySystemSession,
                        new ArtifactRequest(dep.getArtifact(), repositoryList, null));
                if(isLibraryArtifact(artifactResult.getArtifact())){
                    copyArtifact(artifactResult.getArtifact(), libDirectory);
                } else {
                    getLog().debug("Artifact "+artifactResult.getArtifact()+" is not a library, ignoring");
                }
                copyTransitiveRuntimeDependenciesToLibDirectory(dep, getDependenciesOfDependency);
            }
        } catch (ArtifactDescriptorException | ArtifactResolutionException e) {
            throw new DependencyResolutionException(e.getMessage(), e);
        }   
    }

    /**
     * Collate a list of transitive runtime dependencies that need to be copied to the /lib directory and then copy them there.
     *
     * @param artifact The artifact that is a transitive dependency
     * @param getDependenciesOfDependency get dependencies of dependency
     * @throws DependencyResolutionException
     * @throws IOException
     */
    private void copyTransitiveRuntimeDependenciesToLibDirectory(Artifact artifact, boolean getDependenciesOfDependency) throws DependencyResolutionException, IOException {
        copyTransitiveRuntimeDependenciesToLibDirectory(new Dependency(artifact, DEPENDENCIES_DEFAULT_SEARCH_SCOPE), getDependenciesOfDependency); 
    }
    
	/**
	 * Collate a list of transitive runtime dependencies that need to be copied to the /lib directory and then copy them there.
	 *
	 * @param rootDependency {@link Dependency} The artifact that is a transitive dependency
	 * @param getDependenciesOfDependency get dependencies of dependency
	 * @throws DependencyResolutionException
	 * @throws IOException
	 */
	private void copyTransitiveRuntimeDependenciesToLibDirectory(Dependency rootDependency, boolean getDependenciesOfDependency) 
	        throws DependencyResolutionException, IOException {
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(rootDependency);
		collectRequest.setRepositories(repositoryList);
		// In #classpathFilter, we are not actually passing the scope, but the classpath identifier (just using the same enum as for the scope).
        // That is, for example, for a test classpath, dependencies are required with any scope (that is, the TEST filter is the softest)
		
		DependencyFilter dependencyFilter = 
		        DependencyFilterUtils.andFilter(DependencyFilterUtils.classpathFilter(DEPENDENCIES_DEFAULT_SEARCH_SCOPE),
		                (DependencyNode dependencyNode, List<DependencyNode> arg1) -> {
                                Artifact artifact = dependencyNode.getArtifact();
                                if (dependencyNode.getDependency().isOptional()) {
                                    getLog().debug("Filtering dependency "+dependencyNode.getDependency());
                                    return false;
                                }
                                for(Exclusion currentExclusion: parsedExcludedArtifacts){
                                    if (currentExclusion.getGroupId().equals(artifact.getGroupId()) &&
                                            (currentExclusion.getArtifactId().equals(artifact.getArtifactId())) 
                                            || (currentExclusion.getArtifactId().equals(ARTIFACT_STAR))){
                                        getLog().debug("Filtering excluded dependency "+dependencyNode.getDependency());
                                        return false;
                                    }
                                }
                                return true;
                            });
		DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, dependencyFilter);

        if (getLog().isDebugEnabled()) {
            getLog().debug("Root dependency name: " + rootDependency.toString());
            if ((dependencyRequest.getCollectRequest() != null) && (dependencyRequest.getCollectRequest().getTrace() != null)){
                getLog().debug("Root dependency request trace: " + dependencyRequest.getCollectRequest().getTrace().toString());
            }
            getLog().debug("Root dependency exclusions: " + rootDependency.getExclusions());
            getLog().debug(LINE_SEPARATOR);
        }
		try {
		    // here we can not resolve, since exclusions can be caught, which are therefore excluded, which are absent in the repositories.
		    List<DependencyNode> artifactDependencyNodes = 
		            repositorySystem.collectDependencies(repositorySystemSession, collectRequest).getRoot().getChildren();
			for (DependencyNode dependencyNode : artifactDependencyNodes) {
				if (getLog().isDebugEnabled()) {
					getLog().debug("Dependency name: " + dependencyNode.toString());
					if ((dependencyRequest.getCollectRequest() != null) && (dependencyRequest.getCollectRequest().getTrace() != null)){
					    getLog().debug("Dependency request trace: " + 
					            dependencyRequest.getCollectRequest().getTrace().toString());
					}
					getLog().debug(LINE_SEPARATOR);
				}
                Exclusion dummyExclusion = new Exclusion(
                        dependencyNode.getArtifact().getGroupId(), 
                        dependencyNode.getArtifact().getArtifactId(), 
                        dependencyNode.getArtifact().getClassifier(), 
                        dependencyNode.getArtifact().getExtension());
				if ((downloadOptionalDependencies || !dependencyNode.getDependency().isOptional()) &&
				        ! containsEx(parsedExcludedArtifacts, dummyExclusion) &&
                        !((rootDependency.getExclusions() != null) && (containsEx(rootDependency.getExclusions(), dummyExclusion)) ) ) {				        
					Artifact returnedArtifact = repositorySystem.resolveArtifact(repositorySystemSession,
					        new ArtifactRequest(dependencyNode)).getArtifact();
					if ((!returnedArtifact.getArtifactId().startsWith(JMETER_ARTIFACT_PREFIX)) && (isLibraryArtifact(returnedArtifact))){
                        copyArtifact(returnedArtifact, libDirectory);
                    }

                    if (getDependenciesOfDependency && !processedArtifacts.contains(dummyExclusion)) {
                        processedArtifacts.add(dummyExclusion);
                        if (getLog().isDebugEnabled()) {
                            getLog().debug("Added to processed list: " + dummyExclusion);
                            getLog().debug("total processed: " + processedArtifacts.size());
                            getLog().debug(LINE_SEPARATOR);
                        }
                        copyTransitiveRuntimeDependenciesToLibDirectory(dependencyNode.getDependency(), true);                        
                    }
				}
			}
		} catch (DependencyCollectionException | ArtifactResolutionException e) {
			throw new DependencyResolutionException(e.getMessage(), e);
		}
	}

    /**
     * Exclusive can be specified by wildcard:
     * -- groupId:artifactId:*:*
     * -- groupId:*:*:*
     * <p>
     * And in general, to require a strict match up to the version and the classifier is not necessary
     * <p>
     * TODO: the correct fix would be to rewrite {@link Exclusion # equals (Object)}, but what about the boundary case: 
     * If contains (id1: *: *: *, id1: id2: *: *) == true, then that's equals ??    
     * TODO: there must be useful code in Aether or maven on this topic
     * <p>
     * @param exclusions
     * @param exclusion
     * @return
     */
    private boolean containsEx(Collection<Exclusion> exclusions, Exclusion exclusion){
        if(exclusion != null && exclusions != null) {
            for(Exclusion currentExclusion: exclusions){
                if (currentExclusion.getGroupId().equals(exclusion.getGroupId()) &&
                        (currentExclusion.getArtifactId().equals(exclusion.getArtifactId()) || (currentExclusion.getArtifactId().equals(ARTIFACT_STAR)))) {
                    return true;
                }
            }
        }
        return false;
    }
	
    /**
     * Is artifact a library ?
     * @param artifact {@link Artifact}
     * @return boolean if true
     */
    private boolean isLibraryArtifact(Artifact artifact){
        return artifact.getExtension().equals("jar") || 
                artifact.getExtension().equals("war") || 
                artifact.getExtension().equals("zip") || 
                artifact.getExtension().equals("ear");        
    }

    /**
	 * Copy an Artifact to a directory
	 *
	 * @param artifact             Artifact that needs to be copied.
	 * @param destinationDirectory Directory to copy the artifact to.
	 * @throws IOException                   Unable to copy file
	 * @throws DependencyResolutionException Unable to resolve dependency
	 */
	private void copyArtifact(Artifact artifact, File destinationDirectory) 
	        throws IOException, DependencyResolutionException {// NOSONAR
		for (String ignoredArtifact : ignoredArtifacts) {
			Artifact artifactToIgnore = getArtifactResult(new DefaultArtifact(ignoredArtifact));
			if (artifact.getFile().getName().equals(artifactToIgnore.getFile().getName())) {
				getLog().debug(artifact.getFile().getName() + " has not been copied over because it is in the ignore list.");
				return;
			}
		}
		try {
		    for (Iterator<Artifact> iterator = copiedArtifacts.iterator(); iterator.hasNext();) {
		        Artifact currentArtifact = iterator.next();
                if(currentArtifact.getGroupId().equals(artifact.getGroupId()) && 
                        currentArtifact.getArtifactId().equals(artifact.getArtifactId()) && 
                        currentArtifact.getExtension().equals(artifact.getExtension()) && 
                        currentArtifact.getClassifier().equals(artifact.getClassifier())){
                    // already copied, but perhaps the version right now is more recent than it was.
                    // We keep the most recent one
                    GenericVersionScheme genericVersionScheme = new GenericVersionScheme();
                    Version currentArtifactVersion = genericVersionScheme.parseVersion(currentArtifact.getVersion());
                    Version artifactVersion = genericVersionScheme.parseVersion(artifact.getVersion());
                    if (currentArtifactVersion.compareTo(artifactVersion) >= 0){
                        // the version of the already copied artifact above or the same, do not copy, do nothing
                        return;
                    } else{
                        // We delete the old artifact, and copy it
                        // (here we only delete, we will copy by the output from the loop)
                        File artifactToDelete = new File(destinationDirectory, currentArtifact.getFile().getName());
                        getLog().debug("Deleting file:'"+artifactToDelete.getAbsolutePath()+"'");
                        FileUtils.forceDelete(artifactToDelete);
                        iterator.remove();
                        break;
                    }
                }
            }
            copiedArtifacts.add(artifact);
			File artifactToCopy = new File(destinationDirectory, artifact.getFile().getName());
			getLog().debug("Checking: " + artifactToCopy.getAbsolutePath() + "...");
			if (!artifactToCopy.exists()) {
				getLog().debug("Copying: " + artifactToCopy.getAbsolutePath() + " to "+destinationDirectory.getAbsolutePath());
				FileUtils.copyFileToDirectory(artifact.getFile(), destinationDirectory);
			}
		} catch (java.io.IOException | InvalidVersionSpecificationException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Extract the configuration settings (not properties files) from the configuration artifact 
	 * and load them into the /bin directory
	 *
	 * @param artifact Configuration artifact
	 * @throws IOException
	 */
	private void extractConfigSettings(Artifact artifact) 
	        throws IOException  {// NOSONAR
		try (JarFile configSettings = new JarFile(artifact.getFile())) {
			Enumeration<JarEntry> entries = configSettings.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarFileEntry = entries.nextElement();
				// Only interested in files in the /bin directory that are not properties files
				if (!jarFileEntry.isDirectory() && jarFileEntry.getName().startsWith("bin") 
				        && !jarFileEntry.getName().endsWith(".properties")) {
					File fileToCreate = new File(jmeterDirectory, jarFileEntry.getName());
					copyInputStreamToFile(configSettings.getInputStream(jarFileEntry), fileToCreate);
				}
			}
		} catch (java.io.IOException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
}
