package org.apache.jmeter;

import java.io.File;
import java.util.Map;

/**
 * Series of useful utilities to make life easy
 *
 * @author Mark Collin
 */
public class Utilities {

    public String humanReadableCommandLineOutput(String[] arguments) {
        String debugOutput = "";
        for (int i = 0; i < arguments.length; i++) {
            debugOutput += arguments[i] + " ";
        }
        return debugOutput;
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
