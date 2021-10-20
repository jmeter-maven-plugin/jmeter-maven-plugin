package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;
import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class JMeterProcessBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMeterProcessBuilder.class);
    private final int initialHeapSizeInMegaBytes;
    private final int maximumHeapSizeInMegaBytes;
    private final String runtimeJarName;
    private final String javaRuntime;
    private final List<String> userSuppliedArguments;
    private List<String> mainClassArguments = new ArrayList<>();
    private String workingDirectory;

    public JMeterProcessBuilder(JMeterProcessJVMSettings settings, String runtimeJarName) {
        this.runtimeJarName = runtimeJarName;
        this.initialHeapSizeInMegaBytes = settings.getXms();
        this.maximumHeapSizeInMegaBytes = settings.getXmx();
        this.userSuppliedArguments = settings.getArguments();
        this.javaRuntime = settings.getJavaRuntime();
    }

    public JMeterProcessBuilder setWorkingDirectory(File workingDirectory) throws MojoExecutionException {
        try {
            this.workingDirectory = workingDirectory.getCanonicalPath();
            if (!workingDirectory.exists()) {
                throw new MojoExecutionException("Working directory does not exist!");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to set working directory for JMeter process!", e);
        }

        return this;
    }

    public JMeterProcessBuilder addArguments(List<String> arguments) {
        mainClassArguments.addAll(arguments);

        return this;
    }

    public ProcessBuilder build() throws MojoExecutionException {
        if (null == workingDirectory) {
            throw new MojoExecutionException("Working directory is not set!");
        }

        return new ProcessBuilder(constructArgumentsList())
                .directory(new File(workingDirectory))
                .redirectErrorStream(true);
    }

    List<String> constructArgumentsList() {
        List<String> argumentsList = new ArrayList<>();
        argumentsList.add(javaRuntime);
        argumentsList.add(MessageFormat.format("-Xms{0}M", String.valueOf(initialHeapSizeInMegaBytes)));
        argumentsList.add(MessageFormat.format("-Xmx{0}M", String.valueOf(maximumHeapSizeInMegaBytes)));
        argumentsList.addAll(userSuppliedArguments);
        argumentsList.add("-jar");
        argumentsList.add(runtimeJarName);
        argumentsList.addAll(mainClassArguments);

        LOGGER.info("Arguments for forked JMeter JVM: {}", argumentsList);
        LOGGER.info(" ");

        return argumentsList;
    }
}
