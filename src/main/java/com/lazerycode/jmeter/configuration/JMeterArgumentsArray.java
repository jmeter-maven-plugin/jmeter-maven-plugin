package com.lazerycode.jmeter.configuration;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static com.lazerycode.jmeter.configuration.JMeterCommandLineArguments.*;
import static com.lazerycode.jmeter.utility.UtilityFunctions.isNotSet;
import static com.lazerycode.jmeter.utility.UtilityFunctions.isSet;

/**
 * Creates an arguments array to pass to the JMeter object to run tests.
 *
 * @author Mark Collin
 */
public class JMeterArgumentsArray {

	private final String jMeterHome;
	private boolean disableTests;

	private final TreeSet<JMeterCommandLineArguments> argumentList = new TreeSet<JMeterCommandLineArguments>();
	private DateTimeFormatter dateFormat = ISODateTimeFormat.basicDate();
	private ProxyConfiguration proxyConfiguration;
	private boolean timestampResults = false;
	private boolean appendTimestamp = false;
	private String resultFileExtension = ".jtl";
	private String remoteStartServerList;
	private List<String> customPropertiesFiles = new ArrayList<>();
	private String testFile;
	private String resultsLogFileName;
	private String jmeterLogFileName;
	private String logsDirectory;
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

	public void setRemoteStart() {
		argumentList.add(REMOTE_OPT);
	}

	public void setRemoteStartServerList(String serverList) {
		if (isNotSet(serverList)) return;
		remoteStartServerList = serverList;
		argumentList.add(REMOTE_OPT_PARAM);
	}

	public void setProxyConfig(ProxyConfiguration configuration) {
		if (configuration == null) return;

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
		customPropertiesFiles.add(customProperties.getAbsolutePath());
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

	public void setLogsDirectory(String logsDirectory) {
		this.logsDirectory = logsDirectory;
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

	public void setResultFileOutputFormatIsCSV(boolean isCSVFormat) {
		if (isCSVFormat) {
			resultFileExtension = ".csv";
		} else {
			resultFileExtension = ".jtl";
		}
	}

	public void setTestFile(File value, File testFilesDirectory) {
		if (isNotSet(value)) return;
		testFile = value.getAbsolutePath();

		String resultFilename = FilenameUtils.removeExtension(testFilesDirectory.toURI().relativize(value.toURI()).getPath().replace(File.separator, "_"));
		resultsLogFileName = resultsDirectory + File.separator;
		if (timestampResults) {
			//TODO investigate when timestamp is generated.
			if (appendTimestamp) {
				resultsLogFileName += resultFilename + "-" + dateFormat.print(new LocalDateTime()) + resultFileExtension;
			} else {
				resultsLogFileName += dateFormat.print(new LocalDateTime()) + "-" + resultFilename + resultFileExtension;
			}
		} else {
			resultsLogFileName += resultFilename + resultFileExtension;
		}
		if (isSet(logsDirectory)) {
			jmeterLogFileName = logsDirectory + File.separator + value.getName() + ".log";
			argumentList.add(JMLOGFILE_OPT);
		}
		argumentList.add(TESTFILE_OPT);
		argumentList.add(LOGFILE_OPT);
		disableTests = false;
	}


	/**
	 * Generate an arguments array representing the command line options you want to send to JMeter.
	 * The order of the array is determined by the order the values in JMeterCommandLineArguments are defined.
	 *
	 * @return An array representing the command line sent to JMeter
	 * @throws MojoExecutionException
	 */
	public List<String> buildArgumentsArray() throws MojoExecutionException {
		if (!argumentList.contains(TESTFILE_OPT) && !disableTests) throw new MojoExecutionException("No test(s) specified!");
		List<String> argumentsArray = new ArrayList<String>();

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
					for (String customPropertiesFile : customPropertiesFiles) {
						argumentsArray.add(PROPFILE2_OPT.getCommandLineArgument());
						argumentsArray.add(customPropertiesFile);
					}
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
					argumentsArray.add(remoteStartServerList);
					break;
				case JMLOGFILE_OPT:
					argumentsArray.add(JMLOGFILE_OPT.getCommandLineArgument());
					argumentsArray.add(jmeterLogFileName);
					break;
			}
		}
		return argumentsArray;
	}
}