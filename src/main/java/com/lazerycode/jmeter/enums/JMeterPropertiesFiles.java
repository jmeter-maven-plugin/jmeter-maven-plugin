package com.lazerycode.jmeter.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * An Enum holding a list of JMeter properties files.
 * If it is set to true JMeter expects it to be in the bin dir.
 *
 * @author Mark Collin
 */
public enum JMeterPropertiesFiles {

    JMETER("jmeter.properties", true),
    SAVESERVICE("saveservice.properties", true),
    UPGRADE("upgrade.properties", true),
    SYSTEM("system.properties", false),
    USER("user.properties", false);

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