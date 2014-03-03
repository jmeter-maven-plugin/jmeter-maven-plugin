/**
 *
 */
package com.lazerycode.jmeter.testrunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

/**
 *
 */
public abstract class AbstractJavaProcessBuilder {

    protected List<String> userSuppliedArguments = new ArrayList<String>();

    private String workingDirectory;

    private List<String> mainClassArguments = new ArrayList<String>();

    private String mainClass;

    public AbstractJavaProcessBuilder(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setWorkingDirectory(File workingDirectory) throws MojoExecutionException {
        try {
            this.workingDirectory = workingDirectory.getCanonicalPath();
        } catch (IOException ignored) {
            throw new MojoExecutionException("Unable to set working directory for JMeter process!");
        }
    }

    public void addArguments(List<String> arguments) {
        for (String argument : arguments) {
            this.mainClassArguments.add(argument);
        }
    }

    private String[] constructArgumentsList() {
        String javaRuntime = "java";

        List<String> argumentsList = new ArrayList<String>();
        argumentsList.add(javaRuntime);
        for (String argument : userSuppliedArguments) {
            argumentsList.add(argument);
        }

        argumentsList.add("-jar");
        argumentsList.add(mainClass);
        for (String arg : mainClassArguments) {
            argumentsList.add(arg);
        }

        return argumentsList.toArray(new String[argumentsList.size()]);
    }

    public Process startProcess() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(constructArgumentsList());
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(this.workingDirectory));
        return processBuilder.start();
    }
}
