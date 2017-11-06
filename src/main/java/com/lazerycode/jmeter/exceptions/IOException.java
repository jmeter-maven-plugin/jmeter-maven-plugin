package com.lazerycode.jmeter.exceptions;

import org.apache.maven.plugin.MojoExecutionException;

public class IOException extends MojoExecutionException {
	/**
     * 
     */
    private static final long serialVersionUID = -459038205882201474L;

    public IOException(String message, Throwable cause) {
		super(message, cause);
	}
}
