package com.lazerycode.jmeter;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class JMeterArgumentsArrayTest {

    private File reportDir = new File("${basedir}/target/jmeter-report");
    private URL testFile = this.getClass().getResource("/test.jmx");
    private static Utilities util = new Utilities();

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
        assertThat(testArgs.getResultsFilename(), not(equalTo("")));
        assertThat(testArgs.getResultsFilename(), not(equalTo(null)));
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/")));
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
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ " + util.argumentsMapToString(remoteProps, JMeterCommandLineArguments.JMETER_GLOBAL_PROP))));
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
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ " + util.argumentsMapToString(globalProps, JMeterCommandLineArguments.JMETER_PROPERTY) + " " + util.argumentsMapToString(globalProps, JMeterCommandLineArguments.JMETER_GLOBAL_PROP))));
    }

    @Test
    public void validateJMeterRemotePropertiesFile() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        File testPropFile = new File("test.properties");
        testArgs.setRemotePropertiesFile(testPropFile);
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ -G " + testPropFile.getAbsolutePath())));
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
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ " + util.argumentsMapToString(localProps, JMeterCommandLineArguments.JMETER_PROPERTY))));
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
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ " + util.argumentsMapToString(sysProps, JMeterCommandLineArguments.SYSTEM_PROPERTY))));
    }

    @Test
    public void validateJMeterSystemPropertiesFile() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        File testPropFile = new File("test.properties");
        testArgs.setASystemPropertiesFile(testPropFile);
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ -S " + testPropFile.getAbsolutePath())));
    }

    @Test
    public void validateJMeterCustomPropertiesFile() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        File testPropFile = new File("test.properties");
        testArgs.setACustomPropertiesFile(testPropFile);
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ -q " + testPropFile.getAbsolutePath())));
    }

    @Test
    public void validateJMeterChangeRootLogLevel() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setLogRootOverride("DEBUG");
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ -L DEBUG")));
    }

    @Test
    public void validateJMeterChangeIndividualLogLevels() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        Map<String, String> logLevels = new HashMap<String, String>();
        logLevels.put("jorphan", "INFO");
        logLevels.put("jmeter.util", "DEBUG");
        testArgs.setLogCategoriesOverrides(logLevels);
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ " + util.argumentsMapToString(logLevels, JMeterCommandLineArguments.LOGLEVEL))));
    }

    @Test
    public void validateJMeterUseRemoteHost() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setUseRemoteHost(true);
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ -r")));
    }

    @Test
    public void validateJMeterSetProxyHost() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setProxyHostDetails("http://10.10.50.43", 8080);
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ -H http://10.10.50.43 -P 8080")));
    }

    @Test
    public void validateJMeterSetProxyUsername() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setProxyUsername("god");
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ -u god")));
    }

    @Test
    public void validateJMeterSetProxyPassword() throws Exception {
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        testArgs.setProxyPassword("changeme");
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -d target/jmeter/ -a changeme")));
    }
}
