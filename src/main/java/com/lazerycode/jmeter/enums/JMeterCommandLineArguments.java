package com.lazerycode.jmeter.enums;

/**
 * An Enum holding all of the command line arguments accepted by JMeter
 *
 * @author Mark Collin
 */
public enum JMeterCommandLineArguments {

    PROXY_PASSWORD("a"),        //DOCUMENTED
    JMETER_HOME_OPT("d"),
    HELP_OPT("h"),              //Shows help information, never used in this plugin.
    JMLOGFILE_OPT("j"),         //TODO
    LOGFILE_OPT("l"),
    NONGUI_OPT("n"),            //This plugin always runs JMeter in non-gui mode
    PROPFILE_OPT("p"),          //We place the jmeter.properties in the correct place on the filesystem so this is never needed
    PROPFILE2_OPT("q"),
    REMOTE_OPT("r"),
    SERVER_OPT("s"),            //TODO
    TESTFILE_OPT("t"),          //DOCUMENTED -- Always used, this is how we pass our test file list over to JMeter
    PROXY_USERNAME("u"),        //DOCUMENTED
    VERSION_OPT("v"),           //TODO
    SYSTEM_PROPERTY("D"),       //DOCUMENTED
    JMETER_GLOBAL_PROP("G"),    //DOCUMENTED
    PROXY_HOST("H"),            //DOCUMENTED
    JMETER_PROPERTY("J"),       //DOCUMENTED
    LOGLEVEL("L"),              //DOCUMENTED
    NONPROXY_HOSTS("N"),        //DOCUMENTED
    PROXY_PORT("P"),            //DOCUMENTED
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
