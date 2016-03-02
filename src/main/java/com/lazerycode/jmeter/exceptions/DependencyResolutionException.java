package com.lazerycode.jmeter.exceptions;

import org.apache.maven.plugin.MojoExecutionException;

public class DependencyResolutionException extends MojoExecutionException {
	public DependencyResolutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public DependencyResolutionException(String message) {
		super(message);
	}
}
