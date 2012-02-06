package com.lazerycode.jmeter;

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
 */
public class ProxyConfig {

    private String hostExclusions = "";
    private String host = "";
    private Integer port = 80;
    private String username = "";
    private String password = "";

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
        return "ProxyConfig: [" +" Host="+ getHost()+", Port="+ getPort()+
                ", Username="+ getUsername()+", Password="+ getPassword()+", HostExclusions="+ getHostExclusions()+" ]";
    }
}
