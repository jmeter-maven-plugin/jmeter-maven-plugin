package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.UtilityFunctions;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class JMeterArgumentsArrayTest {

    private final URL testFile = this.getClass().getResource("/test.jmx");
    private String testFilePath;
    private boolean disableGUI = true;

    String argumentsMapToString(Map<String, String> value, JMeterCommandLineArguments type) {
        String arguments = "";
        Set<String> globalPropertySet = value.keySet();
        for (String property : globalPropertySet) {
            arguments += type.getCommandLineArgument() + " ";
            arguments += property + "=" + value.get(property) + " ";
        }
        return arguments.trim();
    }

    @Before
    public void setTestFileAbsolutePath() throws URISyntaxException {
        testFilePath = new File(this.testFile.toURI()).getAbsolutePath();
    }

    @Test(expected = MojoExecutionException.class)
    public void noTestSpecified() throws MojoExecutionException {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.buildArgumentsArray();
    }

    @Test(expected = MojoExecutionException.class)
    public void jMeterHomeEmpty() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "");
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.buildArgumentsArray();
    }

    @Test(expected = MojoExecutionException.class)
    public void jMeterHomeNull() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, null);
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.buildArgumentsArray();
    }

    @Test
    public void validateDefaultCommandLineOutput() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        assertThat(testArgs.getResultsLogFileName(),
                is(not(equalTo(""))));
        assertThat(testArgs.getResultsLogFileName(),
                is(not(equalTo(null))));
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/")));
    }

    @Test
    public void validateJMeterCustomPropertiesFile() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        File testPropFile = new File("test.properties");
        testArgs.setACustomPropertiesFile(testPropFile);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -q " + testPropFile.getAbsolutePath())));
    }

    @Test
    public void validateJMeterChangeRootLogLevel() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        testArgs.setLogRootOverride("DEBUG");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -L DEBUG")));
    }

    @Test
    public void validateJMeterChangeIndividualLogLevels() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        Map<String, String> logLevels = new HashMap<String, String>();
        logLevels.put("jorphan", "INFO");
        logLevels.put("jmeter.UtilityFunctions", "DEBUG");
        testArgs.setLogCategoriesOverrides(logLevels);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ " + argumentsMapToString(logLevels, JMeterCommandLineArguments.LOGLEVEL))));
    }

    @Test
    public void validateJMeterSetProxyHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPort(8080);
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        testArgs.setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -H http://10.10.50.43 -P 8080")));
    }

    @Test
    public void validateJMeterSetProxyUsername() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setUsername("god");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        testArgs.setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -u god")));
    }

    @Test
    public void validateJMeterSetProxyPassword() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setPassword("changeme");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        testArgs.setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -a changeme")));
    }

    @Test
    public void validateSetNonProxyHosts() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHostExclusions("localhost|*.lazerycode.com");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        testArgs.setProxyConfig(proxyConfiguration);

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -N localhost|*.lazerycode.com")));
    }

    @Test
    public void validateSetRemoteStop() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        testArgs.setRemoteStop();

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -X")));
    }

    @Test
    public void validateSetRemoteStartAll() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        testArgs.setRemoteStartAll();

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -r")));
    }

    @Test
    public void validateSetRemoteStart() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
        testArgs.setTestFile(new File(this.testFile.toURI()));

        testArgs.setRemoteStart("server1, server2");

        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -R server1, server2")));
    }
}
