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
 *          <xms>true</xms>
 *          <xmx>true</xmx>
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
	private List<String> arguments = new ArrayList<String>();

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
}
