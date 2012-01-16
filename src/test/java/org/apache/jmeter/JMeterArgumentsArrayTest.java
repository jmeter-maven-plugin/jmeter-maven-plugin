package org.apache.jmeter;

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
    private URL propertiesFile = this.getClass().getResource("/jmeter.properties");
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
        testArgs.setJMeterDefaultPropertiesFile(new File(this.propertiesFile.toURI()));
        testArgs.buildArgumentsArray();
    }

    @Test
    public void validateDefaultCommandLineOutput() throws Exception{
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterDefaultPropertiesFile(new File(this.propertiesFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        assertThat(testArgs.getResultsFilename(), not(equalTo("")));
        assertThat(testArgs.getResultsFilename(), not(equalTo(null)));
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -p " + new File(this.propertiesFile.toURI()).getAbsolutePath() + " -d target/jmeter/ ")));
    }

    @Test
    public void validateJMeterRemoteProperties() throws Exception{
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterDefaultPropertiesFile(new File(this.propertiesFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        Map<String, String> remoteProps = new HashMap<String, String>();
        remoteProps.put("threads", "1");
        remoteProps.put("testIterations", "10");
        testArgs.setRemoteProperties(remoteProps);
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -p " + new File(this.propertiesFile.toURI()).getAbsolutePath() + " -d target/jmeter/ " + util.argumentsMapToString(remoteProps, JMeterCommandLineArguments.JMETER_GLOBAL_PROP))));
    }

    @Test
    public void validateJMeterGlobalProperties() throws Exception{
        JMeterArgumentsArray testArgs = new JMeterArgumentsArray(reportDir.getAbsolutePath());
        testArgs.setTestFile(new File(this.testFile.toURI()));
        testArgs.setJMeterDefaultPropertiesFile(new File(this.propertiesFile.toURI()));
        testArgs.setJMeterHome("target/jmeter/");
        Map<String, String> globalProps = new HashMap<String, String>();
        globalProps.put("threads", "1");
        globalProps.put("testIterations", "10");
        testArgs.setGlobalProperties(globalProps);
        assertThat(util.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()), is(equalTo("-n -t " + new File(this.testFile.toURI()).getAbsolutePath() + " -l " + testArgs.getResultsFilename() + " -p " + new File(this.propertiesFile.toURI()).getAbsolutePath() + " -d target/jmeter/ " + util.argumentsMapToString(globalProps, JMeterCommandLineArguments.JMETER_PROPERTY) + util.argumentsMapToString(globalProps, JMeterCommandLineArguments.JMETER_GLOBAL_PROP))));
    }

    //TODO test that each command line switch is set correctly
}
