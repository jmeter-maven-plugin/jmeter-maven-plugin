package com.lazerycode.jmeter.configuration;

/**
 * Used to detect which artifacts are JMeter plugins
 * <p/>
 * Configuration in pom.xml:
 * <p/>
 * <pre>
 * {@code
 * 	<jmeterPlugins>
 * 		<plugin>
 *     		<groupId></groupId>
 *     		<artifactId></artifactId>
 *     		<typeId></typeId> // Optional
 *     </plugin>
 * 	</jmeterPlugins>
 * }
 * </pre>
 *
 * @author Mark Collin
 */
public class JMeterPlugins {

	private String groupId = null;
	private String artifactId = null;
	private String typeId = null;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	public String getType() {
		return typeId;
	}

	public void setType(String typeId) {
		this.typeId = typeId;
	}

	@Override
	public String toString() {
		return groupId + ":" + artifactId+":" + typeId;
	}

}
