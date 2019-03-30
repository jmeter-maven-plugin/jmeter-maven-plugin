package com.lazerycode.jmeter.configuration;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.lazerycode.jmeter.utility.UtilityFunctions;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JMeterArgumentsArrayTest {

    private static final String TIMESTAMP = String.valueOf(LocalDateTime.now().getYear());
    private static final String DEFAULT_TIMESTAMP = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    private static final boolean DISABLE_GUI = true;
    private static final boolean ENABLE_GUI = false;
    private final URL testFileUrl = this.getClass().getResource("/tests/test.jmx");
    private final URL testFileTwoUrl = this.getClass().getResource("/tests/subdir/test.jmx");
    private final URL testFileDirectoryUrl = this.getClass().getResource("/tests");
    private String testFilePath;
    private File testFile;
    private File testFileDirectory;
    private int serverPort = 8080;
    private String logsDirectory = "/var/logs";

    @Before
    public void setTestFileAbsolutePath() throws URISyntaxException {
        testFile = new File(this.testFileUrl.toURI());
        testFileDirectory = new File(this.testFileDirectoryUrl.toURI());
        testFilePath = new File(this.testFileUrl.toURI()).getAbsolutePath();
    }

    @Test(expected = MojoExecutionException.class)
    public void noTestSpecified() throws MojoExecutionException {
        new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .buildArgumentsArray();
    }

    @Test(expected = MojoExecutionException.class)
    public void jMeterHomeEmpty() throws Exception {
        new JMeterArgumentsArray(DISABLE_GUI, "")
                .setTestFile(testFile, testFileDirectory)
                .buildArgumentsArray();
    }

    @Test(expected = MojoExecutionException.class)
    public void jMeterHomeNull() throws Exception {
        new JMeterArgumentsArray(DISABLE_GUI, null)
                .setTestFile(testFile, testFileDirectory)
                .buildArgumentsArray();
    }

    @Test(expected = MojoExecutionException.class)
    public void nullTestFileIsNotAddedToArguments() throws Exception {
        new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(null, testFileDirectory)
                .buildArgumentsArray();
    }

    @Test
    public void validateDefaultCommandLineOutputWithGUIDisabled() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }

    @Test
    public void validateDefaultCommandLineOutputWithGUIEnabled() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(ENABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -t " + testFilePath);
    }

    @Test
    public void validateJMeterCustomPropertiesFile() throws Exception {
        List<File> testPropFiles = new ArrayList<>();
        testPropFiles.add(new File("test.properties"));
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .addACustomPropertiesFiles(testPropFiles);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -q " + testPropFiles.get(0).getAbsolutePath() + " -t " + testFilePath);
    }

    @Test
    public void emptyCustomPropertiesFileIsNotAddedToArguments() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .addACustomPropertiesFiles(null);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }

    @Test
    public void validateJMeterCustomPropertiesFiles() throws Exception {
        List<File> testPropFiles = new ArrayList<>();
        testPropFiles.add(new File("test.properties"));
        testPropFiles.add(new File("secondTest.properties"));
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .addACustomPropertiesFiles(testPropFiles);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -q " + testPropFiles.get(0).getAbsolutePath() + " -q " + testPropFiles.get(1).getAbsolutePath() + " -t " + testFilePath);
    }

    @Test
    public void validateSetRootLogLevel() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setLogRootOverride("DEBUG");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -L DEBUG");
    }

    @Test
    public void validateSetRootLogLevelWithWrongCase() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setLogRootOverride("info");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -L INFO");
    }

    @Test
    public void passingAEmptyRootLogLevelDoesNotSetAnything() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setLogRootOverride("");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }

    @Test
    public void passingANullRootLogLevelDoesNotSetAnything() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setLogRootOverride(null);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }

    @Test
    public void validateSettingAnInvalidLogLevelLogsToWarnAndDoesNotSetAnything() throws Exception {
        String randomLogLevel = "MADE_UP";
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        Logger argumentsArrayLogger = (Logger) LoggerFactory.getLogger(JMeterArgumentsArray.class);
        argumentsArrayLogger.addAppender(listAppender);

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setLogRootOverride(randomLogLevel);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
        assertThat(listAppender.list.size()).isEqualTo(1);
        assertThat(listAppender.list.get(0).toString()).isEqualTo(String.format("[WARN] Unknown log level %s", randomLogLevel));
    }

    @Test
    public void nullProxyConfigDoesNotAddProxyArguments() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setProxyConfig(null);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }

    @Test
    public void validateJMeterSetProxyHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPort(8080);
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -H http://10.10.50.43 -P 8080");
        assertThat(proxyConfiguration.toString())
                .isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\n\n");
    }

    @Test
    public void validateJMeterSetProxyUsername() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPort(8080);
        proxyConfiguration.setUsername("god");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -u god -H http://10.10.50.43 -P 8080");
        assertThat(proxyConfiguration.toString())
                .isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\nUsername: god\n\n");
    }

    @Test
    public void validateProxyUsernameNotSetIfNoHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setUsername("god");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
        assertThat(proxyConfiguration.toString())
                .isEqualTo("Proxy server is not being used.\n");
    }

    @Test
    public void validateJMeterSetProxyPassword() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPort(8080);
        proxyConfiguration.setPassword("changeme");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-a changeme -d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -H http://10.10.50.43 -P 8080");
        assertThat(proxyConfiguration.toString())
                .isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\nPassword: changeme\n\n");
    }

    @Test
    public void validateProxyPasswordNotSetIfNoHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setPassword("changeme");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
        assertThat(proxyConfiguration.toString())
                .isEqualTo("Proxy server is not being used.\n");
    }

    @Test
    public void validateSetNonProxyHosts() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPort(8080);
        proxyConfiguration.setHostExclusions("localhost|*.lazerycode.com");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -H http://10.10.50.43 -N localhost|*.lazerycode.com -P 8080");
        assertThat(proxyConfiguration.toString())
                .isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\nHost Exclusions: localhost|*.lazerycode.com\n\n");
    }

    @Test
    public void validateProxyNonProxyHostsNotSetIfNoHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHostExclusions("localhost|*.lazerycode.com");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
        assertThat(proxyConfiguration.toString()).isEqualTo("Proxy server is not being used.\n");
    }

    @Test
    public void checkProxyDetailsReturnedWhenHostAndPortNotSet() {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setUsername("god");
        proxyConfiguration.setPassword("changeme");
        proxyConfiguration.setHostExclusions("localhost|*.lazerycode.com");

        assertThat(proxyConfiguration.toString()).isEqualTo("Proxy server is not being used.\n");
    }

    @Test
    public void validateSetRemoteStop() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setRemoteStop();

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -X");
    }

    @Test
    public void validateSetRemoteStartAll() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setRemoteStart();

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -r -t " + testFilePath);
    }

    @Test
    public void emptyServerListDoesNotAddServerListArguments() throws MojoExecutionException {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setRemoteStartServerList(" ");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }

    @Test
    public void validateSetRemoteStartServerList() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setRemoteStartServerList("server1, server2");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -R server1, server2");
    }

    @Test
    public void validateTestFileTimestampDisabled() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/");
        testArgs.setResultsDirectory(File.separator + "tmp");
        testArgs.setResultsTimestamp(false);
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + File.separator + "tmp" + File.separator + "test.jtl" + " -n -t " + testFilePath);
    }

    @Test
    public void validateTestFileTimestampEnabledAndPrepended() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory(File.separator + "tmp")
                .setResultsTimestamp(true)
                .setResultsFileNameDateFormat("YYYY")
                .setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + File.separator + "tmp" + File.separator + TIMESTAMP + "-test.jtl" + " -n -t " + testFilePath);
    }

    @Test
    public void validateTestFileTimestampEnabledAndAppended() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory(File.separator + "tmp")
                .setResultsTimestamp(true)
                .appendTimestamp(true)
                .setResultsFileNameDateFormat("YYYY")
                .setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + File.separator + "tmp" + File.separator + "test-" + TIMESTAMP + ".jtl" + " -n -t " + testFilePath);
    }

    @Test
    public void fileNameDateFormatNotChangedIfEmptyStringPassedIn() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory(File.separator + "tmp")
                .setResultsTimestamp(true)
                .appendTimestamp(true)
                .setResultsFileNameDateFormat("")
                .setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + File.separator + "tmp" + File.separator + "test-" + DEFAULT_TIMESTAMP + ".jtl" + " -n -t " + testFilePath);
    }

    @Test
    public void fileNameDateFormatNotChangedIfNullPassedIn() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory(File.separator + "tmp")
                .setResultsTimestamp(true)
                .appendTimestamp(true)
                .setResultsFileNameDateFormat(null)
                .setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + File.separator + "tmp" + File.separator + "test-" + DEFAULT_TIMESTAMP + ".jtl" + " -n -t " + testFilePath);
    }

    @Test
    public void fileNameDateFormatNotChangedIfInvalidFormatPassedIn() throws Exception {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        Logger argumentsArrayLogger = (Logger) LoggerFactory.getLogger(JMeterArgumentsArray.class);
        argumentsArrayLogger.addAppender(listAppender);
        String badDateFormat = "ROGER";

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory(File.separator + "tmp")
                .setResultsTimestamp(true)
                .appendTimestamp(true)
                .setResultsFileNameDateFormat(badDateFormat)
                .setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + File.separator + "tmp" + File.separator + "test-" + DEFAULT_TIMESTAMP + ".jtl" + " -n -t " + testFilePath);
        assertThat(listAppender.list.size()).isEqualTo(1);
        assertThat(listAppender.list.get(0).toString()).isEqualTo(String.format("[ERROR] '%s' is an invalid DateTimeFormat.  Defaulting to Standard ISO_8601.", badDateFormat));
    }


    @Test
    public void resultsFileIsCSVFormat() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory(File.separator + "tmp")
                .setResultFileOutputFormatIsCSV(true)
                .setResultsTimestamp(true)
                .setResultsFileNameDateFormat("YYYY")
                .setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isEqualTo(File.separator + "tmp" + File.separator + TIMESTAMP + "-test.csv");
    }

    @Test
    public void resultsFileIsXMLFormat() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory(File.separator + "tmp")
                .setResultFileOutputFormatIsCSV(false)
                .setResultsTimestamp(true)
                .setResultsFileNameDateFormat("YYYY")
                .setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isEqualTo(File.separator + "tmp" + File.separator + TIMESTAMP + "-test.jtl");
    }

    @Test
    public void resultsFileDefaultsToXMLFormat() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory(File.separator + "tmp")
                .setResultsTimestamp(true)
                .setResultsFileNameDateFormat("YYYY")
                .setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName())
                .isEqualTo(File.separator + "tmp" + File.separator + TIMESTAMP + "-test.jtl");
    }

    @Test
    public void resultsFileHasCorrectName() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory("temp")
                .setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + "temp" + File.separator + "test.jtl" + " -n -t " + testFile.getAbsolutePath());
    }

    @Test
    public void resultsFileContainsDirectoryStructureInName() throws Exception {
        File testFileTwo = new File(this.testFileTwoUrl.toURI());

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setResultsDirectory("temp")
                .setTestFile(testFileTwo, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + "temp" + File.separator + "subdir_test.jtl" + " -n -t " + testFileTwo.getAbsolutePath());
    }

    @Test
    public void validateCommandLineWhenSettingLogsDirectory() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setLogsDirectory("/target/jmeter/logs")
                .setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -j /target/jmeter/logs" + File.separator + "test.jmx.log -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }


    @Test
    public void validateCommandLineWithReportGenerationEnabled() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(DISABLE_GUI, "target/jmeter/")
                .setTestFile(testFile, testFileDirectory)
                .setReportsDirectory("/target/jmeter/reports");

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -e -l " + testArgs.getResultsLogFileName() + " -n -o /target/jmeter/reports -t " + testFilePath);
    }

    @Test
    public void validateCommandLineWhenSettingServerModeWithJustPort() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(ENABLE_GUI, "target/jmeter/")
                .setLogsDirectory(logsDirectory)
                .setServerMode(null, serverPort);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -s -j " + logsDirectory + File.separator + "localhost_" + serverPort + ".log");
    }

    @Test
    public void validateCommandLineWhenSettingServerModeWithHostNameAndPort() throws Exception {
        String hostname = "jmeter.example.com";

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(ENABLE_GUI, "target/jmeter/")
                .setLogsDirectory(logsDirectory)
                .setServerMode(hostname, serverPort);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -s -j " + logsDirectory + File.separator + hostname + "_" + serverPort + ".log");
    }

    @Test
    public void validateCommandLineWhenSettingServerModeWithHostIPAndPort() throws Exception {
        String hostname = "10.10.10.4";

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(ENABLE_GUI, "target/jmeter/")
                .setLogsDirectory(logsDirectory)
                .setServerMode(hostname, serverPort);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -s -j " + logsDirectory + File.separator + hostname + "_" + serverPort + ".log");
    }

    @Test
    public void validateCommandLineWhenSettingServerModeWithHostIPAndPortWithoutLogging() throws Exception {
        String hostname = "10.10.10.4";

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(ENABLE_GUI, "target/jmeter/")
                .setServerMode(hostname, serverPort);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray())).isEqualTo("-d target/jmeter/ -s");
    }
}
