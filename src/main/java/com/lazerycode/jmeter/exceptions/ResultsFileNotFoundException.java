package com.lazerycode.jmeter.exceptions;

import org.apache.maven.plugin.MojoExecutionException;

public class ResultsFileNotFoundException extends MojoExecutionException {
	public ResultsFileNotFoundException(String message) {
		super(message);
	}
}
