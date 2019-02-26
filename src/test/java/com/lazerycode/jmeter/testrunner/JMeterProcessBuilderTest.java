package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class JMeterProcessBuilderTest {

    @Test
    public void defaultArgumentsListIsAsExpected() {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();
        String runtimeJarName = "jmeter";
        JMeterProcessBuilder jMeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName);

        assertThat(jMeterProcessBuilder.constructArgumentsList())
                .containsSequence(jMeterProcessJVMSettings.getJavaRuntime(), String.format("-Xms%sM", jMeterProcessJVMSettings.getXms()), String.format("-Xmx%sM", jMeterProcessJVMSettings.getXmx()), "-jar", runtimeJarName);
    }

    @Test
    public void argumentsListAsExpectedWhenJVMSettingsHasAdditionalArguments() {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings().addArgument("foo");
        String runtimeJarName = "jmeter";
        JMeterProcessBuilder jMeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName);

        assertThat(jMeterProcessBuilder.constructArgumentsList()).containsSequence(
                jMeterProcessJVMSettings.getJavaRuntime(),
                String.format("-Xms%sM", jMeterProcessJVMSettings.getXms()),
                String.format("-Xmx%sM", jMeterProcessJVMSettings.getXmx()),
                "foo",
                "-jar",
                runtimeJarName
        );
    }

    @Test
    public void argumentsListAsExpectedWhenAddingAdditionalArgumentsToProcessBuilder() {
        ArrayList<String> additionalArguments = new ArrayList<String>() {{
            add("bar");
            add("day");
        }};
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();
        String runtimeJarName = "jmeter";
        JMeterProcessBuilder jMeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName)
                .addArguments(additionalArguments);

        assertThat(jMeterProcessBuilder.constructArgumentsList()).containsSequence(
                jMeterProcessJVMSettings.getJavaRuntime(),
                String.format("-Xms%sM", jMeterProcessJVMSettings.getXms()),
                String.format("-Xmx%sM", jMeterProcessJVMSettings.getXmx()),
                "-jar",
                runtimeJarName,
                "bar",
                "day"
        );
    }

    @Test
    public void argumentsListAsExpectedWhenAddingAdditionalArgumentsToProcessBuilderAndJVMSettings() {
        ArrayList<String> additionalArguments = new ArrayList<String>() {{
            add("bar");
            add("day");
        }};
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings().addArgument("foo");
        String runtimeJarName = "jmeter";
        JMeterProcessBuilder jMeterProcessBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName)
                .addArguments(additionalArguments);

        assertThat(jMeterProcessBuilder.constructArgumentsList()).containsSequence(
                jMeterProcessJVMSettings.getJavaRuntime(),
                String.format("-Xms%sM", jMeterProcessJVMSettings.getXms()),
                String.format("-Xmx%sM", jMeterProcessJVMSettings.getXmx()),
                "foo",
                "-jar",
                runtimeJarName,
                "bar",
                "day"
        );
    }

    @Test
    public void processBuilderIsConstructedCorrectly() throws MojoExecutionException {
        File workingDirectory = new File(this.getClass().getResource("/").getFile());
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();
        String runtimeJarName = "jmeter";
        ProcessBuilder processBuilder = new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName)
                .setWorkingDirectory(workingDirectory)
                .build();

        assertThat(processBuilder.directory()).isEqualTo(workingDirectory);
        assertThat(processBuilder.command()).containsSequence(
                jMeterProcessJVMSettings.getJavaRuntime(),
                String.format("-Xms%sM", jMeterProcessJVMSettings.getXms()),
                String.format("-Xmx%sM", jMeterProcessJVMSettings.getXmx()),
                "-jar",
                runtimeJarName
        );

    }

    @Test(expected = MojoExecutionException.class)
    public void invalidWorkingDirectoryThrowsMojoExecutionException() throws MojoExecutionException {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();
        String runtimeJarName = "jmeter";
        new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName)
                .setWorkingDirectory(new File("/some/made/up/directory/xinvald"));
    }

    @Test(expected = MojoExecutionException.class)
    public void invalidWorkingDirectoryFilenameThrowsMojoExecutionException() throws MojoExecutionException {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();
        String runtimeJarName = "jmeter";
        new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName)
                .setWorkingDirectory(new File("\u0000"));
    }

    @Test(expected = MojoExecutionException.class)
    public void throwsMojoExecutionExceptionIfWorkingDirectoryIsNotSet() throws MojoExecutionException {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();
        String runtimeJarName = "jmeter";
        new JMeterProcessBuilder(jMeterProcessJVMSettings, runtimeJarName)
                .build();
    }
}
