package com.lazerycode.jmeter.mojo;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class RunJMeterMojoTest {

	private final URL sourceDirectoryFile = this.getClass().getResource("/testFiles");
	private final String systemTempDirectory = System.getProperty("java.io.tmpdir");

	@Test
	public void testCopyFilesInTestDirectory() throws Exception {
		File sourceDirectory = new File(sourceDirectoryFile.toURI());
		File destinationDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_destination_").toFile();
		destinationDirectory.deleteOnExit();
		String[] expectedResult = {"one/fake.jmx", "two/fake2.jmx", "three/fake.jmx", "one/four/fake4.jmx"};

		assertThat(destinationDirectory.list(), arrayWithSize(0));

		RunJMeterMojo.CopyFilesInTestDirectory(sourceDirectory, destinationDirectory);
		assertThat(listRelativeFilePaths(destinationDirectory), containsInAnyOrder(expectedResult));
	}

	private static List<String> listRelativeFilePaths(File directory) throws Exception {
		final Path destinationPath = Paths.get(directory.getAbsolutePath());
		final List<String> walkedPaths = new ArrayList<>();
		Files.walkFileTree(destinationPath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				walkedPaths.add(destinationPath.relativize(file).toString());
				return FileVisitResult.CONTINUE;
			}
		});
		return walkedPaths;
	}
}
