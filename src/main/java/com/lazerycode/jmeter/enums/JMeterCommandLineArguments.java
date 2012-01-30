package com.lazerycode.jmeter.enums;

/**
 * An Enum holding all of the command line arguments accepted by JMeter
 *
 * @author Mark Collin
 */
public enum JMeterCommandLineArguments {

    PROXY_PASSWORD("a"),        //DOCUMENTED
    JMETER_HOME_OPT("d"),
    HELP_OPT("h"),              //NOT_USED - Prints help information and exits.
    JMLOGFILE_OPT("j"),         //TODO
    LOGFILE_OPT("l"),
    NONGUI_OPT("n"),            //ALWAYS_SET - This plugin always runs JMeter in non-gui mode
    PROPFILE_OPT("p"),          //NOT_USED - We place the jmeter.properties in the correct place on the filesystem.
    PROPFILE2_OPT("q"),
    REMOTE_OPT("r"),            //DOCUMENTED
    SERVER_OPT("s"),            //TODO
    TESTFILE_OPT("t"),          //ALWAYS_SET - This is how we pass our test file list over to JMeter
    PROXY_USERNAME("u"),        //DOCUMENTED
    VERSION_OPT("v"),           //NOT_USED - Prints version information and exits.
    SYSTEM_PROPERTY("D"),       //DOCUMENTED
    JMETER_GLOBAL_PROP("G"),    //DOCUMENTED
    PROXY_HOST("H"),            //DOCUMENTED
    JMETER_PROPERTY("J"),       //DOCUMENTED
    LOGLEVEL("L"),              //DOCUMENTED
    NONPROXY_HOSTS("N"),        //DOCUMENTED
    PROXY_PORT("P"),            //DOCUMENTED
    REMOTE_OPT_PARAM("R"),      //DOCUMENTED
    SYSTEM_PROPFILE("S"),       //TODO
    REMOTE_STOP("X");           //DOCUMENTED

    private final String commandLineArgument;

    JMeterCommandLineArguments(String commandLineArgument) {
        this.commandLineArgument = commandLineArgument;
    }

    public String getCommandLineArgument() {
        return "-" + commandLineArgument;
    }

}
