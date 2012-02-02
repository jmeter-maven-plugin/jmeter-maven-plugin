package com.lazerycode.jmeter.properties;

/**
 * Created by IntelliJ IDEA.
 * User: Mark Collin
 * Date: 01/02/12
 * Time: 21:35
 * To change this template use File | Settings | File Templates.
 */

public enum ReservedProperties {

    JAVA_CLASS_PATH("java.class.path"),
    USER_DIR("user.dir");

    private final String propertyKey;

    ReservedProperties(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyKey() {
        return propertyKey;
    }
}
