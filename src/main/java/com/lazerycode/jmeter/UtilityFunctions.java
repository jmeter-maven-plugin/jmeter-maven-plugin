package com.lazerycode.jmeter;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Series of useful utility functions to make life easy
 *
 * @author Mark Collin
 */
@SuppressWarnings("RedundantIfStatement")
public final class UtilityFunctions {

	/**
	 * Make constructor private as this is a non-instantiable helper classes
	 */
	private UtilityFunctions() {
	}

	/**
	 * Build a human readable command line from the arguments set by the plugin
	 *
	 * @param arguments Array of String
	 * @return String
	 */
	public static String humanReadableCommandLineOutput(List<String> arguments) {
		String debugOutput = "";
		for (String argument : arguments) {
			debugOutput += argument + " ";
		}
		return debugOutput.trim();
	}

	/**
	 * Utility function to strip carriage returns out of a String
	 *
	 * @param value String
	 * @return String
	 */
	public static String stripCarriageReturns(String value) {
		return value.replaceAll("[\n\r]", "");
	}

	/**
	 * Utility function to check if a Map is defined and not empty
	 *
	 * @param value Map
	 * @return boolean
	 */
	public static Boolean isNotSet(Map<?, ?> value) {
		return null == value || value.isEmpty();
	}

	/**
	 * Utility function to check if a String is defined and not empty
	 *
	 * @param value String
	 * @return boolean
	 */
	public static Boolean isNotSet(String value) {
		return null == value || value.isEmpty() || value.trim().length() == 0;
	}

	/**
	 * Utility function to check if a String is defined and not empty
	 *
	 * @param value String
	 * @return boolean
	 */
	public static Boolean isSet(String value) {
		return !isNotSet(value);
	}

	/**
	 * Utility function to check if File is defined and not empty
	 *
	 * @param value File
	 * @return boolean
	 */
	public static Boolean isNotSet(File value) {
		return null == value || value.toString().isEmpty() || value.toString().trim().length() == 0;
	}
}
