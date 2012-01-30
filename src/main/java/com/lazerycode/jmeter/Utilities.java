package com.lazerycode.jmeter;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Series of useful utilities to make life easy
 *
 * @author Mark Collin
 */
public class Utilities {

    public static String humanReadableCommandLineOutput(String[] arguments) {
        String debugOutput = "";
        for (String argument : arguments) {
            debugOutput += argument + " ";
        }
        return debugOutput.trim();
    }

    public static Boolean isNotSet(Map<?, ?> value) {
        if (value == null || value.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isNotSet(String value) {
        if (value == null || value.isEmpty() || value.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isNotSet(File value) {
        //TODO: value#toString() can't ever return null if value != null
        if (value == null || value.toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static String argumentsMapToString(Map<String, String> value, JMeterCommandLineArguments type) {
        String arguments = "";
        Set<String> globalPropertySet = value.keySet();
        for (String property : globalPropertySet) {
            arguments += type.getCommandLineArgument() + " ";
            arguments += property + "=" + value.get(property) + " ";
        }
        return arguments.trim();
    }
}
