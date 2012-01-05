package org.apache.jmeter;

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
    private String proxyHost = null;
    private String proxyPort = null;
    private String proxyUsername = null;
    private String proxyPassword = null;
    private String customPropertiesFile = null;
    private String jMeterGlobalPropertiesFile = null;
    private String testFile = null;
    private String resultsFile = null;
    private String jmeterDefaultPropertiesFile = null;
    private String jMeterHome = null;
    private String reportDirectory = null;
    private String overrideRootLogLevel = null;
    private Map jMeterUserProperties = null;
    private Map jMeterGlobalProperties = null;
    private Map systemProperties = null;
    private Map overrideLogCategories = null;
    private static Utilities util = new Utilities();

    /**
     * The argument map will define which arguments are set on the command line.
     * The order properties are initially put into the argument map defines the order they are returned in the array produced by this class.
     */
    public JMeterArgumentsArray(String reportDirectory) {
        this.reportDirectory = reportDirectory;
        argumentMap.put(JMeterCommandLineArguments.NONGUI_OPT, true);           //Always suppress the GUI.
        argumentMap.put(JMeterCommandLineArguments.TESTFILE_OPT, false);        //Required - test file as specified.
        argumentMap.put(JMeterCommandLineArguments.LOGFILE_OPT, false);         //Required - output file as specified.
        argumentMap.put(JMeterCommandLineArguments.PROPFILE_OPT, false);        //Required - jmeter.properties location as specified.
        argumentMap.put(JMeterCommandLineArguments.JMETER_HOME_OPT, false);     //Required - JMETER_HOME location as specified.
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
    }

    public void setProxyHostDetails(String value, int port) {
        if (util.isNotSet(value)) return;
        this.proxyHost = value;
        this.proxyPort = Integer.toString(port);
        this.argumentMap.put(JMeterCommandLineArguments.PROXY_HOST, true);
        this.argumentMap.put(JMeterCommandLineArguments.PROXY_PORT, true);
    }

    public void setProxyUsername(String value) {
        if (util.isNotSet(value)) return;
        this.proxyUsername = value;
        this.argumentMap.put(JMeterCommandLineArguments.PROXY_USERNAME, true);
    }

    public void setProxyPassword(String value) {
        if (util.isNotSet(value)) return;
        this.proxyPassword = value;
        this.argumentMap.put(JMeterCommandLineArguments.PROXY_PASSWORD, true);
    }

    public void setUseRemoteHost(Boolean value) {
        this.argumentMap.put(JMeterCommandLineArguments.REMOTE_OPT, value);
    }

    public void setACustomPropertiesFile(File value) {
        if (util.isNotSet(value)) return;
        this.customPropertiesFile = value.toString();
        this.argumentMap.put(JMeterCommandLineArguments.PROPFILE2_OPT, true);
    }

    public void setUserProperties(Map value) {
        if (util.isNotSet(value)) return;
        this.jMeterUserProperties = value;
        this.argumentMap.put(JMeterCommandLineArguments.JMETER_PROPERTY, true);
    }

    public void setGlobalProperties(Map value) {
        if (util.isNotSet(value)) return;
        this.jMeterGlobalProperties = value;
        this.argumentMap.put(JMeterCommandLineArguments.JMETER_GLOBAL_PROP, true);
    }

    public void setGlobalPropertiesFile(File value) {
        if (util.isNotSet(value)) return;
        this.jMeterGlobalPropertiesFile = value.toString();
        this.argumentMap.put(JMeterCommandLineArguments.JMETER_GLOBAL_PROP, true);
    }

    public void setLogCategoriesOverrides(Map value) {
        if (util.isNotSet(value)) return;
        this.overrideLogCategories = value;
        this.argumentMap.put(JMeterCommandLineArguments.LOGLEVEL, true);
    }

    public void setLogRootOverride(String value) {
        if (util.isNotSet(value)) return;
        this.overrideRootLogLevel = value;
        this.argumentMap.put(JMeterCommandLineArguments.LOGLEVEL, true);
    }

    public void setSystemProperties(Map value) {
        if (util.isNotSet(value)) return;
        this.systemProperties = value;
        this.argumentMap.put(JMeterCommandLineArguments.SYSTEM_PROPERTY, true);
    }

    public void setTestFile(File value) {
        if (util.isNotSet(value)) return;
        this.testFile = value.getAbsolutePath();
        this.argumentMap.put(JMeterCommandLineArguments.TESTFILE_OPT, true);
        this.resultsFile = this.reportDirectory + File.separator + value.getName().substring(0, value.getName().lastIndexOf(".")) + "-" + fmt.format(new Date()) + ".xml";
        this.argumentMap.put(JMeterCommandLineArguments.LOGFILE_OPT, true);
    }

    public void setJMeterDefaultPropertiesFile(File value) {
        if (util.isNotSet(value)) return;
        this.jmeterDefaultPropertiesFile = value.getAbsolutePath();
        this.argumentMap.put(JMeterCommandLineArguments.PROPFILE_OPT, true);
    }

    public void setJMeterHome(String value) {
        if (util.isNotSet(value)) return;
        this.jMeterHome = value;
        this.argumentMap.put(JMeterCommandLineArguments.JMETER_HOME_OPT, true);
    }

    public String getResultsFilename() {
        return this.resultsFile;
    }

    public String getProxyDetails() {
        String proxyDetails = "Proxy server is not being used.";
        if (!this.argumentMap.get(JMeterCommandLineArguments.PROXY_HOST)) {
            return proxyDetails;
        }
        proxyDetails = "Proxy Details:\n\nHost: " + this.proxyHost + ":" + this.proxyPort + "\n";
        if (this.argumentMap.get(JMeterCommandLineArguments.PROXY_USERNAME)) {
            proxyDetails += "Username:" + this.proxyUsername + "\n";
        }
        if (this.argumentMap.get(JMeterCommandLineArguments.PROXY_PASSWORD)) {
            proxyDetails += "Password:" + this.proxyPassword + "\n";
        }
        return proxyDetails + "\n";
    }

    public String[] buildArgumentsArray() throws MojoExecutionException {
        if (!argumentMap.get(JMeterCommandLineArguments.TESTFILE_OPT)) {
            throw new MojoExecutionException("No test specified!");
        } else if (!argumentMap.get(JMeterCommandLineArguments.LOGFILE_OPT)) {
            throw new MojoExecutionException("Log file not set!");
        } else if (!argumentMap.get(JMeterCommandLineArguments.PROPFILE_OPT)) {
            throw new MojoExecutionException("Properties file not set!");
        } else if (!argumentMap.get(JMeterCommandLineArguments.JMETER_HOME_OPT)) {
            throw new MojoExecutionException("JMETER_HOME not set!");
        }
        ArrayList<String> argumentsArray = new ArrayList<String>();
        Iterator mapIterator = argumentMap.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<JMeterCommandLineArguments, Boolean> argument = (Map.Entry<JMeterCommandLineArguments, Boolean>) mapIterator.next();
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
                        argumentsArray.add(this.resultsFile);
                        break;
                    case PROPFILE_OPT:
                        argumentsArray.add(JMeterCommandLineArguments.PROPFILE_OPT.getCommandLineArgument());
                        argumentsArray.add(this.jmeterDefaultPropertiesFile);
                        break;
                    case JMETER_HOME_OPT:
                        argumentsArray.add(JMeterCommandLineArguments.JMETER_HOME_OPT.getCommandLineArgument());
                        argumentsArray.add(this.jMeterHome);
                        break;
                    case JMETER_PROPERTY:
                        Set<String> userPropertySet = (Set<String>) this.jMeterUserProperties.keySet();
                        for (String property : userPropertySet) {
                            argumentsArray.add(JMeterCommandLineArguments.JMETER_PROPERTY.getCommandLineArgument());
                            argumentsArray.add(property + "=" + this.jMeterUserProperties.get(property));
                        }
                        break;
                    case JMETER_GLOBAL_PROP:
                        if (this.jMeterGlobalPropertiesFile == null) {
                            Set<String> globalPropertySet = (Set<String>) this.jMeterUserProperties.keySet();
                            for (String property : globalPropertySet) {
                                argumentsArray.add(JMeterCommandLineArguments.JMETER_GLOBAL_PROP.getCommandLineArgument());
                                argumentsArray.add(property + "=" + this.jMeterGlobalProperties.get(property));
                            }
                        } else {
                            argumentsArray.add(JMeterCommandLineArguments.JMETER_GLOBAL_PROP.getCommandLineArgument());
                            argumentsArray.add(this.jMeterGlobalPropertiesFile);
                        }
                        break;
                    case LOGLEVEL:
                        if (this.overrideRootLogLevel == null) {
                            Set<String> logCategorySet = (Set<String>) this.overrideLogCategories.keySet();
                            for (String category : logCategorySet) {
                                argumentsArray.add(JMeterCommandLineArguments.LOGLEVEL.getCommandLineArgument());
                                argumentsArray.add(category + "=" + this.overrideLogCategories.get(category));
                            }
                        } else {
                            argumentsArray.add(JMeterCommandLineArguments.LOGLEVEL.getCommandLineArgument());
                            argumentsArray.add(this.overrideRootLogLevel);
                        }
                        break;
                    case SYSTEM_PROPERTY:
                        Set<String> systemPropertySet = (Set<String>) this.systemProperties.keySet();
                        for (String property : systemPropertySet) {
                            argumentsArray.add(JMeterCommandLineArguments.SYSTEM_PROPERTY.getCommandLineArgument());
                            argumentsArray.add(property + "=" + this.jMeterUserProperties.get(property));
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
                }
            }
        }
        return argumentsArray.toArray(new String[]{});
    }
}
