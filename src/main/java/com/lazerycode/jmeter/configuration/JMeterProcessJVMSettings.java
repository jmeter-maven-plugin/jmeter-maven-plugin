package com.lazerycode.jmeter.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to detect which artifacts are JMeter plugins
 * <p/>
 * Configuration in pom.xml:
 * <p/>
 * <pre>
 * {@code
 * 	<configuration>
 *      <jMeterProcessJVMSettings>
 *          <javaRuntime>{env.JAVA_HOME}/bin/java</javaRuntime>
 *          <xms>512</xms>
 *          <xmx>1024</xmx>
 *          <arguments>
 *              <argument>foo</argument>
 *          </arguments>
 *      </jMeterProcessJVMSettings>
 *  </configuration>
 * }
 * </pre>
 *
 * @author Mark Collin
 */
public class JMeterProcessJVMSettings {

	private int xms = 512;
	private int xmx = 512;
	private String java = "java";
	private List<String> arguments = new ArrayList<>();

	/**
	 * Empty constructor
	 */
	public JMeterProcessJVMSettings() {
	    super();
	}
	
	/**
	 * Copy constructor
	 * @param source {@link JMeterProcessJVMSettings}
	 */
	public JMeterProcessJVMSettings(JMeterProcessJVMSettings source) {
	    this.xmx = source.xms;
	    this.xms = source.xms;
	    this.java = source.java;
	    this.arguments = new ArrayList<>();
        this.arguments.addAll(source.arguments);
	}
	public int getXms() {
		return xms;
	}

	public void setXms(int xms) {
		this.xms = xms;
	}

	public int getXmx() {
		return xmx;
	}

	public void setXmx(int xmx) {
		this.xmx = xmx;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public void setJavaRuntime(String java) {
		this.java = java;
	}

	public String getJavaRuntime() {
		return this.java;
	}
}
