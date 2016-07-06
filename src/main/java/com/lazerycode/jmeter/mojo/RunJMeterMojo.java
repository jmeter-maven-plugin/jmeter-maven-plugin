package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.exceptions.IOException;
import com.lazerycode.jmeter.testrunner.TestManager;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

/**
 * JMeter Maven plugin.
 */
@Mojo(name = "jmeter", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
@Execute(goal = "configure")
public class RunJMeterMojo extends AbstractJMeterMojo {

	/**
	 * Run all the JMeter tests.
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException {
		getLog().info(" ");
		getLog().info("-------------------------------------------------------");
		getLog().info(" P E R F O R M A N C E    T E S T S");
		getLog().info("-------------------------------------------------------");

		if (!testFilesDirectory.exists()) {
			getLog().info("<testFilesDirectory>" + testFilesDirectory.getAbsolutePath() + "</testFilesDirectory> does not exist...");
			getLog().info("Performance tests are skipped.");
			return;
		}

		initialiseJMeterArgumentsArray(true);
		if (null != remoteConfig) {
			remoteConfig.setPropertiesMap(propertiesMap);
		}

		CopyFilesInTestDirectory(testFilesDirectory, testFilesBuildDirectory);

		TestManager jMeterTestManager = new TestManager(testArgs, testFilesBuildDirectory, testFilesIncluded, testFilesExcluded, remoteConfig, suppressJMeterOutput, workingDirectory, jMeterProcessJVMSettings, runtimeJarName);
		jMeterTestManager.setPostTestPauseInSeconds(postTestPauseInSeconds);
		getLog().info(" ");
		if (proxyConfig != null) {
			getLog().info(this.proxyConfig.toString());
		}
		resultFilesLocations = jMeterTestManager.executeTests();
	}

	static void CopyFilesInTestDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
		final Path sourcePath = Paths.get(sourceDirectory.getAbsolutePath());
		final Path destinationPath = Paths.get(destinationDirectory.getAbsolutePath());
		try {
			Files.walkFileTree(sourcePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws java.io.IOException {
					FileUtils.copyFile(file.toFile(), new File(destinationPath.toFile(), sourcePath.relativize(file).toString().replace(File.separator, "_")));
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (java.io.IOException e) {
			throw new IOException(e.getMessage(), e);
		}

	}
}