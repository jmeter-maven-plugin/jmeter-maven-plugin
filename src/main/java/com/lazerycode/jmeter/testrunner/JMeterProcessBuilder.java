package com.lazerycode.jmeter.testrunner;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JMeterProcessBuilder {

	private int initialHeapSizeInMegaBytes = 512;
	private int maximumHeapSizeInMegaBytes = 512;
	private String workingDirectory;
	private List<String> mainClassArguments = new ArrayList<String>();
	public Map<String, String> env;

	//TODO allow memory settings to be configured in POM
	public int getInitialHeapSizeInMegabytes() {
		return initialHeapSizeInMegaBytes;
	}

	public void setInitialHeapSizeInMegaBytes(int startingHeapSizeInMegabytes) {
		this.initialHeapSizeInMegaBytes = startingHeapSizeInMegabytes;
	}

	public int getMaximumHeapSizeInMegaBytes() {
		return maximumHeapSizeInMegaBytes;
	}

	public void setMaximumHeapSizeInMegaBytes(int maximumHeapSizeInMegaBytes) {
		this.maximumHeapSizeInMegaBytes = maximumHeapSizeInMegaBytes;
	}

	public void setWorkingDirectory(File workingDirectory) {
		try {
			this.workingDirectory = workingDirectory.getCanonicalPath();
		} catch (IOException ignored) {
			//TODO Throw back a mojo exception here?
		}
	}

	public void addArguments(List<String> arguments) {
		for (String argument : arguments) {
			this.mainClassArguments.add(argument);
		}
	}

	private String[] constructArgumentsList(){
		String javaRuntime = "java";
		String mainClass = "ApacheJMeter.jar";

		List<String> argumentsList = new ArrayList<String>();
		argumentsList.add(javaRuntime);
		argumentsList.add(MessageFormat.format("-Xms{0}M", String.valueOf(this.initialHeapSizeInMegaBytes)));
		argumentsList.add(MessageFormat.format("-Xmx{0}M", String.valueOf(this.maximumHeapSizeInMegaBytes)));
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
		env = processBuilder.environment(); //TODO remove? debug only
		return processBuilder.start();
	}
}
