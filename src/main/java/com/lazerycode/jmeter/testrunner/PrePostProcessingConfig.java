package com.lazerycode.jmeter.testrunner;

import java.io.File;

/**
 * Post and Pre Processing configuration
 * The idea is to have the possibility to launch some script at each jmeter script and externally
 * Example : jcmd JFR.start ,....
 */
public class PrePostProcessingConfig {

	/**
	 * Add the test name on script as argument
	 */
	private final boolean addTestNameOnScript;

	/**
	 * Global Pre processing Script
	 */
	private final String globalPreProcessingScript;
	/**
	 * Global Post processing Script
	 */
	private final String globalPostProcessingScript;

	/**
	 * Pre processing before jmeter run
	 */
	private final String preProcessingScript;
	/**
	 * Post processing after jmeter run
	 */
	private final String postProcessingScript;
	private final File directory;

	public PrePostProcessingConfig(String globalPreProcessingScript, String globalPostProcessingScript, String preProcessingScript, String postProcessingScript, boolean addTestNameOnScript, File directory) {
		this.globalPreProcessingScript=globalPreProcessingScript;
		this.globalPostProcessingScript=globalPostProcessingScript;
		this.preProcessingScript=preProcessingScript;
		this.postProcessingScript=postProcessingScript;
		this.addTestNameOnScript=addTestNameOnScript;

		this.directory = directory;
	}


	public String getGlobalPreProcessingScript() {
		return globalPreProcessingScript;
	}

	public String getGlobalPostProcessingScript() {
		return globalPostProcessingScript;
	}

	public String getPreProcessingScript() {
		return preProcessingScript;
	}

	public String getPostProcessingScript() {
		return postProcessingScript;
	}

	public boolean isAddTestNameOnScript() {
		return addTestNameOnScript;
	}

	public File getDirectory() {
		return directory;
	}


}
