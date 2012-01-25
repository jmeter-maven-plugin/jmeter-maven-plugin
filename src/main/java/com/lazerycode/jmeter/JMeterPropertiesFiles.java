package com.lazerycode.jmeter;

/**
 * An Enum holding a list of properties files expected to be in the bin dir by JMeter
 *
 * @author Mark Collin
 */
public enum JMeterPropertiesFiles {

    JMETER("jmeter.properties"),
    SAVESERVICE("saveservice.properties"),
    UPGRADE("upgrade.properties"),
    SYSTEM("system.properties"),
    USER("user.properties");

    private final String propertiesFileName;

    JMeterPropertiesFiles(String commandLineArgument) {
        this.propertiesFileName = commandLineArgument;
    }

    public String getPropertiesFileName() {
        return propertiesFileName;
    }

}