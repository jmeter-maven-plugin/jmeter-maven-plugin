package com.lazerycode.jmeter.mojo;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class RunJMeterMojoTest {

    private final URL sourceDirectoryFile = this.getClass().getResource("/testFiles");
    private final String systemTempDirectory = System.getProperty("java.io.tmpdir");

    @Test
    public void testCopyFilesInTestDirectory() throws Exception {
        File sourceDirectory = new File(sourceDirectoryFile.toURI());
        File destinationDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_destination_").toFile();
        destinationDirectory.deleteOnExit();
        String[] expectedResult = new String[]{"one", "two", "three"};

        assertThat(destinationDirectory.list().length).isEqualTo(0);

        RunJMeterMojo.copyFilesInTestDirectory(sourceDirectory, destinationDirectory);

        assertThat(destinationDirectory.list().length).isEqualTo(3);
        assertThat(destinationDirectory.list()).containsExactlyInAnyOrder(expectedResult);
        assertThat(new File(destinationDirectory, "one").list().length).isEqualTo(2);
        assertThat(new File(destinationDirectory, "one").list()).containsExactlyInAnyOrder("four", "fake.jmx");
        assertThat(new File(destinationDirectory, "two").list().length).isEqualTo(1);
        assertThat(new File(destinationDirectory, "two").list()).containsExactlyInAnyOrder("fake2.jmx");
        assertThat(new File(destinationDirectory, "three").list().length).isEqualTo(1);
        assertThat(new File(destinationDirectory, "three").list()).containsExactlyInAnyOrder("fake.jmx");
        assertThat(new File(destinationDirectory, "one/four").list().length).isEqualTo(1);
        assertThat(new File(destinationDirectory, "one/four").list()).containsExactlyInAnyOrder("fake4.jmx");
    }
}
