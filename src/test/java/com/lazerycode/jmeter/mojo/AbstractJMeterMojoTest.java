package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.ProxyConfiguration;
import com.lazerycode.jmeter.utility.UtilityFunctions;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.Generated;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Generated(value = "org.junit-tools-1.0.2")
public class AbstractJMeterMojoTest {

    private static final String HOST = "host";
    private static final int PORT = 0;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NON_HOST = "h1,h2";
    private final URL sourceDirectoryFile = this.getClass().getResource("/testFiles");
    private final String systemTempDirectory = System.getProperty("java.io.tmpdir");
    private TestLogger TEST_LOGGER;

    @Before
    public void setup() {
        TEST_LOGGER = new TestLogger();
    }

    private AbstractJMeterMojo createtMojoInstanceWithTestLogging() {
        return new AbstractJMeterMojo() {
            @Override
            public void doExecute() throws MojoExecutionException, MojoFailureException {
            }

            @Override
            public Log getLog() {
                return TEST_LOGGER;
            }
        };
    }

    private Proxy createTestProxy() {
        Proxy testProxy = new Proxy();
        testProxy.setHost(HOST);
        testProxy.setPort(PORT);
        testProxy.setUsername(USERNAME);
        testProxy.setPassword(PASSWORD);
        testProxy.setNonProxyHosts(NON_HOST);

        return testProxy;
    }

    @Test
    public void testMavenProxy() throws Exception {
        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        Proxy testProxy = createTestProxy();
        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settings.getActiveProxy()).thenReturn(testProxy);

        testSubject.settings = settings;
        testSubject.useMavenProxy = true;

        testSubject.execute();

        assertThat(testSubject.proxyConfig).isNotNull();
        assertThat(testSubject.proxyConfig.getHost()).isEqualTo(HOST);
        assertThat(testSubject.proxyConfig.getPort()).isEqualTo(Integer.toString(PORT));
        assertThat(testSubject.proxyConfig.getUsername()).isEqualTo(USERNAME);
        assertThat(testSubject.proxyConfig.getPassword()).isEqualTo(PASSWORD);
        assertThat(testSubject.proxyConfig.getHostExclusions()).isEqualTo(NON_HOST);
        assertThat(TEST_LOGGER.getInfoContentContainer()).containsOnlyOnce(String.format("Maven proxy loaded successfully"));

    }

    @Test
    public void testMavenProxyWithNullSettings() throws Exception {
        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        Proxy testProxy = createTestProxy();
        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settings.getActiveProxy()).thenReturn(null);

        testSubject.settings = settings;
        testSubject.useMavenProxy = true;

        testSubject.execute();

        assertThat(TEST_LOGGER.getWarnContentContainer()).containsOnlyOnce(String.format("No maven proxy found, however useMavenProxy is set to true!"));
    }

    @Test
    public void ifSettingsAreNullProxyConfigIsNotSet() throws Exception {
        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        Proxy testProxy = createTestProxy();
        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settings.getActiveProxy()).thenReturn(testProxy);

        testSubject.settings = null;
        testSubject.useMavenProxy = true;

        testSubject.execute();

        assertThat(testSubject.proxyConfig).isNull();
    }

    @Test
    public void testSpecificProxyPriority() throws Exception {
        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        Proxy testProxy = createTestProxy();
        ProxyConfiguration testProxyConfig = new ProxyConfiguration();
        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settings.getActiveProxy()).thenReturn(testProxy);

        testSubject.settings = settings;
        testSubject.useMavenProxy = true;
        testSubject.proxyConfig = testProxyConfig;

        testSubject.execute();

        assertThat(testSubject.proxyConfig).isNotNull();
        assertThat(testSubject.proxyConfig).isEqualTo(testProxyConfig);
    }

    @Test
    public void testNoMavenProxy() throws Exception {
        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        Proxy testProxy = createTestProxy();
        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settings.getActiveProxy()).thenReturn(testProxy);

        testSubject.settings = settings;
        testSubject.useMavenProxy = false;

        testSubject.execute();

        assertThat(testSubject.proxyConfig).isNull();
    }

    @Test
    public void testLoggingWithSkipTests() throws Exception {
        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        Proxy testProxy = createTestProxy();

        MavenSession session = Mockito.mock(MavenSession.class);
        Mockito.when(session.getGoals()).thenReturn(Arrays.asList("foo"));

        testSubject.session = session;
        testSubject.skipTests = true;
        testSubject.execute();

        assertThat(TEST_LOGGER.getInfoContentContainer()).containsOnlyOnce(String.format("Performance tests are skipped."));
    }

    @Test
    public void testLoggingWithSkipTestsAndJMeterGUIGoal() throws Exception {
        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        Proxy testProxy = createTestProxy();

        MavenSession session = Mockito.mock(MavenSession.class);
        MojoExecution mojoExecution = Mockito.mock(MojoExecution.class);
        Mockito.when(session.getGoals()).thenReturn(Arrays.asList("jmeter:gui"));
        Mockito.when(mojoExecution.getExecutionId()).thenReturn("foo");
        Mockito.when(mojoExecution.getLifecyclePhase()).thenReturn("bar");

        testSubject.session = session;
        testSubject.mojoExecution = mojoExecution;
        testSubject.skipTests = true;
        testSubject.execute();

        assertThat(TEST_LOGGER.getInfoContentContainer()).containsOnlyOnce(String.format("Performance tests are skipped."));
    }

    @Test
    public void testCopyFilesInTestDirectory() throws Exception {
        File sourceDirectory = new File(sourceDirectoryFile.toURI());
        File destinationDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_destination_").toFile();
        destinationDirectory.deleteOnExit();
        String[] expectedResult = new String[]{"one", "two", "three"};

        assertThat(Objects.requireNonNull(destinationDirectory.list()).length).isEqualTo(0);

        RunJMeterMojo.copyFilesInTestDirectory(sourceDirectory, destinationDirectory);

        assertThat(Objects.requireNonNull(destinationDirectory.list()).length).isEqualTo(3);
        assertThat(destinationDirectory.list()).containsExactlyInAnyOrder(expectedResult);
        assertThat(Objects.requireNonNull(new File(destinationDirectory, "one").list()).length).isEqualTo(2);
        assertThat(new File(destinationDirectory, "one").list()).containsExactlyInAnyOrder("four", "fake.jmx");
        assertThat(Objects.requireNonNull(new File(destinationDirectory, "two").list()).length).isEqualTo(1);
        assertThat(new File(destinationDirectory, "two").list()).containsExactlyInAnyOrder("fake2.jmx");
        assertThat(Objects.requireNonNull(new File(destinationDirectory, "three").list()).length).isEqualTo(1);
        assertThat(new File(destinationDirectory, "three").list()).containsExactlyInAnyOrder("fake.jmx");
        assertThat(Objects.requireNonNull(new File(destinationDirectory, "one/four").list()).length).isEqualTo(1);
        assertThat(new File(destinationDirectory, "one/four").list()).containsExactlyInAnyOrder("fake4.jmx");
    }

    @Test(expected = MojoExecutionException.class)
    public void MojoExecutionExceptionThrownIfError() throws Exception {
        File sourceDirectory = new File("/does/not/exist");
        File destinationDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_destination_").toFile();
        destinationDirectory.deleteOnExit();

        RunJMeterMojo.copyFilesInTestDirectory(sourceDirectory, destinationDirectory);
    }

    @Test
    public void computeJMeterArgumentsArrayReturnsExpectedArray() throws Exception {
        File resultsDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_results_destination_").toFile();
        resultsDirectory.deleteOnExit();
        File logsDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_logs_destination_").toFile();
        logsDirectory.deleteOnExit();
        File jmeterDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_jmeter_destination_").toFile();
        jmeterDirectory.deleteOnExit();
        File testFile = Files.createTempFile(Paths.get(systemTempDirectory), "test_file_", "").toFile();
        jmeterDirectory.deleteOnExit();
        File testFileDirectory = new File(systemTempDirectory);

        JMeterArgumentsArray expected = new JMeterArgumentsArray(true, jmeterDirectory.getAbsolutePath())
                .setLogsDirectory(logsDirectory.getAbsolutePath())
                .setResultsDirectory(resultsDirectory.getAbsolutePath())
                .setResultFileOutputFormatIsCSV(true)
                .setProxyConfig(new ProxyConfiguration())
                .setLogRootOverride(null)
                .addACustomPropertiesFiles(null)
                .setTestFile(testFile, testFileDirectory);

        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        testSubject.resultsDirectory = resultsDirectory;
        testSubject.logsDirectory = logsDirectory;
        testSubject.customPropertiesFiles = null;
        testSubject.generateReports = false;
        testSubject.testResultsTimestamp = false;
        JMeterArgumentsArray actual = testSubject.computeJMeterArgumentsArray(true, true, jmeterDirectory.getAbsolutePath())
                .setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(actual.buildArgumentsArray())).isEqualTo(UtilityFunctions.humanReadableCommandLineOutput(expected.buildArgumentsArray()));
    }

    @Test
    public void computeJMeterArgumentsArrayReturnsExpectedArrayWithGenerateReportsEnabled() throws Exception {
        File resultsDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_results_destination_").toFile();
        resultsDirectory.deleteOnExit();
        File logsDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_logs_destination_").toFile();
        logsDirectory.deleteOnExit();
        File jmeterDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_jmeter_destination_").toFile();
        jmeterDirectory.deleteOnExit();
        File reportsDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_reports_destination_").toFile();
        reportsDirectory.deleteOnExit();
        File testFile = Files.createTempFile(Paths.get(systemTempDirectory), "test_file_", "").toFile();
        jmeterDirectory.deleteOnExit();
        File testFileDirectory = new File(systemTempDirectory);

        JMeterArgumentsArray expected = new JMeterArgumentsArray(true, jmeterDirectory.getAbsolutePath())
                .setLogsDirectory(logsDirectory.getAbsolutePath())
                .setReportsDirectory(reportsDirectory.getAbsolutePath())
                .setResultsDirectory(resultsDirectory.getAbsolutePath())
                .setResultFileOutputFormatIsCSV(true)
                .setProxyConfig(new ProxyConfiguration())
                .setLogRootOverride(null)
                .addACustomPropertiesFiles(null)
                .setTestFile(testFile, testFileDirectory);

        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        testSubject.resultsDirectory = resultsDirectory;
        testSubject.logsDirectory = logsDirectory;
        testSubject.reportDirectory = reportsDirectory;
        testSubject.customPropertiesFiles = null;
        testSubject.generateReports = true;
        testSubject.testResultsTimestamp = false;
        JMeterArgumentsArray actual = testSubject.computeJMeterArgumentsArray(true, true, jmeterDirectory.getAbsolutePath())
                .setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(actual.buildArgumentsArray())).isEqualTo(UtilityFunctions.humanReadableCommandLineOutput(expected.buildArgumentsArray()));
    }

    @Test
    public void computeJMeterArgumentsArrayReturnsExpectedArrayWithTestResultsTimeStampEnabled() throws Exception {
        File resultsDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_results_destination_").toFile();
        resultsDirectory.deleteOnExit();
        File logsDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_logs_destination_").toFile();
        logsDirectory.deleteOnExit();
        File jmeterDirectory = Files.createTempDirectory(Paths.get(systemTempDirectory), "temp_jmeter_destination_").toFile();
        jmeterDirectory.deleteOnExit();
        File testFile = Files.createTempFile(Paths.get(systemTempDirectory), "test_file_", "").toFile();
        jmeterDirectory.deleteOnExit();
        File testFileDirectory = new File(systemTempDirectory);

        JMeterArgumentsArray expected = new JMeterArgumentsArray(true, jmeterDirectory.getAbsolutePath())
                .setLogsDirectory(logsDirectory.getAbsolutePath())
                .setResultsDirectory(resultsDirectory.getAbsolutePath())
                .setResultFileOutputFormatIsCSV(true)
                .setProxyConfig(new ProxyConfiguration())
                .setResultsTimestamp(true)
                .appendTimestamp(true)
                .setResultsFileNameDateFormat("YYYYMM")
                .setLogRootOverride(null)
                .addACustomPropertiesFiles(null)
                .setTestFile(testFile, testFileDirectory);

        AbstractJMeterMojo testSubject = createtMojoInstanceWithTestLogging();
        testSubject.resultsDirectory = resultsDirectory;
        testSubject.logsDirectory = logsDirectory;
        testSubject.customPropertiesFiles = null;
        testSubject.generateReports = false;
        testSubject.testResultsTimestamp = true;
        testSubject.appendResultsTimestamp = true;
        testSubject.resultsFileNameDateFormat = "YYYYMM";
        JMeterArgumentsArray actual = testSubject.computeJMeterArgumentsArray(true, true, jmeterDirectory.getAbsolutePath())
                .setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(actual.buildArgumentsArray())).isEqualTo(UtilityFunctions.humanReadableCommandLineOutput(expected.buildArgumentsArray()));
    }
}
