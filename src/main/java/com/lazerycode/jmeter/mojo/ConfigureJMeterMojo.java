package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.configuration.ArtifactHelpers;
import com.lazerycode.jmeter.configuration.RepositoryConfiguration;
import com.lazerycode.jmeter.json.TestConfigurationWrapper;
import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesFile;
import com.lazerycode.jmeter.properties.PropertiesMapping;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
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
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.version.InvalidVersionSpecificationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.lazerycode.jmeter.configuration.ArtifactHelpers.*;
import static com.lazerycode.jmeter.properties.ConfigurationFiles.*;

/**
 * Goal that configures Apache JMeter bundle.<br>
 * This goal is also called by other goals.<br>
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

    @Parameter
    protected List<RepositoryConfiguration> additionalRepositories = new ArrayList<>();

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
    @Parameter(property = "jmeter.version", defaultValue = "5.5")
    private String jmeterVersion;

    /**
     * A list of artifacts that we use to configure JMeter.
     * This list is hard coded by default, you can override this list and supply your own list of artifacts for JMeter.
     * This would be useful if you want to use a different version of JMeter that has a different list of required artifacts.
     * <br>
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
     * <br>
     * &lt;excludedArtifacts&gt;
     * &lt;exclusion&gt;commons-pool2:commons-pool2&lt;/exclusion&gt;
     * &lt;exclusion&gt;commons-math3:commons-math3&lt;/exclusion&gt;
     * &lt;excludedArtifacts&gt;
     */
    @Parameter
    private List<String> excludedArtifacts = new ArrayList<>();

    /**
     * A list of artifacts that the plugin should ignore.
     * This would be useful if you don't want specific dependencies brought down by JMeter (or any uzsed defined artifacts) copied into the JMeter directory structure.
     * <br>
     * &lt;ignoredArtifacts&gt;
     * &nbsp;&nbsp;&lt;artifact&gt;org.bouncycastle:bcprov-jdk15on:1.49&lt;/artifact&gt;
     * &lt;ignoredArtifacts&gt;
     */
    @Parameter
    private List<String> ignoredArtifacts = new ArrayList<>();

    /**
     * Download all dependencies of files you want to add to lib/ext and copy them to lib/ext too
     * <br>
     * &lt;downloadExtensionDependencies&gt;
     * &nbsp;&nbsp;&lt;true&gt;
     * &lt;downloadExtensionDependencies&gt;
     */
    @Parameter(defaultValue = "true")
    protected boolean downloadExtensionDependencies;

    /**
     * A list of artifacts that should be copied into the lib/ext directory e.g.
     * <br>
     * &lt;jmeterExtensions&gt;
     * &nbsp;&nbsp;&lt;artifact&gt;kg.apc:jmeter-plugins:1.3.1&lt;/artifact&gt;
     * &lt;jmeterExtensions&gt;
     */
    @Parameter
    protected List<String> jmeterExtensions = new ArrayList<>();

    /**
     * Download all transitive dependencies of the JMeter artifacts.
     * <br>
     * &lt;downloadJMeterDependencies&gt;
     * &nbsp;&nbsp;&lt;false&gt;
     * &lt;downloadJMeterDependencies&gt;
     */
    @Parameter(defaultValue = "true")
    protected boolean downloadJMeterDependencies;

    /**
     * Download all optional transitive dependencies of artifacts.
     * <br>
     * &lt;downloadOptionalDependencies&gt;
     * &nbsp;&nbsp;&lt;true&gt;
     * &lt;downloadOptionalDependencies&gt;
     */
    @Parameter(defaultValue = "false")
    protected boolean downloadOptionalDependencies;

    /**
     * Download all dependencies of files you want to add to lib/junit and copy them to lib/junit too
     * <br>
     * &lt;downloadLibraryDependencies&gt;
     * &nbsp;&nbsp;&lt;true&gt;
     * &lt;downloadLibraryDependencies&gt;
     */
    @Parameter(defaultValue = "true")
    protected boolean downloadLibraryDependencies;

    /**
     * A list of artifacts that should be copied into the lib/junit directory e.g.
     * <br>
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
     * <br>
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
    protected boolean resultsOutputIsCSVFormat = true;
    protected Artifact jmeterConfigArtifact;
    protected Path customPropertiesDirectory;
    protected Path jmeterDirectoryPath;
    protected Path binDirectory;
    protected Path libDirectory;
    protected Path libExtDirectory;
    protected Path libJUnitDirectory;

    /**
     * Configure a local instance of JMeter
     *
     * @throws MojoExecutionException Exception
     */
    @Override
    public void doExecute() throws MojoExecutionException {
        getLog().info(" ");
        getLog().info(LINE_SEPARATOR);
        getLog().info("C O N F I G U R I N G    J M E T E R");
        getLog().info(LINE_SEPARATOR);
        getLog().info(" ");
        getLog().info("Creating test configuration for execution ID: " + this.mojoExecution.getExecutionId());
        testConfig = new TestConfigurationWrapper();
        testConfig.getCurrentTestConfiguration().setExecutionID(this.mojoExecution.getExecutionId());
        testConfig.getCurrentTestConfiguration().setGenerateReports(generateReports);
        processedArtifacts.clear();
        parsedExcludedArtifacts = setupExcludedArtifacts(excludedArtifacts);
        getLog().info("Building JMeter directory structure...");
        getLog().info("Generating JSON Test config...");
        jmeterDirectoryPath = Paths.get(projectBuildDirectory.getAbsolutePath(), UUID.randomUUID().toString(), "jmeter");
        testConfig.getCurrentTestConfiguration().setJmeterDirectoryPath(jmeterDirectoryPath.toString());
        generateJMeterDirectoryTree();
        getLog().info("Configuring JMeter artifacts...");
        configureJMeterArtifacts();
        getLog().info("Populating JMeter directory...");
        populateJMeterDirectoryTree();
        copyExplicitLibraries(jmeterExtensions, libExtDirectory.toFile(), downloadExtensionDependencies, "extensions");
        copyExplicitLibraries(junitLibraries, libJUnitDirectory.toFile(), downloadLibraryDependencies, "junit libraries");
        copyExplicitLibraries(testPlanLibraries, libDirectory.toFile(), downloadLibraryDependencies, "test plan libraries");
        getLog().info("Configuring JMeter properties...");
        configurePropertiesFiles();
        testConfig.writeResultFilesConfigTo(testConfigFile);
    }

    /**
     * Generate the directory tree utilised by JMeter.
     */
    private void generateJMeterDirectoryTree() throws MojoExecutionException {
        binDirectory = jmeterDirectoryPath.resolve("bin");
        customPropertiesDirectory = jmeterDirectoryPath.resolve("custom_properties");
        libDirectory = jmeterDirectoryPath.resolve("lib");
        libExtDirectory = libDirectory.resolve("ext");
        libJUnitDirectory = libDirectory.resolve("junit");

        try {
            Files.createDirectories(jmeterDirectoryPath);
            Files.createDirectories(binDirectory);
            Files.createDirectories(customPropertiesDirectory);
            Files.createDirectories(libExtDirectory);
            Files.createDirectories(libJUnitDirectory);

            testFilesBuildDirectory.mkdirs();
            resultsDirectory.mkdirs();
            if (generateReports) {
                reportDirectory.mkdirs();
            }
            logsDirectory.mkdirs();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void configurePropertiesFiles() throws MojoExecutionException {
        Map<ConfigurationFiles, PropertiesMapping> propertiesMap = new EnumMap<>(ConfigurationFiles.class);
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
            File propertiesFileToWrite = new File(testConfig.getCurrentTestConfiguration().getJmeterWorkingDirectoryPath(), configurationFile.getFilename());
            PropertiesFile somePropertiesFile = new PropertiesFile(jmeterConfigArtifact, configurationFile);
            somePropertiesFile.loadProvidedPropertiesIfAvailable(suppliedPropertiesFile, propertiesReplacedByCustomFiles);
            somePropertiesFile.addAndOverwriteProperties(propertiesMap.get(configurationFile).getAdditionalProperties());
            somePropertiesFile.writePropertiesToFile(propertiesFileToWrite);
            propertiesMap.get(configurationFile).setPropertiesFile(somePropertiesFile);
        }

        for (File customPropertiesFile : customPropertiesFiles) {
            PropertiesFile customProperties = new PropertiesFile(customPropertiesFile);
            String customPropertiesFilename = FilenameUtils.getBaseName(customPropertiesFile.getName())
                    + "-" + UUID.randomUUID()
                    + "." + FilenameUtils.getExtension(customPropertiesFile.getName());
            customProperties.writePropertiesToFile(customPropertiesDirectory.resolve(customPropertiesFilename).toFile());
        }

        testConfig.getCurrentTestConfiguration().setPropertiesMap(propertiesMap);
        setDefaultPluginProperties(testConfig.getCurrentTestConfiguration().getJmeterWorkingDirectoryPath().getAbsolutePath());
    }

    protected void setJMeterResultFileFormat() {
        if (generateReports || "csv".equalsIgnoreCase(resultsFileFormat)) {
            propertiesJMeter.put("jmeter.save.saveservice.output_format", "csv");
            resultsOutputIsCSVFormat = true;
        } else {
            propertiesJMeter.put("jmeter.save.saveservice.output_format", "xml");
            resultsOutputIsCSVFormat = false;
        }
        testConfig.getCurrentTestConfiguration().setResultsOutputIsCSVFormat(resultsOutputIsCSVFormat);
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
            jmeterArtifacts = createDefaultJmeterArtifactsArray(jmeterVersion);
        }
        getLog().debug("JMeter Artifact List:");
        jmeterArtifacts.forEach(artifact ->
                getLog().debug(artifact)
        );

    }

    private void populateJMeterDirectoryTree() throws MojoExecutionException {
        if (jmeterArtifacts.isEmpty()) {
            throw new MojoExecutionException("No JMeter dependencies specified!, check jmeterArtifacts and jmeterVersion elements");
        }
        for (String desiredArtifact : jmeterArtifacts) {
            Artifact returnedArtifact = getArtifactResult(new DefaultArtifact(desiredArtifact));
            switch (returnedArtifact.getArtifactId()) {
                case JMETER_CONFIG_ARTIFACT_NAME:
                    jmeterConfigArtifact = returnedArtifact;
                    extractConfigSettings(jmeterConfigArtifact);
                    break;
                case JMETER_ARTIFACT_NAME:
                    testConfig.getCurrentTestConfiguration().setRuntimeJarName(returnedArtifact.getFile().getName());
                    copyArtifactIfRequired(returnedArtifact, binDirectory);
                    copyTransitiveRuntimeDependenciesToLibDirectory(returnedArtifact, downloadJMeterDependencies);
                    break;
                case JORPHAN_ARTIFACT_NAME:
                    copyArtifactIfRequired(returnedArtifact, libDirectory);
                    copyTransitiveRuntimeDependenciesToLibDirectory(returnedArtifact, downloadJMeterDependencies);
                    break;
                default:
                    copyArtifactIfRequired(returnedArtifact, libExtDirectory);
                    copyTransitiveRuntimeDependenciesToLibDirectory(returnedArtifact, downloadJMeterDependencies);
            }
        }
        if (confFilesDirectory.exists()) {
            copyFilesInTestDirectory(confFilesDirectory, binDirectory.toFile());
        }
    }

    /**
     * Copy a list of libraries to a specific folder.
     *
     * @param desiredArtifacts     A list of artifacts
     * @param destination          A destination folder to copy these artifacts to
     * @param downloadDependencies Do we download dependencies
     * @throws MojoExecutionException MojoExecutionException
     */
    private void copyExplicitLibraries(List<String> desiredArtifacts, File destination, boolean downloadDependencies, String description) throws MojoExecutionException {
        getLog().info(String.format("Copying %s to %s", description, destination));
        getLog().info(String.format("Downloading dependencies: %s", downloadDependencies));
        for (String desiredArtifact : desiredArtifacts) {
            copyExplicitLibrary(desiredArtifact, destination, downloadDependencies);
        }
    }

    /**
     * @param desiredArtifact      Artifact to copy
     * @param destination          A destination folder to copy these artifacts to
     * @param downloadDependencies Do we download dependencies
     * @throws MojoExecutionException MojoExecutionException
     */
    private void copyExplicitLibrary(String desiredArtifact, File destination, boolean downloadDependencies) throws MojoExecutionException {
        getLog().debug(String.format("Copying %s to %s", desiredArtifact, destination.getAbsolutePath()));
        Artifact returnedArtifact = getArtifactResult(new DefaultArtifact(desiredArtifact));
        copyArtifactIfRequired(returnedArtifact, Paths.get(destination.toURI()));
        if (downloadDependencies) {
            resolveTestDependenciesAndCopyWithTransitivity(returnedArtifact);
        }
    }

    /**
     * Find a specific artifact in a remote repository
     *
     * @param desiredArtifact The artifact that we want to find
     * @return Will return an ArtifactResult object
     * @throws MojoExecutionException MojoExecutionException
     */
    private Artifact getArtifactResult(Artifact desiredArtifact) throws MojoExecutionException {// NOSONAR
        try {
            ArtifactRequest artifactRequest = new ArtifactRequest().setArtifact(ArtifactHelpers.resolveArtifactVersion(repositorySystem, repositorySystemSession, repositoryList, desiredArtifact));
            additionalRepositories.forEach((additionalRepository) -> repositoryList.add(additionalRepository.getRemoteRepository()));
            artifactRequest.setRepositories(repositoryList);
            return repositorySystem.resolveArtifact(repositorySystemSession, artifactRequest).getArtifact();
        } catch (ArtifactResolutionException | VersionRangeResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * @param artifact {@link Artifact}
     * @throws MojoExecutionException MojoExecutionException
     */
    private void resolveTestDependenciesAndCopyWithTransitivity(Artifact artifact) throws MojoExecutionException {
        ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(artifact, repositoryList, null);
        try {
            ArtifactDescriptorResult result = repositorySystem.readArtifactDescriptor(repositorySystemSession, request);
            for (Dependency dep : result.getDependencies()) {
                // Here we can not filter dependencies by scope, we need to use dependencies with any scope
                // This is because JMeter tests use test, provided, and compile-scoped dependencies
                ArtifactRequest artifactRequest = new ArtifactRequest(ArtifactHelpers.resolveArtifactVersion(repositorySystem, repositorySystemSession, repositoryList, dep.getArtifact()), repositoryList, null);
                ArtifactResult artifactResult = repositorySystem.resolveArtifact(repositorySystemSession, artifactRequest);
                if (isArtifactALibrary(artifactResult.getArtifact())) {
                    copyArtifactIfRequired(artifactResult.getArtifact(), libDirectory);
                } else {
                    getLog().debug("Artifact " + artifactResult.getArtifact() + " is not a library, ignoring");
                }
                copyTransitiveRuntimeDependenciesToLibDirectory(dep, true);
            }
        } catch (ArtifactDescriptorException | ArtifactResolutionException | VersionRangeResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Collate a list of transitive runtime dependencies that need to be copied to the /lib directory and then copy them there.
     *
     * @param artifact                    The artifact that is a transitive dependency
     * @param getDependenciesOfDependency get dependencies of dependency
     * @throws MojoExecutionException MojoExecutionException
     */
    private void copyTransitiveRuntimeDependenciesToLibDirectory(Artifact artifact, boolean getDependenciesOfDependency) throws MojoExecutionException {
        copyTransitiveRuntimeDependenciesToLibDirectory(new Dependency(artifact, DEPENDENCIES_DEFAULT_SEARCH_SCOPE), getDependenciesOfDependency);
    }

    /**
     * Collate a list of transitive runtime dependencies that need to be copied to the /lib directory and then copy them there.
     *
     * @param rootDependency              {@link Dependency} The artifact that is a transitive dependency
     * @param getDependenciesOfDependency get dependencies of dependency
     * @throws MojoExecutionException MojoExecutionException
     */
    private void copyTransitiveRuntimeDependenciesToLibDirectory(Dependency rootDependency, boolean getDependenciesOfDependency) throws MojoExecutionException {
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
                                getLog().debug("Filtering dependency " + dependencyNode.getDependency());
                                return false;
                            }

                            boolean notExcluded = artifactIsNotExcluded(parsedExcludedArtifacts, artifact);
                            if (!notExcluded) {
                                getLog().debug("Filtering excluded dependency " + dependencyNode.getDependency());
                            }

                            return notExcluded;
                        });
        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, dependencyFilter);
        CollectRequest dependencyRequestCollect = dependencyRequest.getCollectRequest();
        RequestTrace dependencyRequestTrace = dependencyRequestCollect.getTrace();

        if (getLog().isDebugEnabled()) {
            getLog().debug("Root dependency name: " + rootDependency.toString());
            if ((dependencyRequestCollect != null) && (dependencyRequestTrace != null)) {
                getLog().debug("Root dependency request trace: " + dependencyRequestTrace.toString());
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
                    if ((dependencyRequestCollect != null) && (dependencyRequestTrace != null)) {
                        getLog().debug("Dependency request trace: " +
                                dependencyRequestTrace.toString());
                    }
                }
                Exclusion dummyExclusion = new Exclusion(
                        dependencyNode.getArtifact().getGroupId(),
                        dependencyNode.getArtifact().getArtifactId(),
                        dependencyNode.getArtifact().getClassifier(),
                        dependencyNode.getArtifact().getExtension());
                if ((downloadOptionalDependencies || !dependencyNode.getDependency().isOptional()) &&
                        !containsExclusion(parsedExcludedArtifacts, dummyExclusion) &&
                        !((rootDependency.getExclusions() != null) && (containsExclusion(rootDependency.getExclusions(), dummyExclusion)))) {
                    ArtifactRequest artifactRequest = new ArtifactRequest(dependencyNode);
                    artifactRequest.setArtifact(ArtifactHelpers.resolveArtifactVersion(repositorySystem, repositorySystemSession, repositoryList, artifactRequest.getArtifact()));
                    Artifact returnedArtifact = repositorySystem.resolveArtifact(repositorySystemSession, artifactRequest).getArtifact();
                    if ((!returnedArtifact.getArtifactId().startsWith(JMETER_ARTIFACT_PREFIX)) && (isArtifactALibrary(returnedArtifact))) {
                        copyArtifactIfRequired(returnedArtifact, libDirectory);
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
        } catch (DependencyCollectionException | ArtifactResolutionException | VersionRangeResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Copy an Artifact to a directory
     *
     * @param artifactToCopy       Artifact that needs to be copied.
     * @param destinationDirectory Directory to copy the artifact to
     * @return true if artifact copied, false if artifact not copied
     * @throws MojoExecutionException Unable to copy file or resolve dependency, or unable to find artifact or unable to parse Artifact version
     */
    private boolean copyArtifactIfRequired(Artifact artifactToCopy, Path destinationDirectory) throws MojoExecutionException {
        for (String ignoredArtifact : ignoredArtifacts) {
            Artifact artifactToIgnore = getArtifactResult(new DefaultArtifact(ignoredArtifact));
            if (artifactToCopy.getFile().getName().equals(artifactToIgnore.getFile().getName())) {
                getLog().debug(artifactToCopy.getFile().getName() + " has not been copied over because it is in the ignore list.");
                return false;
            }
        }
        try {
            for (Iterator<Artifact> iterator = copiedArtifacts.iterator(); iterator.hasNext(); ) {
                Artifact alreadyCopiedArtifact = iterator.next();
                if (artifactsAreMatchingTypes(alreadyCopiedArtifact, artifactToCopy)) {
                    if (isArtifactIsOlderThanArtifact(alreadyCopiedArtifact, artifactToCopy)) {
                        Path artifactToDelete = Paths.get(destinationDirectory.toString(), alreadyCopiedArtifact.getFile().getName());
                        getLog().debug(String.format("Deleting file:'%s'", artifactToDelete));
                        // We delete the old artifact and remove it from the list of copied artifacts, the new artifact will be copied below
                        Files.deleteIfExists(artifactToDelete);
                        iterator.remove();
                        break;
                    } else {
                        return false;
                    }
                }
            }
            Path desiredArtifact = Paths.get(destinationDirectory.toString(), artifactToCopy.getFile().getName());
            if (!desiredArtifact.toFile().exists()) {
                getLog().debug(String.format("Copying: %s to %s", desiredArtifact, destinationDirectory.toString()));
                Files.copy(Paths.get(artifactToCopy.getFile().getAbsolutePath()), desiredArtifact);
            }
        } catch (IOException | InvalidVersionSpecificationException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        copiedArtifacts.add(artifactToCopy);

        return true;
    }

    /**
     * Extract the configuration settings (not properties files) from the configuration artifact
     * and load them into the /bin directory
     *
     * @param artifact Configuration artifact
     * @throws MojoExecutionException MojoExecutionException
     */
    private void extractConfigSettings(Artifact artifact) throws MojoExecutionException {// NOSONAR
        try (JarFile configSettings = new JarFile(artifact.getFile())) {
            Enumeration<JarEntry> entries = configSettings.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarFileEntry = entries.nextElement();
                // Only interested in files in the /bin directory that are not properties files
                if (!jarFileEntry.isDirectory() && jarFileEntry.getName().startsWith("bin") && !jarFileEntry.getName().endsWith(".properties")) {
                    //FIXME add a test to check directory creation with multiple child directories
                    Files.createDirectories(jmeterDirectoryPath.resolve(new File(jarFileEntry.getName()).getParentFile().getPath()));
                    final Path zipEntryPath = jmeterDirectoryPath.resolve(jarFileEntry.getName());
                    if (!zipEntryPath.normalize().startsWith(jmeterDirectoryPath.normalize())) {
                        throw new RuntimeException("Bad zip entry");
                    }
                    Files.copy(configSettings.getInputStream(jarFileEntry), zipEntryPath);
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
