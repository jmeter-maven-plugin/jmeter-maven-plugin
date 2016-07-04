package com.lazerycode.jmeter.mojo;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class RunJMeterMojoTest {

	private final URL sourceDirectoryFile = this.getClass().getResource("/testFiles");
	private final String systemTempDirectory = System.getProperty("java.io.tmpdir");

	@Test
	public void testCopyFilesInTestDirectory() throws Exception {
		File sourceDirectory = new File(sourceDirectoryFile.toURI());
		File destinationDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_destination_").toFile();
		destinationDirectory.deleteOnExit();
		String[] expectedResult = new String[]{"one_fake.jmx", "two_fake2.jmx", "three_fake.jmx", "one_four_fake4.jmx"};

		assertThat(destinationDirectory.list().length, is(equalTo(0)));

		RunJMeterMojo.CopyFilesInTestDirectory(sourceDirectory, destinationDirectory);

		assertThat(destinationDirectory.list().length, is(equalTo(4)));
		assertThat(destinationDirectory.list(), arrayContainingInAnyOrder(expectedResult));
	}
}
