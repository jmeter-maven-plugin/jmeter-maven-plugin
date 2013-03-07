package com.lazerycode.jmeter.configuration;

/**
 * An Enum holding all of the command line arguments accepted by JMeter
 *
 * @author Mark Collin
 */
public enum JMeterCommandLineArguments {

    NONGUI_OPT("n"),            //ALWAYS_SET - This plugin always runs JMeter in non-gui mode.
    TESTFILE_OPT("t"),          //ALWAYS_SET - This is how we pass our test file list over to JMeter.
    LOGFILE_OPT("l"),           //ALWAYS_SET - Test name is used for logfile name (<testname>.jtl).
    JMETER_HOME_OPT("d"),       //ALWAYS_SET - The JMeter dir structure is created by the plugin.
    SYSTEM_PROPFILE("S"),       //NOT_USED - We place the system.properties in the correct place on the filesystem.
    SYSTEM_PROPERTY("D"),       //DOCUMENTED
    JMETER_PROPERTY("J"),       //DOCUMENTED
    JMETER_GLOBAL_PROP("G"),    //DOCUMENTED
    LOGLEVEL("L"),              //DOCUMENTED
    PROPFILE2_OPT("q"),         //DOCUMENTED
    REMOTE_OPT("r"),            //DOCUMENTED
    PROXY_HOST("H"),            //DOCUMENTED
    PROXY_PORT("P"),            //DOCUMENTED
    PROXY_USERNAME("u"),        //DOCUMENTED
    PROXY_PASSWORD("a"),        //DOCUMENTED
    NONPROXY_HOSTS("N"),        //DOCUMENTED
    REMOTE_STOP("X"),           //DOCUMENTED
    REMOTE_OPT_PARAM("R"),      //DOCUMENTED
    JMLOGFILE_OPT("j"),         //ALWAYS_SET - Test name is used for logfile name (<testname>.log).
    PROPFILE_OPT("p"),          //NOT_USED - We place the jmeter.properties in the correct place on the filesystem.
    SERVER_OPT("s"),            //NOT_USED - We are never going to start up a server instance on the command line, we are only running tests.
    VERSION_OPT("v"),           //NOT_USED - Prints version information and exits.
    HELP_OPT("h");              //NOT_USED - Prints help information and exits.

    private final String commandLineArgument;

    JMeterCommandLineArguments(String commandLineArgument) {
        this.commandLineArgument = commandLineArgument;
    }

    public String getCommandLineArgument() {
        return "-" + commandLineArgument;
    }

}
