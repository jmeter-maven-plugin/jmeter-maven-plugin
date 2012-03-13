package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.UtilityFunctions;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Creates an arguments array to pass to the JMeter object to run tests.
 *
 * @author Mark Collin
 */
public class JMeterArgumentsArray {

    private LinkedHashMap<JMeterCommandLineArguments, Boolean> argumentMap = new LinkedHashMap<JMeterCommandLineArguments, Boolean>();
    private DateFormat fmt = new SimpleDateFormat("yyMMdd");
    private boolean timestampResults = true;
    private String remoteStartList = null;
    private String nonProxyHosts = null;
    private String proxyHost = null;
    private String proxyPort = null;
    private String proxyUsername = null;
    private String proxyPassword = null;
    private String customPropertiesFile = null;
    private String testFile = null;
    private String resultsFileName = null;
    private String jMeterHome = null;
    private String reportDirectory = null;
    private String overrideRootLogLevel = null;
    private Map<String,String> overrideLogCategories = null;

    /**
     * The argument map will define which arguments are set on the command line.
     * The order properties are initially put into the argument map defines the order they are returned in the array produced by this class.
     */
    public JMeterArgumentsArray(String reportDirectory) {
        this.reportDirectory = reportDirectory;
        argumentMap.put(JMeterCommandLineArguments.NONGUI_OPT, true);           //Always suppress the GUI.
        argumentMap.put(JMeterCommandLineArguments.TESTFILE_OPT, false);        //Required - test file as specified.
        argumentMap.put(JMeterCommandLineArguments.LOGFILE_OPT, false);         //Required - output file as specified.
        argumentMap.put(JMeterCommandLineArguments.JMETER_HOME_OPT, false);     //Required - JMETER_HOME location as specified.
        argumentMap.put(JMeterCommandLineArguments.SYSTEM_PROPFILE, false);     //Set to true if system properties are specified.
        argumentMap.put(JMeterCommandLineArguments.SYSTEM_PROPERTY, false);     //Set to true if system properties are specified.
        argumentMap.put(JMeterCommandLineArguments.JMETER_PROPERTY, false);     //Set to true if user properties are specified.
        argumentMap.put(JMeterCommandLineArguments.JMETER_GLOBAL_PROP, false);  //Set to true if global properties are specified(These get sent to remote servers as well).
        argumentMap.put(JMeterCommandLineArguments.LOGLEVEL, false);            //Set to true if log level overrides have been specified
        argumentMap.put(JMeterCommandLineArguments.PROPFILE2_OPT, false);       //Set to true if a custom properties file is specified.
        argumentMap.put(JMeterCommandLineArguments.REMOTE_OPT, false);          //Set to true if a remote host used.
        argumentMap.put(JMeterCommandLineArguments.PROXY_HOST, false);          //Set to true if proxy host is specified
        argumentMap.put(JMeterCommandLineArguments.PROXY_PORT, false);          //Set to true if proxy port is specified
        argumentMap.put(JMeterCommandLineArguments.PROXY_USERNAME, false);      //Set to true if proxy username is specified
        argumentMap.put(JMeterCommandLineArguments.PROXY_PASSWORD, false);      //Set to true if proxy password is specified
        argumentMap.put(JMeterCommandLineArguments.NONPROXY_HOSTS, false);      //Set to true if non-proxy hosts are specified
        argumentMap.put(JMeterCommandLineArguments.REMOTE_STOP, false);         //Set to true to stop remote servers at the end of the tests.
        argumentMap.put(JMeterCommandLineArguments.REMOTE_OPT_PARAM, false);    //Set to true to stop remote servers at the end of the tests.
    }

    public void setRemoteStop(boolean value) {
        this.argumentMap.put(JMeterCommandLineArguments.REMOTE_STOP, value);
    }

    public void setRemoteStartAll(boolean value) {
        this.argumentMap.put(JMeterCommandLineArguments.REMOTE_OPT, value);
    }

    public void setRemoteStart(String value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.remoteStartList = value;
        this.argumentMap.put(JMeterCommandLineArguments.REMOTE_OPT_PARAM, true);
    }

    public void setProxyConfig(ProxyConfiguration proxyConfiguration) {
        this.setProxyHostDetails(proxyConfiguration.getHost(), proxyConfiguration.getPort());
        this.setProxyUsername(proxyConfiguration.getUsername());
        this.setProxyPassword(proxyConfiguration.getPassword());
        this.setNonProxyHosts(proxyConfiguration.getHostExclusions());

    }

    private void setProxyHostDetails(String value, int port) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.proxyHost = value;
        this.proxyPort = Integer.toString(port);
        this.argumentMap.put(JMeterCommandLineArguments.PROXY_HOST, true);
        this.argumentMap.put(JMeterCommandLineArguments.PROXY_PORT, true);
    }

    private void setProxyUsername(String value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.proxyUsername = value;
        this.argumentMap.put(JMeterCommandLineArguments.PROXY_USERNAME, true);
    }

    private void setProxyPassword(String value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.proxyPassword = value;
        this.argumentMap.put(JMeterCommandLineArguments.PROXY_PASSWORD, true);
    }

    private void setNonProxyHosts(String value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.nonProxyHosts = value;
        this.argumentMap.put(JMeterCommandLineArguments.NONPROXY_HOSTS, true);
    }

    //TODO enable this?  There is no defined name for this file so we can't automatically pick it up.
    public void setACustomPropertiesFile(File value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.customPropertiesFile = value.getAbsolutePath();
        this.argumentMap.put(JMeterCommandLineArguments.PROPFILE2_OPT, true);
    }

    //TODO we should support this rather than expecting people to modify thier jmeter.properties
    public void setLogCategoriesOverrides(Map<String,String> value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.overrideLogCategories = value;
        this.argumentMap.put(JMeterCommandLineArguments.LOGLEVEL, true);
    }

    //TODO we should support this rather than expecting people to modify thier jmeter.properties
    public void setLogRootOverride(String value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.overrideRootLogLevel = value;
        this.argumentMap.put(JMeterCommandLineArguments.LOGLEVEL, true);
    }

    public void setTestFile(File value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.testFile = value.getAbsolutePath();
        this.argumentMap.put(JMeterCommandLineArguments.TESTFILE_OPT, true);
        if (this.timestampResults) {
            this.resultsFileName = this.reportDirectory + File.separator + value.getName().substring(0, value.getName().lastIndexOf(".")) + "-" + fmt.format(new Date()) + ".jtl";
        } else {
            this.resultsFileName = this.reportDirectory + File.separator + value.getName().substring(0, value.getName().lastIndexOf(".")) + ".jtl";
        }
        this.argumentMap.put(JMeterCommandLineArguments.LOGFILE_OPT, true);
    }

    public void setJMeterHome(String value) {
        if (UtilityFunctions.isNotSet(value)) return;
        this.jMeterHome = value;
        this.argumentMap.put(JMeterCommandLineArguments.JMETER_HOME_OPT, true);
    }

    public void setResultsTimestamp(boolean value) {
        this.timestampResults = value;
    }

    public String getResultsFileName() {
        return this.resultsFileName;
    }

    public String[] buildArgumentsArray() throws MojoExecutionException {
        if (!argumentMap.get(JMeterCommandLineArguments.TESTFILE_OPT)) {
            throw new MojoExecutionException("No test specified!");
        } else if (!argumentMap.get(JMeterCommandLineArguments.LOGFILE_OPT)) {
            throw new MojoExecutionException("Log file not set!");
        } else if (!argumentMap.get(JMeterCommandLineArguments.JMETER_HOME_OPT)) {
            throw new MojoExecutionException("JMETER_HOME not set!");
        }
        ArrayList<String> argumentsArray = new ArrayList<String>();
        Iterator<Map.Entry<JMeterCommandLineArguments, Boolean>> mapIterator = argumentMap.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<JMeterCommandLineArguments, Boolean> argument = mapIterator.next();
            if (argument.getValue()) {
                switch (argument.getKey()) {
                    case NONGUI_OPT:
                        argumentsArray.add(JMeterCommandLineArguments.NONGUI_OPT.getCommandLineArgument());
                        break;
                    case TESTFILE_OPT:
                        argumentsArray.add(JMeterCommandLineArguments.TESTFILE_OPT.getCommandLineArgument());
                        argumentsArray.add(this.testFile);
                        break;
                    case LOGFILE_OPT:
                        argumentsArray.add(JMeterCommandLineArguments.LOGFILE_OPT.getCommandLineArgument());
                        argumentsArray.add(this.resultsFileName);
                        break;
                    case JMETER_HOME_OPT:
                        argumentsArray.add(JMeterCommandLineArguments.JMETER_HOME_OPT.getCommandLineArgument());
                        argumentsArray.add(this.jMeterHome);
                        break;
                    case LOGLEVEL:
                        if (this.overrideRootLogLevel == null) {
                            Set<String> logCategorySet = this.overrideLogCategories.keySet();
                            for (String category : logCategorySet) {
                                argumentsArray.add(JMeterCommandLineArguments.LOGLEVEL.getCommandLineArgument());
                                argumentsArray.add(category + "=" + this.overrideLogCategories.get(category));
                            }
                        } else {
                            argumentsArray.add(JMeterCommandLineArguments.LOGLEVEL.getCommandLineArgument());
                            argumentsArray.add(this.overrideRootLogLevel);
                        }
                        break;
                    case PROPFILE2_OPT:
                        argumentsArray.add(JMeterCommandLineArguments.PROPFILE2_OPT.getCommandLineArgument());
                        argumentsArray.add(this.customPropertiesFile);
                        break;
                    case REMOTE_OPT:
                        argumentsArray.add(JMeterCommandLineArguments.REMOTE_OPT.getCommandLineArgument());
                        break;
                    case PROXY_HOST:
                        argumentsArray.add(JMeterCommandLineArguments.PROXY_HOST.getCommandLineArgument());
                        argumentsArray.add(this.proxyHost);
                        break;
                    case PROXY_PORT:
                        argumentsArray.add(JMeterCommandLineArguments.PROXY_PORT.getCommandLineArgument());
                        argumentsArray.add(this.proxyPort);
                        break;
                    case PROXY_USERNAME:
                        argumentsArray.add(JMeterCommandLineArguments.PROXY_USERNAME.getCommandLineArgument());
                        argumentsArray.add(proxyUsername);
                        break;
                    case PROXY_PASSWORD:
                        argumentsArray.add(JMeterCommandLineArguments.PROXY_PASSWORD.getCommandLineArgument());
                        argumentsArray.add(proxyPassword);
                        break;
                    case NONPROXY_HOSTS:
                        argumentsArray.add(JMeterCommandLineArguments.NONPROXY_HOSTS.getCommandLineArgument());
                        argumentsArray.add(this.nonProxyHosts);
                        break;
                    case REMOTE_STOP:
                        argumentsArray.add(JMeterCommandLineArguments.REMOTE_STOP.getCommandLineArgument());
                        break;
                    case REMOTE_OPT_PARAM:
                        argumentsArray.add(JMeterCommandLineArguments.REMOTE_OPT_PARAM.getCommandLineArgument());
                        argumentsArray.add(this.remoteStartList);
                        break;}
            }
        }
        return argumentsArray.toArray(new String[argumentsArray.size()]);
    }
}
