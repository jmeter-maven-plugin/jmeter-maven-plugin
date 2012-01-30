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

    public String humanReadableCommandLineOutput(String[] arguments) {
        String debugOutput = "";
        for (int i = 0; i < arguments.length; i++) {
            debugOutput += arguments[i] + " ";
        }
        return debugOutput.trim();
    }

    public Boolean isNotSet(Map<?, ?> value) {
        if (value == null || value.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isNotSet(String value) {
        if (value == null || value.isEmpty() || value.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isNotSet(File value) {
        if (value == null || value.toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}
