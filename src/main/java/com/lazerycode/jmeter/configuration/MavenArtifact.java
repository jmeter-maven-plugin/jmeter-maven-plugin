package com.lazerycode.jmeter.configuration;

import org.apache.maven.plugins.annotations.Parameter;

public class MavenArtifact {

	/**
	 * The {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>} of the artifact to resolve.
	 */
	@Parameter(required = true)
	private String dependency;

	public MavenArtifact(String dependency) {
		this.dependency = dependency;
	}

	public String getDependency() {
		return dependency;
	}

	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
}