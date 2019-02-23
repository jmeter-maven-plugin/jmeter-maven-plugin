package com.lazerycode.jmeter.testrunner;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;

public class JMeterProcessBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(JMeterProcessBuilder.class);
	private int initialHeapSizeInMegaBytes;
	private int maximumHeapSizeInMegaBytes;
	private final String runtimeJarName;
	private String workingDirectory;
	private String javaRuntime;
	private List<String> userSuppliedArguments;
	private List<String> mainClassArguments = new ArrayList<>();

	public JMeterProcessBuilder(JMeterProcessJVMSettings settings, String runtimeJarName) {
		this.runtimeJarName = runtimeJarName;
		this.initialHeapSizeInMegaBytes = settings.getXms();
		this.maximumHeapSizeInMegaBytes = settings.getXmx();
		this.userSuppliedArguments = settings.getArguments();
		this.javaRuntime = settings.getJavaRuntime();
	}

	public void setWorkingDirectory(File workingDirectory) throws MojoExecutionException {
		try {
			this.workingDirectory = workingDirectory.getCanonicalPath();
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to set working directory for JMeter process!", e);
		}
	}

	public void addArguments(List<String> arguments) {
		this.mainClassArguments.addAll(arguments);
	}

	protected String[] constructArgumentsList() {
		List<String> argumentsList = new ArrayList<>();
		argumentsList.add(javaRuntime);
		argumentsList.add(MessageFormat.format("-Xms{0}M", String.valueOf(initialHeapSizeInMegaBytes)));
		argumentsList.add(MessageFormat.format("-Xmx{0}M", String.valueOf(maximumHeapSizeInMegaBytes)));
		argumentsList.addAll(userSuppliedArguments);
		argumentsList.add("-jar");
		argumentsList.add(runtimeJarName);
		argumentsList.addAll(mainClassArguments);

		LOGGER.debug("Arguments for forked JMeter JVM: {}", argumentsList);

		return argumentsList.toArray(new String[0]);
	}

	public Process startProcess() throws IOException {
	    String[] arguments = constructArgumentsList();
	    LOGGER.info("Starting process with:{}", Arrays.asList(arguments));
		ProcessBuilder processBuilder = new ProcessBuilder(arguments);
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(new File(workingDirectory));

		return processBuilder.start();
	}
}
