package com.lazerycode.jmeter;

import com.lazerycode.jmeter.enums.JMeterCommandLineArguments;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Series of useful utility functions to make life easy
 *
 * @author Mark Collin
 */
public class UtilityFunctions {

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

}
