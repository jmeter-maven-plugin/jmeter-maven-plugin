package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.UtilityFunctions;
import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;
import com.lazerycode.jmeter.configuration.JMeterCommandLineArguments;
import com.lazerycode.jmeter.configuration.ProxyConfiguration;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class JMeterArgumentsArrayTest {

    private File reportDir = new File("${basedir}/target/jmeter-report");
    private URL testFile = this.getClass().getResource("/test.jmx");

    public String argumentsMapToString(Map<String, String> value, JMeterCommandLineArguments type) {
        String arguments = "";
        Set<String> globalPropertySet = (Set<String>) value.keySet();
        for (String property : globalPropertySet) {
            arguments += type.getCommandLineArgument() + " ";
            arguments += property + "=" + value.get(property) + " ";
        }
        return arguments.trim();
    }

    @Test(expected = MojoExecutionException.class)
    public void noTestSpecified() throws MojoExecutionException {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.buildArgumentsArray();
    }

    @Test(expected = MojoExecutionException.class)
    public void propertiesFileNotSet() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.buildArgumentsArray();
    }

    @Test(expected = MojoExecutionException.class)
    public void jMeterHomeNotSet() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.buildArgumentsArray();
    }

    @Test
    public void validateDefaultCommandLineOutput() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");

        assertThat(testArgs.getResultsFileName(),
                is(not(equalTo(""))));
        assertThat(testArgs.getResultsFileName(),
                is(not(equalTo(null))));
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/")));
    }

    @Test
    public void validateJMeterRemoteProperties() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        Map<String, String> remoteProps = new HashMap<String, String>();
        remoteProps.put("threads", "1");
        remoteProps.put("testIterations", "10");
        testArgs.setRemoteProperties(remoteProps);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ " + argumentsMapToString(remoteProps, JMeterCommandLineArguments.JMETER_GLOBAL_PROP))));
    }

    @Test
    public void validateJMeterGlobalProperties() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        Map<String, String> globalProps = new HashMap<String, String>();
        globalProps.put("threads", "1");
        globalProps.put("testIterations", "10");
        testArgs.setGlobalProperties(globalProps);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ " + argumentsMapToString(globalProps, JMeterCommandLineArguments.JMETER_PROPERTY) + " " + argumentsMapToString(globalProps, JMeterCommandLineArguments.JMETER_GLOBAL_PROP))));
    }

    @Test
    public void validateJMeterRemotePropertiesFile() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        File testPropFile = new File("test.properties");
        testArgs.setRemotePropertiesFile(testPropFile);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -G " + testPropFile.getAbsolutePath())));
    }

    @Test
    public void validateJMeterLocalProperties() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        Map<String, String> localProps = new HashMap<String, String>();
        localProps.put("threads", "1");
        localProps.put("testIterations", "10");
        testArgs.setUserProperties(localProps);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ " + argumentsMapToString(localProps, JMeterCommandLineArguments.JMETER_PROPERTY))));
    }

    @Test
    public void validateJMeterSystemProperties() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        Map<String, String> sysProps = new HashMap<String, String>();
        sysProps.put("user.dir", "/home/foo/working");
        sysProps.put("user.home", "/home/foo");
        testArgs.setSystemProperties(sysProps);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ " + argumentsMapToString(sysProps, JMeterCommandLineArguments.SYSTEM_PROPERTY))));
    }

    @Test
    public void validateJMeterSystemPropertiesFile() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        File testPropFile = new File("test.properties");
        testArgs.setASystemPropertiesFile(testPropFile);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -S " + testPropFile.getAbsolutePath())));
    }

    @Test
    public void validateJMeterCustomPropertiesFile() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        File testPropFile = new File("test.properties");
        testArgs.setACustomPropertiesFile(testPropFile);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -q " + testPropFile.getAbsolutePath())));
    }

    @Test
    public void validateJMeterChangeRootLogLevel() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setLogRootOverride("DEBUG");
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -L DEBUG")));
    }

    @Test
    public void validateJMeterChangeIndividualLogLevels() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        Map<String, String> logLevels = new HashMap<String, String>();
        logLevels.put("jorphan", "INFO");
        logLevels.put("jmeter.UtilityFunctions", "DEBUG");
        testArgs.setLogCategoriesOverrides(logLevels);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ " + argumentsMapToString(logLevels, JMeterCommandLineArguments.LOGLEVEL))));
    }

    @Test
    public void validateJMeterSetProxyHost() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPort(8080);
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setProxyConfig(proxyConfiguration);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -H http://10.10.50.43 -P 8080")));
    }

    @Test
    public void validateJMeterSetProxyUsername() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setUsername("god");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setProxyConfig(proxyConfiguration);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -u god")));
    }

    @Test
    public void validateJMeterSetProxyPassword() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setPassword("changeme");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setProxyConfig(proxyConfiguration);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -a changeme")));
    }

    @Test
    public void validateSetNonProxyHosts() throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHostExclusions("localhost|*.lazerycode.com");
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setProxyConfig(proxyConfiguration);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -N localhost|*.lazerycode.com")));
    }

    @Test
    public void validateSetRemoteStop() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setRemoteStop(true);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -X")));
    }

    @Test
    public void validateSetRemoteStartAll() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setRemoteStartAll(true);
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -r")));
    }

    @Test
    public void validateSetRemoteStart() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setRemoteStart("server1, server2");
        assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
                is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFileName() + " -d target/jmeter/ -R server1, server2")));
    }
}
