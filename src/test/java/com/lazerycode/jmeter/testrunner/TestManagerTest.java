package com.lazerycode.jmeter.testrunner;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;
import com.lazerycode.jmeter.configuration.RemoteConfiguration;
import org.apache.maven.plugin.MojoExecutionException;
import org.assertj.core.api.ListAssert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestManagerTest {

    private TestManager testManager;
    private final String tempDir = System.getProperty("java.io.tmpdir");

    @Before
    public void setup() {
        testManager = new TestManager();
    }

    @Test
    public void checkSetBinDir() {
        File binDirectory = new File(tempDir);
        testManager.setBinDir(binDirectory);

        assertThat(testManager.getBinDir()).isEqualTo(binDirectory);
    }

    @Test
    public void checkSetBaseTestArguments() throws MojoExecutionException {
        JMeterArgumentsArray jMeterArgumentsArray = new JMeterArgumentsArray(true, "/some/directory");
        testManager.setBaseTestArgs(jMeterArgumentsArray);

        assertThat(testManager.getBaseTestArgs()).isEqualTo(jMeterArgumentsArray);
    }

    @Test
    public void checkTestFilesDirectory() {
        File testFilesDirectory = new File(tempDir);
        testManager.setTestFilesDirectory(testFilesDirectory);

        assertThat(testManager.getTestFilesDirectory()).isEqualTo(testFilesDirectory);
    }

    @Test
    public void checkRemoteServerConfiguration() {
        RemoteConfiguration remoteConfiguration = new RemoteConfiguration();
        testManager.setRemoteServerConfiguration(remoteConfiguration);

        assertThat(testManager.getRemoteServerConfiguration()).isEqualTo(remoteConfiguration);
    }

    @Test
    public void checkSuppressJMeterOutput() {
        testManager.setSuppressJMeterOutput(true);

        assertThat(testManager.isSuppressJMeterOutput()).isTrue();
    }

    @Test
    public void checkJMeterProcessJVMSettings() {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();
        testManager.setJMeterProcessJVMSettings(jMeterProcessJVMSettings);

        assertThat(testManager.getJMeterProcessJVMSettings()).isEqualTo(jMeterProcessJVMSettings);
    }

    @Test
    public void checkRuntimeJarName() {
        testManager.setRuntimeJarName("fred");

        assertThat(testManager.getRuntimeJarName()).isEqualTo("fred");
    }

    @Test
    public void checkReportDirectory() {
        File reportDirectory = new File(tempDir);
        testManager.setReportDirectory(reportDirectory);

        assertThat(testManager.getReportDirectory()).isEqualTo(reportDirectory);
    }

    @Test
    public void checkGenerateReports() {
        testManager.setGenerateReports(true);

        assertThat(testManager.isGenerateReports()).isTrue();
    }

    @Test
    public void checkTestFilesExcluded() {
        List<String> testFilesExcluded = new ArrayList<>();
        testFilesExcluded.add("excluded.jmx");
        String[] expected = testFilesExcluded.toArray(new String[0]);
        testManager.setTestFilesExcluded(testFilesExcluded);

        assertThat(testManager.getTestFilesExcluded()).isEqualTo(expected);
    }

    @Test
    public void checkTestFilesExcludedCanSetEmptyList() {
        List<String> testFilesExcluded = new ArrayList<>();
        testFilesExcluded.add("excluded.jmx");
        String[] expected = testFilesExcluded.toArray(new String[0]);
        testManager.setTestFilesExcluded(testFilesExcluded);

        assertThat(testManager.getTestFilesExcluded()).isEqualTo(expected);

        testManager.setTestFilesExcluded(new ArrayList<>());

        assertThat(testManager.getTestFilesExcluded()).isEqualTo(new String[0]);
    }

    @Test
    public void checkTestFilesExcludedDefaultsToEmptyArray() {
        assertThat(testManager.getTestFilesExcluded().length).isEqualTo(0);
    }

    @Test
    public void checkTestFilesIncluded() {
        List<String> testFilesIncluded = new ArrayList<>();
        testFilesIncluded.add("included.jmx");
        String[] expected = testFilesIncluded.toArray(new String[0]);
        testManager.setTestFilesIncluded(testFilesIncluded);

        assertThat(testManager.getTestFilesIncluded()).isEqualTo(expected);
    }

    @Test
    public void checkTestFilesIncludedDoesNotSetEmptyList() {
        List<String> testFilesIncluded = new ArrayList<>();
        testFilesIncluded.add("excluded.jmx");
        String[] expected = testFilesIncluded.toArray(new String[0]);
        testManager.setTestFilesIncluded(testFilesIncluded);

        assertThat(testManager.getTestFilesIncluded()).isEqualTo(expected);

        testManager.setTestFilesIncluded(new ArrayList<>());

        assertThat(testManager.getTestFilesIncluded()).isEqualTo(expected);
    }

    @Test
    public void checkTestFilesIncludedDefaultsToAllJMXFiles() {
        assertThat(testManager.getTestFilesIncluded()).isEqualTo(new String[]{"**/*.jmx"});
    }

    @Test
    public void checkPostTestPauseInSeconds() {
        testManager.setPostTestPauseInSeconds("10");

        assertThat(testManager.getPostTestPauseInSeconds()).isEqualTo(10L);
    }

    @Test
    public void checkPostTestPauseInSecondsIsNotChangedIfInvalidValueIsPassedIn() {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        Logger argumentsArrayLogger = (Logger) LoggerFactory.getLogger(TestManager.class);
        argumentsArrayLogger.addAppender(listAppender);

        testManager.setPostTestPauseInSeconds("10");

        assertThat(testManager.getPostTestPauseInSeconds()).isEqualTo(10L);

        testManager.setPostTestPauseInSeconds("Oranges");

        assertThat(testManager.getPostTestPauseInSeconds()).isEqualTo(10L);
        assertThat(listAppender.list.size()).isEqualTo(1);
        assertThat(listAppender.list.get(0).toString()).isEqualTo("[ERROR] Error parsing <postTestPauseInSeconds>Oranges</postTestPauseInSeconds> to Long, will default to 0L");
    }

    @Test
    public void checkPostTestPauseInSecondsDefault() {
        assertThat(testManager.getPostTestPauseInSeconds()).isEqualTo(0L);
    }

    @Test
    public void checkGenerateTestList() throws URISyntaxException {
        List<String> expected = new ArrayList<>();
        expected.add("one/fake.jmx");
        expected.add("one/four/fake4.jmx");
        expected.add("three/fake.jmx");
        expected.add("two/fake2.jmx");

        testManager.setTestFilesDirectory(new File(this.getClass().getResource("/testFiles").toURI()));
        List<String> actual = testManager.generateTestList();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void checkGenerateTestListWithExclusions() throws URISyntaxException {
        List<String> expected = new ArrayList<>();
        expected.add("one/four/fake4.jmx");
        expected.add("two/fake2.jmx");

        testManager.setTestFilesDirectory(new File(this.getClass().getResource("/testFiles").toURI()))
                .setTestFilesExcluded(Collections.singletonList("**/fake.jmx"));
        List<String> actual = testManager.generateTestList();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void checkEmptyListIsReturnedIfTestFilesDirectoryIsNotSet() {
        assertThat(testManager.generateTestList()).isEqualTo(Collections.emptyList());
    }
}
