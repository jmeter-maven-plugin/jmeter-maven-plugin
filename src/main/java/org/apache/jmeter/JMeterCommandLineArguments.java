package org.apache.jmeter;

public enum JMeterCommandLineArguments {

    PROXY_PASSWORD("a"),
    JMETER_HOME_OPT("d"),
    HELP_OPT("h"),
    JMLOGFILE_OPT("j"),
    LOGFILE_OPT("l"),
    NONGUI_OPT("n"),
    PROPFILE_OPT("p"),
    PROPFILE2_OPT("q"),
    REMOTE_OPT("r"),
    SERVER_OPT("s"),
    TESTFILE_OPT("t"),
    PROXY_USERNAME("u"),
    VERSION_OPT("v"),
    SYSTEM_PROPERTY("D"),
    JMETER_GLOBAL_PROP("G"),
    PROXY_HOST("H"),
    JMETER_PROPERTY("J"),
    LOGLEVEL("L"),
    NONPROXY_HOSTS("N"),
    PROXY_PORT("P"),
    REMOTE_OPT_PARAM("R"),
    SYSTEM_PROPFILE("S"),
    REMOTE_STOP("X");

    private final String commandLineArgument;

    JMeterCommandLineArguments(String commandLineArgument) {
        this.commandLineArgument = commandLineArgument;
    }

    public String getCommandLineArgument() {
        return "-" + commandLineArgument;
    }

}
