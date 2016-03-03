package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.exceptions.DependencyResolutionException;
import com.lazerycode.jmeter.exceptions.IOException;
import com.lazerycode.jmeter.properties.PropertyHandler;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

@Mojo(name = "configure", defaultPhase = LifecyclePhase.COMPILE)
public class ConfigureJMeterMojo extends AbstractJMeterMojo {

	@Component
	private RepositorySystem repositorySystem;

	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
	private RepositorySystemSession repositorySystemSession;

	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
	private List<RemoteRepository> remoteRepositories;

	/**
	 * The version of JMeter that this plugin will use to run tests.
	 * We use a hard coded list of artifacts to configure JMeter locally,
	 * if you change this version number the list of artifacts required to run JMeter may change.
	 * If this happens you will need to override the &lt;jmeterArtifacts&gt; element.
	 */
	@Parameter(defaultValue = "3.0-SNAPSHOT")
	private String jmeterVersion;

	/**
	 * A list of artifacts that we use to configure JMeter.
	 * This list is hard coded by default, you can override this list and supply your own list of artifacts for JMeter.
	 * This would be useful if you want to use a different version of JMeter that has a different list of required artifacts.
	 * <p/>
	 * &lt;jmeterExtensions&gt;
	 * &nbsp;&nbsp;&lt;artifact&gt;kg.apc:jmeter-plugins:1.3.1&lt;/artifact&gt;
	 * &lt;jmeterExtensions&gt;
	 */
	@Parameter
	private List<String> jmeterArtifacts = new ArrayList<>();

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
	 * A list of artifacts that should be copied into the lib/junit directory e.g.
	 * <p/>
	 * &lt;junitLibraries&gt;
	 * &nbsp;&nbsp;&lt;artifact&gt;com.lazerycode.junit:junit-test:1.0.0&lt;/artifact&gt;
	 * &lt;junitLibraries&gt;
	 */
	@Parameter
	protected List<String> junitLibraries = new ArrayList<>();

	public static final String JMETER_CONFIG_ARTIFACT = "ApacheJMeter_config";
	private static final String JMETER_GROUP_ID = "org.apache.jmeter";

	/**
	 * Configure a local instance of JMeter
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException {
		getLog().info("-------------------------------------------------------");
		getLog().info(" Configuring JMeter...");
		getLog().info("-------------------------------------------------------");
		generateJMeterDirectoryTree();
		setJMeterResultFileFormat();
		configureAdvancedLogging();
		propertyConfiguration();
		configureJMeterArtifacts();
		populateJMeterDirectoryTree();
		copyExplicitLibraries(jmeterExtensions, libExtDir);
		copyExplicitLibraries(junitLibraries, libJUnitDir);
	}

	/**
	 * Generate the directory tree utilised by JMeter.
	 */
	protected void generateJMeterDirectoryTree() {
		logsDir = new File(workDir, "logs");
		logsDir.mkdirs();
		binDir = new File(workDir, "bin");
		binDir.mkdirs();
		if (null != resultsDirectory) {
			resultsDir = new File(resultsDirectory.replaceAll("\\|/", File.separator));
		} else {
			resultsDir = new File(workDir, "results");
		}
		resultsDir.mkdirs();
		libDir = new File(workDir, "lib");
		libExtDir = new File(libDir, "ext");
		libExtDir.mkdirs();
		libJUnitDir = new File(libDir, "junit");
		libJUnitDir.mkdirs();
	}

	protected void propertyConfiguration() throws MojoExecutionException {
		PropertyHandler pluginProperties = new PropertyHandler(propertiesFilesDirectory, binDir, propertiesReplacedByCustomFiles);
		pluginProperties.setJMeterProperties(propertiesJMeter);
		pluginProperties.setJMeterGlobalProperties(propertiesGlobal);
		pluginProperties.setJMeterSaveServiceProperties(propertiesSaveService);
		pluginProperties.setJMeterUpgradeProperties(propertiesUpgrade);
		pluginProperties.setJmeterUserProperties(propertiesUser);
		pluginProperties.setJMeterSystemProperties(propertiesSystem);
		pluginProperties.configureJMeterPropertiesFiles();
		pluginProperties.setDefaultPluginProperties(binDir.getAbsolutePath());
	}

	/**
	 * This sets the default list of artifacts that we use to set up a local instance of JMeter.
	 * We only use this default list if &lt;jmeterArtifacts&gt; has not been overridden in the POM.
	 */
	private void configureJMeterArtifacts() {
		if (jmeterArtifacts.size() == 0) {
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
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_monitors:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_native:" + jmeterVersion);
			jmeterArtifacts.add(JMETER_GROUP_ID + ":ApacheJMeter_tcp:" + jmeterVersion);
		}
	}

	private void populateJMeterDirectoryTree() throws DependencyResolutionException, IOException {
		if (jmeterArtifacts.size() == 0) {
			throw new DependencyResolutionException("No JMeter dependencies specified!");
		}
		for (String desiredArtifact : jmeterArtifacts) {
			ArtifactResult result = getArtifactResult(new DefaultArtifact(desiredArtifact));
			switch (result.getArtifact().getArtifactId()) {
				case JMETER_CONFIG_ARTIFACT:
					extractConfigSettings(result.getArtifact());
					break;
				case "ApacheJMeter":
					//TODO set the following for JMeterProcessBuilder: result.getArtifact().getFile().getName()
					copyArtifact(result.getArtifact(), binDir);
					copyTransitiveRuntimeDependenciesToLibDirectory(result.getArtifact());
					break;
				default:
					copyArtifact(result.getArtifact(), libExtDir);
					copyTransitiveRuntimeDependenciesToLibDirectory(result.getArtifact());
			}
		}
	}

	/**
	 * Copy a list of libraries to a specific folder.
	 *
	 * @param desiredArtifacts A list of artifacts
	 * @param destination      A destination folder to copy these artifacts to
	 * @throws DependencyResolutionException
	 * @throws IOException
	 */
	private void copyExplicitLibraries(List<String> desiredArtifacts, File destination) throws DependencyResolutionException, IOException {
		for (String desiredArtifact : desiredArtifacts) {
			ArtifactResult result = getArtifactResult(new DefaultArtifact(desiredArtifact));
			copyArtifact(result.getArtifact(), destination);
			copyTransitiveRuntimeDependenciesToLibDirectory(result.getArtifact());
		}
	}

	/**
	 * Find a specific artifact in a remote repository
	 *
	 * @param desiredArtifact The artifact that we want to find
	 * @return Will return an ArtifactResult object
	 * @throws DependencyResolutionException
	 */
	private ArtifactResult getArtifactResult(org.eclipse.aether.artifact.Artifact desiredArtifact) throws DependencyResolutionException {
		ArtifactRequest request = new ArtifactRequest();
		request.setArtifact(desiredArtifact);
		request.setRepositories(remoteRepositories);
		try {
			return repositorySystem.resolveArtifact(repositorySystemSession, request);
		} catch (ArtifactResolutionException e) {
			throw new DependencyResolutionException(e.getMessage(), e);
		}
	}

	/**
	 * Collate a list of transitive runtime dependencies that need to be copied to the /lib directory and then copy them there.
	 *
	 * @param artifact The artifact that is a transitive dependency
	 * @throws DependencyResolutionException
	 * @throws IOException
	 */
	private void copyTransitiveRuntimeDependenciesToLibDirectory(org.eclipse.aether.artifact.Artifact artifact) throws DependencyResolutionException, IOException {
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(new Dependency(artifact, JavaScopes.RUNTIME));
		collectRequest.setRepositories(remoteRepositories);
		DependencyFilter dependencyFilter = DependencyFilterUtils.classpathFilter();
		DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, dependencyFilter);

		try {
			List<DependencyNode> artifactDependencies = repositorySystem.resolveDependencies(repositorySystemSession, dependencyRequest).getRoot().getChildren();
			for (DependencyNode dependency : artifactDependencies) {
				ArtifactResult result = getArtifactResult(dependency.getArtifact());
				if (!result.getArtifact().getArtifactId().startsWith("ApacheJMeter_")) {
					copyArtifact(result.getArtifact(), libDir);
				}
			}
		} catch (org.eclipse.aether.resolution.DependencyResolutionException e) {
			throw new DependencyResolutionException(e.getMessage(), e);
		}
	}

	/**
	 * Copy an Artifact to a directory
	 *
	 * @param artifact             Artifact that needs to be copied.
	 * @param destinationDirectory Directory to copy the artifact to.
	 * @throws IOException
	 */
	private void copyArtifact(org.eclipse.aether.artifact.Artifact artifact, File destinationDirectory) throws IOException {
		if (getLog().isDebugEnabled()) {
			//TODO work out how to implement this in Aether
//			List<String> trail = artifact.getDependencyTrail();
//			for (int i = 0; i < trail.size(); i++) {
//				getLog().debug(StringUtils.leftPad("", i) + trail.get(i));
//			}
		}
		try {
			FileUtils.copyFileToDirectory(artifact.getFile(), destinationDirectory);
		} catch (java.io.IOException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Extract the configuration settings (not properties files) from the configuration artifact and load them into the /bin directory
	 *
	 * @param artifact Configuration artifact
	 * @throws IOException
	 */
	private void extractConfigSettings(org.eclipse.aether.artifact.Artifact artifact) throws IOException {
		try {
			JarFile configSettings = new JarFile(artifact.getFile());
			Enumeration<JarEntry> entries = configSettings.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarFileEntry = entries.nextElement();
				// Only interested in files in the /bin directory that are not properties files
				if (!jarFileEntry.isDirectory() && jarFileEntry.getName().startsWith("bin") && !jarFileEntry.getName().endsWith(".properties")) {
					File fileToCreate = new File(workDir.getCanonicalPath() + File.separator + jarFileEntry.getName());
					if (jarFileEntry.getName().endsWith(logConfigFilename) && fileToCreate.exists()) {
						break;
					}
					copyInputStreamToFile(configSettings.getInputStream(jarFileEntry), fileToCreate);
				}
			}
			configSettings.close();
		} catch (java.io.IOException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
}
