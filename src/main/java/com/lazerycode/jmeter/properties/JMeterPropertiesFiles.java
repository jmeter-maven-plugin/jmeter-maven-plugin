package com.lazerycode.jmeter.properties;

/**
 * An Enum holding a list of JMeter properties files.
 * If it is set to true JMeter expects it to be in the bin dir.
 *
 * @author Mark Collin
 */
public enum JMeterPropertiesFiles {

    JMETER_PROPERTIES("jmeter.properties", true),
    SAVE_SERVICE_PROPERTIES("saveservice.properties", true),
    UPGRADE_PROPERTIES("upgrade.properties", true),
    SYSTEM_PROPERTIES("system.properties", false),
    USER_PROPERTIES("user.properties", false),
    GLOBAL_PROPERTIES("global.properties", false);

    private final Object[] propertiesData;

    JMeterPropertiesFiles(Object ...values) {
        this.propertiesData = values;
    }

    public String getPropertiesFileName() {
        return (String) this.propertiesData[0];
    }

    public boolean createFileIfItDoesntExist() {
        return (Boolean) this.propertiesData[1];
    }

}