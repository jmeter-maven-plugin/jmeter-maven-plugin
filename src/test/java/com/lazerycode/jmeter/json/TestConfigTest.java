package com.lazerycode.jmeter.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TestConfigTest {

    private String testConfigFile = "/config.json";
    private String tempdir = System.getProperty("java.io.tmpdir");

    @Test
    public void createConfigFromResourceFile() throws MojoExecutionException, URISyntaxException, JsonProcessingException {
        URL configFile = this.getClass().getResource(testConfigFile);
        File testConfigJSON = new File(configFile.toURI());
        TestConfig testConfig = new TestConfig(testConfigJSON, "test-execution");
        assertThat(testConfig.getFullConfig())
                .isEqualTo("{\"executionID\":\"test-execution\",\"jmeterDirectoryPath\":null,\"resultsOutputIsCSVFormat\":false,\"resultFilesLocations\":[],\"generateReports\":false}");
    }

    @Test(expected = MojoExecutionException.class)
    public void testConfigFileDoesNotExist() throws MojoExecutionException {
        File testConfigJSON = new File("/does/not/exist");
        new TestConfig(testConfigJSON, "configuration");
    }

    @Test(expected = MojoExecutionException.class)
    public void testConfigResourceDoesNotExist() throws MojoExecutionException {
        File configFile = new File("/does/not.exist");
        new TestConfig(configFile, "configuration");
    }

    @Test
    public void changeCSVFormat() throws MojoExecutionException, URISyntaxException {
        File configFile = new File(this.getClass().getResource(testConfigFile).toURI());
        TestConfig testConfig = new TestConfig(configFile, "test-execution");

        assertThat(testConfig.getResultsOutputIsCSVFormat()).isFalse();

        testConfig.setResultsOutputIsCSVFormat(true);

        assertThat(testConfig.getResultsOutputIsCSVFormat()).isTrue();

        testConfig.setResultsOutputIsCSVFormat(false);

        assertThat(testConfig.getResultsOutputIsCSVFormat()).isFalse();
    }

    @Test
    public void changeResultsFileLocation() throws MojoExecutionException, URISyntaxException {
        File configFile = new File(this.getClass().getResource(testConfigFile).toURI());
        TestConfig testConfig = new TestConfig(configFile, "test-execution");

        assertThat(testConfig.getResultsFileLocations().size()).isEqualTo(0);

        List<String> resultFilenames = new ArrayList<>();
        resultFilenames.add(0, "c:\\windows\\temp");
        resultFilenames.add(1, "/usr/local/temp");

        testConfig.setResultsFileLocations(resultFilenames);

        assertThat(testConfig.getResultsFileLocations().size()).isEqualTo(2);
        assertThat(testConfig.getResultsFileLocations().get(0)).isEqualTo("c:\\windows\\temp");
        assertThat(testConfig.getResultsFileLocations().get(1)).isEqualTo("/usr/local/temp");
    }

    @Test
    public void changeGenerateReports() throws MojoExecutionException, URISyntaxException {
        File configFile = new File(this.getClass().getResource(testConfigFile).toURI());
        TestConfig testConfig = new TestConfig(configFile, "test-execution");

        assertThat(testConfig.getGenerateReports()).isFalse();

        testConfig.setGenerateReports(true);

        assertThat(testConfig.getGenerateReports()).isTrue();

        testConfig.setGenerateReports(false);

        assertThat(testConfig.getGenerateReports()).isFalse();
    }

    @Test
    public void checkThatAWrittenFileCanBeReadInAgain() throws MojoExecutionException, URISyntaxException, IOException {
        String tempFileLocation = tempdir + File.separator + UUID.randomUUID() + File.separator + "test_config.json";
        File tempTestFile = new File(tempFileLocation);
        tempTestFile.getParentFile().mkdirs();
        tempTestFile.deleteOnExit();

        File configFile = new File(this.getClass().getResource(testConfigFile).toURI());
        TestConfig testConfig = new TestConfig(configFile, "test-execution");
        testConfig.setResultsOutputIsCSVFormat(true);
        testConfig.writeResultFilesConfigTo(tempFileLocation);

        //TODO make sure we don't overwrite original file data
        TestConfig newlyCreatedTestConfig = new TestConfig(tempTestFile, "test-execution");

        assertThat(testConfig).isEqualTo(newlyCreatedTestConfig);
        assertThat(testConfig.hashCode()).isEqualTo(newlyCreatedTestConfig.hashCode());
    }

    @Test(expected = MojoExecutionException.class)
    public void checkExceptionIsThrownIfFileCannotBeCreated() throws MojoExecutionException, URISyntaxException {
        String tempFileLocation = tempdir + File.separator + UUID.randomUUID() + File.separator + "test_config.json";
        File tempTestFile = new File(tempFileLocation);
        tempTestFile.deleteOnExit();

        File configFile = new File(this.getClass().getResource(testConfigFile).toURI());
        TestConfig testConfig = new TestConfig(configFile, "test-execution");
        testConfig.setResultsOutputIsCSVFormat(true);
        testConfig.writeResultFilesConfigTo(tempFileLocation);
    }

    @Test
    public void checkEqualsWorksForIdenticalObject() throws MojoExecutionException, URISyntaxException {
        File configFile = new File(this.getClass().getResource(testConfigFile).toURI());
        TestConfig testConfig = new TestConfig(configFile, "test-execution");

        assertThat(testConfig).isEqualTo(testConfig);
    }

    @Test
    public void checkEqualsWorksForNull() throws MojoExecutionException, URISyntaxException {
        File configFile = new File(this.getClass().getResource(testConfigFile).toURI());
        TestConfig testConfig = new TestConfig(configFile, "test-execution");

        assertThat(testConfig).isNotNull();
    }

    @Test
    public void checkEqualsWorksForDifferentClassType() throws MojoExecutionException, URISyntaxException {
        File configFile = new File(this.getClass().getResource(testConfigFile).toURI());
        TestConfig testConfig = new TestConfig(configFile, "test-execution");
        String notTestConfig = "nope";

        assertThat(testConfig).isNotEqualTo(notTestConfig);
    }
}
