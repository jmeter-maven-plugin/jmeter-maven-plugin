package com.lazerycode.jmeter;

import com.lazerycode.jmeter.threadhandling.JMeterThreads;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Series of useful utility functions to make life easy
 *
 * @author Mark Collin
 */
@SuppressWarnings("RedundantIfStatement")
public class UtilityFunctions {

    /**
     * private constructor for non-instantiable helper classes
     */
    private UtilityFunctions() {}

    public static String humanReadableCommandLineOutput(String[] arguments) {
        String debugOutput = "";
        for (String argument : arguments) {
            debugOutput += argument + " ";
        }
        return debugOutput.trim();
    }
    
    public static String stripCarriageReturns(String value) {
        return value.replaceAll("[\n\r]", "");
    }

    public static Boolean isNotSet(Map<?, ?> value) {
        if (null == value || value.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isNotSet(String value) {
        if (null == value || value.isEmpty() || value.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isNotSet(File value) {
        if (null == value || value.toString().isEmpty() || value.toString().trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a list of thread names to wait upon
     *
     * @param ofTypeGUI look for GUI threads (false implies non-GUI threads)
     * @return the list of thread names to wait upon
     */
    public static List<String> getThreadNames(boolean ofTypeGUI) {
        List<String> threadNames = new ArrayList<String>();
        for (JMeterThreads thread : JMeterThreads.values()) {
            if (thread.isGUIThread() == ofTypeGUI) {
                threadNames.add(thread.getThreadName());
            }
        }
        return threadNames;
    }
}
