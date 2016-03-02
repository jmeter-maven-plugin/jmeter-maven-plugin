package com.lazerycode.jmeter.exceptions;

import org.apache.maven.plugin.MojoExecutionException;

public class IOException extends MojoExecutionException {
	public IOException(String message, Throwable cause) {
		super(message, cause);
	}
}
