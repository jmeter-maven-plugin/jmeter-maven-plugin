package com.lazerycode.jmeter.enums;

/**
 * An Enum holding all of the command line arguments accepted by JMeter
 *
 * @author Mark Collin
 */
public enum JMeterCommandLineArguments {

    PROXY_PASSWORD("a"),
    JMETER_HOME_OPT("d"),
    HELP_OPT("h"),              //Shows help information, never used in this plugin.
    JMLOGFILE_OPT("j"),         //TODO
    LOGFILE_OPT("l"),
    NONGUI_OPT("n"),
    PROPFILE_OPT("p"),          //We place the jmeter.log in the correct place on the filesystem so this is never needed
    PROPFILE2_OPT("q"),
    REMOTE_OPT("r"),
    SERVER_OPT("s"),            //TODO
    TESTFILE_OPT("t"),
    PROXY_USERNAME("u"),
    VERSION_OPT("v"),           //TODO
    SYSTEM_PROPERTY("D"),
    JMETER_GLOBAL_PROP("G"),
    PROXY_HOST("H"),
    JMETER_PROPERTY("J"),
    LOGLEVEL("L"),
    NONPROXY_HOSTS("N"),        //TODO
    PROXY_PORT("P"),
    REMOTE_OPT_PARAM("R"),      //TODO
    SYSTEM_PROPFILE("S"),       //TODO
    REMOTE_STOP("X");           //TODO

    private final String commandLineArgument;

    JMeterCommandLineArguments(String commandLineArgument) {
        this.commandLineArgument = commandLineArgument;
    }

    public String getCommandLineArgument() {
        return "-" + commandLineArgument;
    }

}
