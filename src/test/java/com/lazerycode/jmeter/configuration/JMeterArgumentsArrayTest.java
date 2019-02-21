package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.utility.UtilityFunctions;
import org.apache.maven.plugin.MojoExecutionException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class JMeterArgumentsArrayTest {

    private final URL testFileURL = this.getClass().getResource("/tests/test.jmx");
    private final URL testFileTwoURL = this.getClass().getResource("/tests/subdir/test.jmx");
    private final URL testFileDirectoryURL = this.getClass().getResource("/tests");
    private final String timestamp = new DateTime().year().getAsText();
    private final boolean disableGUI = true;
    private final boolean enableGUI = false;
    private String testFilePath;
    private File testFile;
    private File testFileDirectory;
    private int serverPort = 8080;
    private String logsDirectory = "/var/logs";

    @Before
    public void setTestFileAbsolutePath() throws URISyntaxException {
        testFile = new File(this.testFileURL.toURI());
        testFileDirectory = new File(this.testFileDirectoryURL.toURI());
        testFilePath = new File(this.testFileURL.toURI()).getAbsolutePath();
    }

    @Test(expected = MojoExecutionException.class)
    public void noTestSpecified() throws MojoExecutionException {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.buildArgumentsArray();
    }

    @Test(expected = MojoExecutionException.class)
    public void jMeterHomeEmpty() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "");
        testArgs.setTestFile(testFile, testFileDirectory);
        testArgs.buildArgumentsArray();
    }

    @Test(expected = MojoExecutionException.class)
    public void jMeterHomeNull() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, null);
        testArgs.setTestFile(testFile, testFileDirectory);
        testArgs.buildArgumentsArray();
    }

    @Test
    public void validateDefaultCommandLineOutputWithGUIDisabled() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }

    @Test
    public void validateDefaultCommandLineOutputWithGUIEnabled() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(enableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -t " + testFilePath);
    }

    @Test
    public void validateJMeterCustomPropertiesFile() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        File testPropFile = new File("test.properties");
        testArgs.setACustomPropertiesFile(testPropFile);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -q " + testPropFile.getAbsolutePath() + " -t " + testFilePath);
    }

    @Test
    public void validateJMeterCustomPropertiesFiles() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        File testPropFile = new File("test.properties");
        File secondTestPropFile = new File("secondTest.properties");
        testArgs.setACustomPropertiesFile(testPropFile);
        testArgs.setACustomPropertiesFile(secondTestPropFile);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -q " + testPropFile.getAbsolutePath() + " -q " + secondTestPropFile.getAbsolutePath() + " -t " + testFilePath);
    }

    @Test
    public void validateSetRootLogLevel() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setLogRootOverride("DEBUG");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -L DEBUG");
    }

    @Test
    public void validateSetRootLogLevelWithWrongCase() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setLogRootOverride("info");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -L INFO");
    }

    @Test
    public void passingAEmptyRootLogLevelDoesNotSetAnything() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setLogRootOverride("");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }

    @Test
    public void passingANullRootLogLevelDoesNotSetAnything() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setLogRootOverride(null);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }

    @Test
    public void validateJMeterSetProxyHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPort(8080);
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setProxyConfig(proxyConfiguration);

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
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -u god -H http://10.10.50.43 -P 8080");
        assertThat(proxyConfiguration.toString())
                .isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\nUsername: god\n\n");
    }

    @Test
    public void validateProxyUsernameNotSetIfNoHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setUsername("god");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setProxyConfig(proxyConfiguration);

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
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");

        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-a changeme -d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -H http://10.10.50.43 -P 8080");
        assertThat(proxyConfiguration.toString())
                .isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\nPassword: changeme\n\n");
    }

    @Test
    public void validateProxyPasswordNotSetIfNoHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setPassword("changeme");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setProxyConfig(proxyConfiguration);

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
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);
        testArgs.setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -H http://10.10.50.43 -N localhost|*.lazerycode.com -P 8080");
        assertThat(proxyConfiguration.toString())
                .isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\nHost Exclusions: localhost|*.lazerycode.com\n\n");
    }

    @Test
    public void validateProxyNonProxyHostsNotSetIfNoHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHostExclusions("localhost|*.lazerycode.com");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);
        testArgs.setProxyConfig(proxyConfiguration);

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
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setRemoteStop();

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -X");
    }

    @Test
    public void validateSetRemoteStartAll() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setRemoteStart();

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -r -t " + testFilePath);
    }

    @Test
    public void validateSetRemoteStart() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);

        testArgs.setRemoteStartServerList("server1, server2");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath + " -R server1, server2");
    }

    @Test
    public void validateTestFileTimestampDisabled() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setResultsDirectory(File.separator + "tmp");
        testArgs.setResultsTimestamp(false);
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + File.separator + "tmp" + File.separator + "test.jtl" + " -n -t " + testFilePath);
    }

    @Test
    public void validateTestFileTimestampEnabledAndPrepended() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setResultsDirectory(File.separator + "tmp");
        testArgs.setResultsTimestamp(true);
        testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -l " + File.separator + "tmp" + File.separator + timestamp + "-test.jtl" + " -n -t " + testFilePath);
    }

    @Test
    public void validateTestFileTimestampEnabledAndAppended() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setResultsDirectory(File.separator + "tmp");
        testArgs.setResultsTimestamp(true);
        testArgs.appendTimestamp(true);
        testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + File.separator + "tmp" + File.separator + "test-" + timestamp + ".jtl" + " -n -t " + testFilePath);
    }

    @Test
    public void resultsFileIsCSVFormat() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setResultsDirectory(File.separator + "tmp");
        testArgs.setResultFileOutputFormatIsCSV(true);
        testArgs.setResultsTimestamp(true);
        testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName())
                .isEqualTo(File.separator + "tmp" + File.separator + timestamp + "-test.csv");
    }

    @Test
    public void resultsFileIsXMLFormat() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setResultsDirectory(File.separator + "tmp");
        testArgs.setResultFileOutputFormatIsCSV(false);
        testArgs.setResultsTimestamp(true);
        testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName())
                .isEqualTo(File.separator + "tmp" + File.separator + timestamp + "-test.jtl");
    }

    @Test
    public void resultsFileDefaultsToXMLFormat() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setResultsDirectory(File.separator + "tmp");
        testArgs.setResultsTimestamp(true);
        testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName())
                .isEqualTo(File.separator + "tmp" + File.separator + timestamp + "-test.jtl");
    }

    @Test
    public void resultsFileHasCorrectName() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setResultsDirectory("temp");
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + "temp" + File.separator + "test.jtl" + " -n -t " + testFile.getAbsolutePath());
    }

    @Test
    public void resultsFileContainsDirectoryStructureInName() throws Exception {
        File testFileTwo = new File(this.testFileTwoURL.toURI());

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setResultsDirectory("temp");
        testArgs.setTestFile(testFileTwo, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -l " + "temp" + File.separator + "subdir_test.jtl" + " -n -t " + testFileTwo.getAbsolutePath());
    }

    @Test
    public void validateCommandLineWhenSettingLogsDirectory() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setLogsDirectory("/target/jmeter/logs");
        testArgs.setTestFile(testFile, testFileDirectory);

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -j /target/jmeter/logs" + File.separator + "test.jmx.log -l " + testArgs.getResultsLogFileName() + " -n -t " + testFilePath);
    }


    @Test
    public void validateCommandLineWithReportGenerationEnabled() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(testFile, testFileDirectory);
        testArgs.setReportsDirectory("/target/jmeter/reports");

        assertThat(testArgs.getResultsLogFileName()).isNotEmpty();
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/" + " -e -l " + testArgs.getResultsLogFileName() + " -n -o /target/jmeter/reports -t " + testFilePath);
    }

    @Test
    public void validateCommandLineWhenSettingServerModeWithJustPort() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(enableGUI, "target/jmeter/");
        testArgs.setLogsDirectory(logsDirectory);
        testArgs.setServerMode(null, serverPort);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -s -j " + logsDirectory + File.separator + "localhost_" + serverPort + ".log");
    }

    @Test
    public void validateCommandLineWhenSettingServerModeWithHostNameAndPort() throws Exception {
        String hostname = "jmeter.example.com";

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(enableGUI, "target/jmeter/");
        testArgs.setLogsDirectory(logsDirectory);
        testArgs.setServerMode(hostname, serverPort);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -s -j " + logsDirectory + File.separator + hostname + "_" + serverPort + ".log");
    }

    @Test
    public void validateCommandLineWhenSettingServerModeWithHostIPAndPort() throws Exception {
        String hostname = "10.10.10.4";

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(enableGUI, "target/jmeter/");
        testArgs.setLogsDirectory(logsDirectory);
        testArgs.setServerMode(hostname, serverPort);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -s -j " + logsDirectory + File.separator + hostname + "_" + serverPort + ".log");
    }

    @Test
    public void validateCommandLineWhenSettingServerModeWithHostIPAndPortWithoutLogging() throws Exception {
        String hostname = "10.10.10.4";

        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(enableGUI, "target/jmeter/");
        testArgs.setServerMode(hostname, serverPort);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()))
                .isEqualTo("-d target/jmeter/ -s");
    }
}
