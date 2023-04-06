package com.lazerycode.jmeter.configuration;

import java.util.TreeSet;

import static com.lazerycode.jmeter.configuration.JMeterCommandLineArguments.*;
import static com.lazerycode.jmeter.utility.UtilityFunctions.isNotSet;
import static com.lazerycode.jmeter.utility.UtilityFunctions.isSet;

/**
 * Is used for configuration of all proxy related configuration.
 * <br>
 * Configuration in pom.xml:
 * <br>
 * <pre>
 * {@code
 * <proxyConfig>
 *     <host></host>
 *     <port></port>
 *     <username></username>
 *     <password></password>
 *     <hostExclusions></hostExclusions>
 * </proxyConfig>
 * }
 * </pre>
 *
 * @author Arne Franken
 */
public class ProxyConfiguration {

	private String hostExclusions = null;
	private String host = null;
	private Integer port = 80;
	private String username = null;
	private String password = null;

	/**
	 * @return HTTP proxy host name
	 */
	public String getHost() {
		return host;
	}

	/**
	 * HTTP proxy host name
	 *
	 * @param host String
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return HTTP proxy port as long as a host is set
	 */
	public String getPort() {
		if (isNotSet(host)) return null;
		return port.toString();
	}

	/**
	 * HTTP proxy port
	 * Default: 80
	 *
	 * @param port Integer
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return HTTP proxy username as long as a host is set
	 */
	public String getUsername() {
		if (isNotSet(host)) return null;
		return username;
	}

	/**
	 * HTTP proxy username
	 *
	 * @param username String
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return HTTP proxy user password as long as a host is set
	 */
	public String getPassword() {
		if (isNotSet(host)) return null;
		return password;
	}

	/**
	 * HTTP proxy user password
	 *
	 * @param password String
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Regex of hosts that will not be proxied as long as a host is set
	 */
	public String getHostExclusions() {
		if (isNotSet(host)) return null;
		return hostExclusions;
	}

	/**
	 * Regex of hosts that will not be proxied
	 *
	 * @param hostExclusions String
	 */
	public void setHostExclusions(String hostExclusions) {
		this.hostExclusions = hostExclusions;
	}

	/**
	 * Proxy details formatted for command line output.
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		String proxyDetails = "Proxy server is not being used.";
		if (isSet(host)) {
			proxyDetails = "Proxy Details:\n\nHost: " + host + ":" + port + "\n";
			if (isSet(username)) proxyDetails += "Username: " + username + "\n";
			if (isSet(password)) proxyDetails += "Password: " + password + "\n";
			if (isSet(hostExclusions)) proxyDetails += "Host Exclusions: " + hostExclusions + "\n";
		}
		return proxyDetails + "\n";
	}

	public TreeSet<JMeterCommandLineArguments> setCommandLineArguments(TreeSet<JMeterCommandLineArguments> argumentList){

		if (isSet(this.getHost())) {
			argumentList.add(PROXY_HOST);
			argumentList.add(PROXY_PORT);
		}
		if (isSet(this.getUsername())) {
			argumentList.add(PROXY_USERNAME);
		}
		if (isSet(this.getPassword())) {
			argumentList.add(PROXY_PASSWORD);
		}
		if (isSet(this.getHostExclusions())) {
			argumentList.add(NONPROXY_HOSTS);
		}

		return argumentList;
	}
}
