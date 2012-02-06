package com.lazerycode.jmeter;

/**
 * Value class that contains all proxy related configuration
 */
public class ProxyConfig {

    /**
     * Regex of hosts that will not be proxied
     */
    private String hostExclusions = "";

    /**
     * HTTP proxy host name.
     */
    private String host = "";

    /**
     * HTTP proxy port.
     */
    private Integer port = 80;

    /**
     * HTTP proxy username.
     */
    private String username;

    /**
     * HTTP proxy user password.
     */
    private String password;

    public String getHostExclusions() {
        return hostExclusions == null ? "" : hostExclusions;
    }

    public void setHostExclusions(String hostExclusions) {
        this.hostExclusions = hostExclusions;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port == null ? 80 : port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username == null ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ProxyConfig: [" +" Host="+ getHost()+", Port="+ getPort()+
                ", Username="+ getUsername()+", Password="+ getPassword()+", HostExclusions="+ getHostExclusions()+" ]";
    }
}
