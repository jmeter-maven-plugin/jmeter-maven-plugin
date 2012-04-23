package com.lazerycode.jmeter.configuration;

/**
 * Is used for configuration of all proxy related configuration.
 * <p>
 * Configuration in pom.xml:
 * <p>
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
        return hostExclusions;
    }

    /**
     * Regex of hosts that will not be proxied
     * @param hostExclusions
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
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return HTTP proxy port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * HTTP proxy port
     * Default: 80
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return HTTP proxy username
     */
    public String getUsername() {
        return username;
    }

    /**
     * HTTP proxy username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return HTTP proxy user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * HTTP proxy user password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        //this method is used by maven when debug output is enabled
        String proxyDetails = "Proxy server is not being used.";
        if (null != this.host) {
            proxyDetails = "Proxy Details:\n\nHost: " + this.host + ":" + this.port + "\n";
            if (null != this.username) {
                proxyDetails += "Username:" + this.username + "\n";
            }
            if (null != this.password) {
                proxyDetails += "Password:" + this.password + "\n";
            }
            if (null != this.hostExclusions) {
                proxyDetails += "Host Exclusions:" + this.hostExclusions + "\n";
            }
        }
        return proxyDetails + "\n";
    }
}
