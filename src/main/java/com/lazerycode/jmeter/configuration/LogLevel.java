package com.lazerycode.jmeter.configuration;

/**
 * Enum containing the log levels supported by JMeter
 */
public enum LogLevel {
	FATAL_ERROR,
	ERROR,
	WARN,
	INFO,
	DEBUG;

	@Override
	public String toString() {
		return super.toString().toUpperCase();
	}
}
