package com.lazerycode.jmeter.configuration;

import org.apache.maven.plugin.MojoExecutionException;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

import static com.lazerycode.jmeter.UtilityFunctions.isNotSet;
import static com.lazerycode.jmeter.UtilityFunctions.isSet;
import static com.lazerycode.jmeter.configuration.JMeterCommandLineArguments.*;

/**
 * Creates an arguments array to pass to the JMeter object to run tests.
 *
 * @author Mark Collin
 */
public class JMeterArgumentsArray {

	private final String jMeterHome;
	private final boolean disableTests;
	private final TreeSet<JMeterCommandLineArguments> argumentList = new TreeSet<JMeterCommandLineArguments>();
	private DateTimeFormatter dateFormat = ISODateTimeFormat.dateTime();
	private ProxyConfiguration proxyConfiguration;
	private boolean timestampResults = true;
	private boolean appendTimestamp = false;
	private String remoteStartList;
	private String customPropertiesFile;
	private String testFile;
	private String resultsLogFileName;
	private String resultsDirectory;
	private LogLevel overrideRootLogLevel;

	/**
	 * Create an instance of JMeterArgumentsArray
	 *
	 * @param disableGUI          If GUI should be disabled or not
	 * @param jMeterHomeDirectory The JMETER_HOME directory, what JMeter bases its classpath on
	 * @throws MojoExecutionException
	 */
	public JMeterArgumentsArray(boolean disableGUI, String jMeterHomeDirectory) throws MojoExecutionException {
		if (isNotSet(jMeterHomeDirectory)) throw new MojoExecutionException("Unable to set JMeter Home Directory...");
		jMeterHome = jMeterHomeDirectory;
		argumentList.add(JMETER_HOME_OPT);
		if (disableGUI) {
			argumentList.add(NONGUI_OPT);
			disableTests = false;
		} else {
			disableTests = true;
		}
	}

	public void setRemoteStop() {
		argumentList.add(REMOTE_STOP);
	}

	public void setRemoteStartAll() {
		argumentList.add(REMOTE_OPT);
	}

	public void setRemoteStart(String remoteStart) {
		if (isNotSet(remoteStart)) return;
		remoteStartList = remoteStart;
		argumentList.add(REMOTE_OPT_PARAM);
	}

	public void setProxyConfig(ProxyConfiguration configuration) {
		this.proxyConfiguration = configuration;
		if (isSet(proxyConfiguration.getHost())) {
			argumentList.add(PROXY_HOST);
			argumentList.add(PROXY_PORT);
		}
		if (isSet(proxyConfiguration.getUsername())) {
			argumentList.add(PROXY_USERNAME);
		}
		if (isSet(proxyConfiguration.getPassword())) {
			argumentList.add(PROXY_PASSWORD);
		}
		if (isSet(proxyConfiguration.getHostExclusions())) {
			argumentList.add(NONPROXY_HOSTS);
		}
	}

	public void setACustomPropertiesFile(File customProperties) {
		if (isNotSet(customProperties)) return;
		customPropertiesFile = customProperties.getAbsolutePath();
		argumentList.add(PROPFILE2_OPT);
	}

	public void setLogRootOverride(String requestedLogLevel) {
		if (isNotSet(requestedLogLevel)) return;
		for (LogLevel logLevel : LogLevel.values()) {
			if (logLevel.toString().equals(requestedLogLevel.toUpperCase())) {
				overrideRootLogLevel = logLevel;
				argumentList.add(LOGLEVEL);
			}
		}
	}

	public void setResultsDirectory(String resultsDirectory) {
		this.resultsDirectory = resultsDirectory;
	}

	public void setResultsTimestamp(boolean addTimestamp) {
		timestampResults = addTimestamp;
	}

	public void setResultsFileNameDateFormat(DateTimeFormatter dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void appendTimestamp(boolean append) {
		appendTimestamp = append;
	}

	public String getResultsLogFileName() {
		return resultsLogFileName;
	}

	public void setTestFile(File value) {
		if (isNotSet(value) || disableTests) return;
		testFile = value.getAbsolutePath();
		if (timestampResults) {
			//TODO investigate when timestamp is generated.
			if (appendTimestamp) {
				resultsLogFileName = resultsDirectory + File.separator + value.getName().substring(0, value.getName().lastIndexOf(".")) + "-" + dateFormat.print(new LocalDateTime()) + ".jtl";
			} else {
				resultsLogFileName = resultsDirectory + File.separator + dateFormat.print(new LocalDateTime()) + "-" + value.getName().substring(0, value.getName().lastIndexOf(".")) + ".jtl";
			}
		} else {
			resultsLogFileName = resultsDirectory + File.separator + value.getName().substring(0, value.getName().lastIndexOf(".")) + ".jtl";
		}
		argumentList.add(TESTFILE_OPT);
		argumentList.add(LOGFILE_OPT);
	}

	/**
	 * Generate an arguments array representing the command line options you want to send to JMeter.
	 * The order of the array is determined by the order the values in JMeterCommandLineArguments are defined.
	 *
	 * @return An array representing the command line sent to JMeter
	 * @throws MojoExecutionException
	 */
	public String[] buildArgumentsArray() throws MojoExecutionException {
		if (!argumentList.contains(TESTFILE_OPT) && !disableTests) throw new MojoExecutionException("No test(s) specified!");
		ArrayList<String> argumentsArray = new ArrayList<String>();
		for (JMeterCommandLineArguments argument : argumentList) {
			switch (argument) {
				case NONGUI_OPT:
					argumentsArray.add(NONGUI_OPT.getCommandLineArgument());
					break;
				case TESTFILE_OPT:
					argumentsArray.add(TESTFILE_OPT.getCommandLineArgument());
					argumentsArray.add(testFile);
					break;
				case LOGFILE_OPT:
					argumentsArray.add(LOGFILE_OPT.getCommandLineArgument());
					argumentsArray.add(resultsLogFileName);
					break;
				case JMETER_HOME_OPT:
					argumentsArray.add(JMETER_HOME_OPT.getCommandLineArgument());
					argumentsArray.add(jMeterHome);
					break;
				case LOGLEVEL:
					argumentsArray.add(LOGLEVEL.getCommandLineArgument());
					argumentsArray.add(overrideRootLogLevel.toString());
					break;
				case PROPFILE2_OPT:
					argumentsArray.add(PROPFILE2_OPT.getCommandLineArgument());
					argumentsArray.add(customPropertiesFile);
					break;
				case REMOTE_OPT:
					argumentsArray.add(REMOTE_OPT.getCommandLineArgument());
					break;
				case PROXY_HOST:
					argumentsArray.add(PROXY_HOST.getCommandLineArgument());
					argumentsArray.add(proxyConfiguration.getHost());
					break;
				case PROXY_PORT:
					argumentsArray.add(PROXY_PORT.getCommandLineArgument());
					argumentsArray.add(proxyConfiguration.getPort());
					break;
				case PROXY_USERNAME:
					argumentsArray.add(PROXY_USERNAME.getCommandLineArgument());
					argumentsArray.add(proxyConfiguration.getUsername());
					break;
				case PROXY_PASSWORD:
					argumentsArray.add(PROXY_PASSWORD.getCommandLineArgument());
					argumentsArray.add(proxyConfiguration.getPassword());
					break;
				case NONPROXY_HOSTS:
					argumentsArray.add(NONPROXY_HOSTS.getCommandLineArgument());
					argumentsArray.add(proxyConfiguration.getHostExclusions());
					break;
				case REMOTE_STOP:
					argumentsArray.add(REMOTE_STOP.getCommandLineArgument());
					break;
				case REMOTE_OPT_PARAM:
					argumentsArray.add(REMOTE_OPT_PARAM.getCommandLineArgument());
					argumentsArray.add(remoteStartList);
					break;
			}
		}
		return argumentsArray.toArray(new String[argumentsArray.size()]);
	}
}