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
	private int initialHeapSizeInMegaBytes;
	private int maximumHeapSizeInMegaBytes;
	private String workingDirectory;
	private String javaRuntime;
	private List<String> userSuppliedArguments;
	private List<String> mainClassArguments = new ArrayList<>();

	public JMeterProcessBuilder(JMeterProcessJVMSettings settings) {
		if (null == settings) {
			settings = new JMeterProcessJVMSettings();
		}
		this.initialHeapSizeInMegaBytes = settings.getXms();
		this.maximumHeapSizeInMegaBytes = settings.getXmx();
		this.userSuppliedArguments = settings.getArguments();
		this.javaRuntime = settings.getJavaRuntime();
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
		String mainClass = "ApacheJMeter-3.0-SNAPSHOT.jar";

		List<String> argumentsList = new ArrayList<>();
		argumentsList.add(javaRuntime);
		argumentsList.add(MessageFormat.format("-Xms{0}M", String.valueOf(this.initialHeapSizeInMegaBytes)));
		argumentsList.add(MessageFormat.format("-Xmx{0}M", String.valueOf(this.maximumHeapSizeInMegaBytes)));
		for (String argument : userSuppliedArguments) {
			argumentsList.add(argument);
		}

		argumentsList.add("-jar");
		argumentsList.add(mainClass);
		for (String arg : mainClassArguments) {
			argumentsList.add(arg);
		}

		LOGGER.debug("Arguments for forked JMeter JVM: " + argumentsList.toString());

		return argumentsList.toArray(new String[argumentsList.size()]);
	}

	public Process startProcess() throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(constructArgumentsList());
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(new File(this.workingDirectory));
		return processBuilder.start();
	}
}
