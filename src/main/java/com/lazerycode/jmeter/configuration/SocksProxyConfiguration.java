package com.lazerycode.jmeter.configuration;

import static com.lazerycode.jmeter.UtilityFunctions.isNotSet;
import static com.lazerycode.jmeter.UtilityFunctions.isSet;

/**
 * Is used for configuration of SOCKS proxy related configuration.
 * <p/>
 * Configuration in pom.xml:
 * <p/>
 * <pre>
 * {@code
 * <socksProxyConfig>
 *     <host></host>
 *     <port></port>
 * </socksProxyConfig>
 * }
 * </pre>
 *
 */
public class SocksProxyConfiguration {

	private String host = null;
	private Integer port = null;

	/**
	 * @return SOCKS proxy host name
	 */
	public String getHost() {
		return host;
	}

	/**
	 * SOCKS proxy host name
	 *
	 * @param host String
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return SOCKS proxy port as long as a host is set
	 */
	public String getPort() {
		if (isNotSet(host)) return null;
		return port.toString();
	}

	/**
	 * SOCKS proxy port
	 *
	 * @param port Integer
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * Proxy details formatted for command line output.
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		String proxyDetails = "SOCKS proxy server is not being used.";
		if (isSet(host)) {
			proxyDetails = "SOCKS proxy Details:\n\nHost: " + host + ":" + port + "\n";
		}
		return proxyDetails + "\n";
	}
}
