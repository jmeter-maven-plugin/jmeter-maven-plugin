package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class JMeterProcessBuilder {

	private int initialHeapSizeInMegaBytes;
	private int maximumHeapSizeInMegaBytes;
	private String workingDirectory;
	private String javaRuntime;
	private List<String> userSuppliedArguments;
	private List<String> mainClassArguments = new ArrayList<String>();

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

	public ArrayList<String> getJVMArgumentsList() {
		ArrayList<String> argumentsList = new ArrayList<String>();
		argumentsList.add(MessageFormat.format("-Xms{0}M", String.valueOf(this.initialHeapSizeInMegaBytes)));
		argumentsList.add(MessageFormat.format("-Xmx{0}M", String.valueOf(this.maximumHeapSizeInMegaBytes)));
		for (String argument : userSuppliedArguments) {
			argumentsList.add(argument);
		}
		return argumentsList;
	}

	private String[] constructArgumentsList() {
		String mainClass = "ApacheJMeter.jar";

		List<String> argumentsList = new ArrayList<String>();
		argumentsList.add(javaRuntime);
		argumentsList.addAll(getJVMArgumentsList());

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
