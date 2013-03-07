package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.UtilityFunctions;
import org.apache.maven.plugin.MojoExecutionException;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.lazerycode.jmeter.configuration.JMeterCommandLineArguments.*;

/**
 * Creates an arguments array to pass to the JMeter object to run tests.
 *
 * @author Mark Collin
 */
public class JMeterArgumentsArray {

	private final boolean disableTests;
	private TreeSet<JMeterCommandLineArguments> argumentList = new TreeSet<JMeterCommandLineArguments>();
	private DateTimeFormatter dateFormat = ISODateTimeFormat.dateTime();
	private boolean timestampResults = true;
	private boolean appendTimestamp = false;
	private String remoteStartList = null;
	private String nonProxyHosts = null;
	private String proxyHost = null;
	private String proxyPort = null;
	private String proxyUsername = null;
	private String proxyPassword = null;
	private String customPropertiesFile = null;
	private String testFile = null;
	private String resultsLogFileName = null;
	private String jMeterHome = null;
	private String resultsDirectory = null;
	private String overrideRootLogLevel = null;
	private Map<String, String> overrideLogCategories = null;

	/**
	 * The argument map will define which arguments are set on the command line.
	 * The order properties are initially put into the argument map defines the order they are returned in the array produced by this class.
	 */
	public JMeterArgumentsArray(boolean disableGUI, String jMeterHomeDirectory) throws MojoExecutionException {
		if (UtilityFunctions.isNotSet(jMeterHomeDirectory)) {
			throw new MojoExecutionException("Unable to set JMeter Home Directory...");
		}
		jMeterHome = jMeterHomeDirectory;
		argumentList.add(JMETER_HOME_OPT);
		if (disableGUI) {
			argumentList.add(NONGUI_OPT);
			disableTests = false;
		} else {
			disableTests = true;
		}
	}

	public void setResultsFileNameDateFormat(DateTimeFormatter dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setRemoteStop() {
		argumentList.add(REMOTE_STOP);
	}

	public void setRemoteStartAll() {
		argumentList.add(REMOTE_OPT);
	}

	public void setRemoteStart(String remoteStart) {
		if (UtilityFunctions.isNotSet(remoteStart)) return;
		remoteStartList = remoteStart;
		argumentList.add(REMOTE_OPT_PARAM);
	}

	public void setProxyConfig(ProxyConfiguration proxyConfiguration) {
		setProxyHostDetails(proxyConfiguration.getHost(), proxyConfiguration.getPort());
		setProxyUsername(proxyConfiguration.getUsername());
		setProxyPassword(proxyConfiguration.getPassword());
		setNonProxyHosts(proxyConfiguration.getHostExclusions());
	}

	private void setProxyHostDetails(String hostname, int port) {
		if (UtilityFunctions.isNotSet(hostname)) return;
		proxyHost = hostname;
		proxyPort = Integer.toString(port);
		argumentList.add(PROXY_HOST);
		argumentList.add(PROXY_PORT);
	}

	private void setProxyUsername(String username) {
		if (UtilityFunctions.isNotSet(username)) return;
		proxyUsername = username;
		argumentList.add(PROXY_USERNAME);
	}

	private void setProxyPassword(String password) {
		if (UtilityFunctions.isNotSet(password)) return;
		proxyPassword = password;
		argumentList.add(PROXY_PASSWORD);
	}

	private void setNonProxyHosts(String hostsList) {
		if (UtilityFunctions.isNotSet(hostsList)) return;
		nonProxyHosts = hostsList;
		argumentList.add(NONPROXY_HOSTS);
	}

	public void setACustomPropertiesFile(File customProperties) {
		if (UtilityFunctions.isNotSet(customProperties)) return;
		customPropertiesFile = customProperties.getAbsolutePath();
		argumentList.add(PROPFILE2_OPT);
	}

	//TODO we should support this rather than expecting people to modify thier jmeter.properties
	public void setLogCategoriesOverrides(Map<String, String> logCategoryOverrideMap) {
		if (UtilityFunctions.isNotSet(logCategoryOverrideMap)) return;
		overrideLogCategories = logCategoryOverrideMap;
		argumentList.add(LOGLEVEL);
	}

	//TODO we should support this rather than expecting people to modify thier jmeter.properties
	public void setLogRootOverride(String logLevel) {
		if (UtilityFunctions.isNotSet(logLevel)) return;
		overrideRootLogLevel = logLevel;
		//TODO ERROR should not be same as above...
		argumentList.add(LOGLEVEL);
	}

	public void setResultsDirectory(String resultsDirectory) {
		this.resultsDirectory = resultsDirectory;
	}

	public void setTestFile(File value) {
		if (UtilityFunctions.isNotSet(value) || disableTests) return;
		testFile = value.getAbsolutePath();
		if (timestampResults) {
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

	public void setResultsTimestamp(boolean addTimestamp) {
		timestampResults = addTimestamp;
	}

	public void appendTimestamp(boolean append) {
		appendTimestamp = append;
	}

	public String getResultsLogFileName() {
		return resultsLogFileName;
	}

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
					if (overrideRootLogLevel == null) {
						Set<String> logCategorySet = overrideLogCategories.keySet();
						for (String category : logCategorySet) {
							argumentsArray.add(LOGLEVEL.getCommandLineArgument());
							argumentsArray.add(category + "=" + overrideLogCategories.get(category));
						}
					} else {
						argumentsArray.add(LOGLEVEL.getCommandLineArgument());
						argumentsArray.add(overrideRootLogLevel);
					}
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
					argumentsArray.add(proxyHost);
					break;
				case PROXY_PORT:
					argumentsArray.add(PROXY_PORT.getCommandLineArgument());
					argumentsArray.add(proxyPort);
					break;
				case PROXY_USERNAME:
					argumentsArray.add(PROXY_USERNAME.getCommandLineArgument());
					argumentsArray.add(proxyUsername);
					break;
				case PROXY_PASSWORD:
					argumentsArray.add(PROXY_PASSWORD.getCommandLineArgument());
					argumentsArray.add(proxyPassword);
					break;
				case NONPROXY_HOSTS:
					argumentsArray.add(NONPROXY_HOSTS.getCommandLineArgument());
					argumentsArray.add(nonProxyHosts);
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
