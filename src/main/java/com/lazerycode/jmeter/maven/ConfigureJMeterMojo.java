package com.lazerycode.jmeter.maven;

import com.lazerycode.jmeter.configuration.MavenArtifact;
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
import org.eclipse.aether.artifact.Artifact;
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

	@Parameter(defaultValue = "3.0-SNAPSHOT")
	private String JMeterVersion;

	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
	private RepositorySystemSession repositorySystemSession;

	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
	private List<RemoteRepository> remoteRepositories;

	@Parameter(defaultValue = "${project.remotePluginRepositories}", readonly = true)
	private List<RemoteRepository> pluginRepositories;

	@Parameter
	private List<MavenArtifact> jmeterArtifacts = new ArrayList<>();

	/**
	 * Value class that wraps all remote configurations.
	 */
	@Parameter
	protected List<MavenArtifact> jmeterPlugins = new ArrayList<>();

	/**
	 * Value class that wraps all remote configurations.
	 */
	@Parameter
	protected List<MavenArtifact> junitLibraries = new ArrayList<>();

	/**
	 * All property files are stored in this artifact, it comes with JMeter library
	 */
	public static final String JMETER_CONFIG_ARTIFACT = "ApacheJMeter_config";
	private static final String GROUP_ID = "org.apache.jmeter";
	private List<RemoteRepository> repositoryList = new ArrayList<>();

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
		addRepositories(remoteRepositories);
		addRepositories(pluginRepositories);
		copyExplicitLibraries(jmeterPlugins, libExtDir);
		copyExplicitLibraries(junitLibraries, libJUnitDir);
	}

	private void addRepositories(List<RemoteRepository> listOfRepositoriesToAdd) {
		for (RemoteRepository repository : listOfRepositoriesToAdd) {
			if (!repositoryList.contains(repository)) {
				repositoryList.add(repository);
			}
		}
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
	 * If a specific list of JMeter artifacts has not been specified fall back to the default list of artifacts
	 * we would expect for the current version of JMeter supported by this plugin
	 */
	private void configureJMeterArtifacts() {
		if (jmeterArtifacts.size() == 0) {
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_components:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_config:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_core:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_ftp:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_functions:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_http:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_java:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_jdbc:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_jms:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_junit:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_ldap:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_mail:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_mongodb:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_monitors:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_native:" + JMeterVersion));
			jmeterArtifacts.add(new MavenArtifact(GROUP_ID + ":ApacheJMeter_tcp:" + JMeterVersion));
		}
	}

	private void populateJMeterDirectoryTree() throws DependencyResolutionException, IOException {
		if (jmeterArtifacts.size() == 0) {
			throw new DependencyResolutionException("No JMeter dependencies specified!");
		}
		for (MavenArtifact desiredArtifact : jmeterArtifacts) {
			ArtifactResult result = getArtifactResult(new DefaultArtifact(desiredArtifact.getDependency()));
			switch (result.getArtifact().getArtifactId()) {
				case JMETER_CONFIG_ARTIFACT:
					extractConfigSettings(result.getArtifact());
					break;
				case "ApacheJMeter":
					//TODO set the following for JMeterProcessBuilder: result.getArtifact().getFile().getName()
					copyArtifact(result.getArtifact(), binDir);
					copyTransitiveDependenciesToLibDirectory(result.getArtifact(), JavaScopes.COMPILE);
					copyTransitiveDependenciesToLibDirectory(result.getArtifact(), JavaScopes.RUNTIME);
					break;
				default:
					copyArtifact(result.getArtifact(), libExtDir);
					copyTransitiveDependenciesToLibDirectory(result.getArtifact(), JavaScopes.COMPILE);
					copyTransitiveDependenciesToLibDirectory(result.getArtifact(), JavaScopes.RUNTIME);
			}
		}
	}

	private void copyExplicitLibraries(List<MavenArtifact> desiredArtifacts, File destination) throws DependencyResolutionException, IOException {
		for (MavenArtifact desiredArtifact : desiredArtifacts) {
			ArtifactResult result = getArtifactResult(new DefaultArtifact(desiredArtifact.getDependency()));
			copyArtifact(result.getArtifact(), destination);
			copyTransitiveDependenciesToLibDirectory(result.getArtifact(), JavaScopes.COMPILE);
			copyTransitiveDependenciesToLibDirectory(result.getArtifact(), JavaScopes.RUNTIME);
		}
	}

	private ArtifactResult getArtifactResult(Artifact desiredArtifact) throws DependencyResolutionException {
		ArtifactRequest request = new ArtifactRequest();
		request.setArtifact(desiredArtifact);
		request.setRepositories(repositoryList);
		try {
			return repositorySystem.resolveArtifact(repositorySystemSession, request);
		} catch (ArtifactResolutionException e) {
			throw new DependencyResolutionException(e.getMessage(), e);
		}
	}

	private void copyTransitiveDependenciesToLibDirectory(Artifact artifact, String scope) throws DependencyResolutionException, IOException {
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(new Dependency(artifact, scope));
		collectRequest.setRepositories(repositoryList);
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

	private void copyArtifact(Artifact artifact, File destinationDirectory) throws IOException {
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
	 * Extract the configuration settings (not properties files) form the configuration artifact and load them into the /bin directory
	 *
	 * @param artifact Configuration artifact
	 * @throws IOException
	 */
	private void extractConfigSettings(Artifact artifact) throws IOException {
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
