package com.lazerycode.jmeter.configuration;

/**
 * Is used for configuration of all proxy related configuration.
 * <p/>
 * Configuration in pom.xml:
 * <p/>
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
	 * @return Regex of hosts that will not be proxied
	 */
	public String getHostExclusions() {
		if (null == host || host.isEmpty()) return null;
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
	 * @return HTTP proxy port
	 */
	public String getPort() {
		if (null == host || host.isEmpty()) return null;
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
	 * @return HTTP proxy username
	 */
	public String getUsername() {
		if (null == host || host.isEmpty()) return null;
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
	 * @return HTTP proxy user password
	 */
	public String getPassword() {
		if (null == host || host.isEmpty()) return null;
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
	 * Proxy details formatted for command line output.
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		String proxyDetails = "Proxy server is not being used.";
		if (null != host) {
			proxyDetails = "Proxy Details:\n\nHost: " + host + ":" + port + "\n";
			if (null != username) {
				proxyDetails += "Username: " + username + "\n";
			}
			if (null != password) {
				proxyDetails += "Password: " + password + "\n";
			}
			if (null != hostExclusions) {
				proxyDetails += "Host Exclusions: " + hostExclusions + "\n";
			}
		}
		return proxyDetails + "\n";
	}
}
