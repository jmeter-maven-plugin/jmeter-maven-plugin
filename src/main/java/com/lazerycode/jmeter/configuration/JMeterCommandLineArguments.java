package com.lazerycode.jmeter.configuration;

/**
 * An Enum holding all of the command line arguments accepted by JMeter
 * The values are defined in a specific order to ensure the order attributes are applied to the command line.
 *
 * @author Mark Collin
 */
public enum JMeterCommandLineArguments {

	PROXY_PASSWORD("a"),            //DOCUMENTED
	JMETER_HOME_OPT("d"),           //ALWAYS_SET - The JMeter dir structure is created by the plugin.
	REPORT_AT_END_OPT("e"),         //DOCUMENTED
	HELP_OPT("h"),                  //NOT_USED - Prints help information and exits.
	REPORT_GENERATING_OPT("g"),     //NOT_USED - We always generate reports in the verify phase.
	JMLOGFILE_OPT("j"),             //ALWAYS_SET - Test name is used for logfile name (<testname>.log).
	LOGFILE_OPT("l"),               //ALWAYS_SET - Test name is used for logfile name (<testname>.jtl).
	NONGUI_OPT("n"),                //DOCUMENTED
	REPORT_OUTPUT_FOLDER_OPT("o"),  //DOCUMENTED
	PROPFILE_OPT("p"),              //NOT_USED - We place the jmeter.properties in the correct place on the filesystem.
	PROPFILE2_OPT("q"),             //DOCUMENTED
	REMOTE_OPT("r"),                //DOCUMENTED
	SERVER_OPT("s"),                //NOT_USED - We are never going to start up a server instance on the command line, we are only running tests.
	TESTFILE_OPT("t"),              //ALWAYS_SET - This is how we pass our test file list over to JMeter.
	PROXY_USERNAME("u"),            //DOCUMENTED
	VERSION_OPT("v"),               //NOT_USED - Prints version information and exits.
	SYSTEM_PROPERTY("D"),           //DOCUMENTED
	JMETER_GLOBAL_PROP("G"),        //DOCUMENTED
	PROXY_HOST("H"),                //DOCUMENTED
	JMETER_PROPERTY("J"),           //DOCUMENTED
	LOGLEVEL("L"),                  //DOCUMENTED
	NONPROXY_HOSTS("N"),            //DOCUMENTED
	PROXY_PORT("P"),                //DOCUMENTED
	REMOTE_OPT_PARAM("R"),          //DOCUMENTED
	SYSTEM_PROPFILE("S"),           //NOT_USED - We place the system.properties in the correct place on the filesystem.
	REMOTE_STOP("X");               //DOCUMENTED

	private final String commandLineArgument;

	JMeterCommandLineArguments(String commandLineArgument) {
		this.commandLineArgument = commandLineArgument;
	}

	public String getCommandLineArgument() {
		return "-" + commandLineArgument;
	}

}
