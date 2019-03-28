package com.lazerycode.jmeter.configuration;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyConfigurationTest {

    @Test
    public void checkSetHost(){
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");

        assertThat(proxyConfiguration.getHost()).isEqualTo("http://10.10.50.43");
    }

    @Test
    public void checkSetPort(){
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPort(9090);

        assertThat(proxyConfiguration.getPort()).isEqualTo("9090");
    }

    @Test
    public void getPortReturnsNullIfHostNotSet(){
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setPort(9090);

        assertThat(proxyConfiguration.getPort()).isNull();
    }

    @Test
    public void checkSetUsername(){
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setUsername("Fred");

        assertThat(proxyConfiguration.getUsername()).isEqualTo("Fred");
    }

    @Test
    public void getUsernameReturnsNullIfHostNotSet(){
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setUsername("Fred");

        assertThat(proxyConfiguration.getPort()).isNull();
    }

    @Test
    public void checkSetHostExclusions(){
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setHostExclusions("server1");

        assertThat(proxyConfiguration.getHostExclusions()).isEqualTo("server1");
    }

    @Test
    public void getHostExclusionsReturnsNullIfHostNotSet(){
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHostExclusions("server1");

        assertThat(proxyConfiguration.getPort()).isNull();
    }

    @Test
    public void checkSetPassword(){
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPassword("monday");

        assertThat(proxyConfiguration.getPassword()).isEqualTo("monday");
    }

    @Test
    public void getPasswordReturnsNullIfHostNotSet(){
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setPassword("Fred");

        assertThat(proxyConfiguration.getPort()).isNull();
    }
    
    @Test
    public void checkToStringWhenHostSet() {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");

        assertThat(proxyConfiguration.toString()).isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:80\n\n");
    }

    @Test
    public void checkToStringWhenPortSet() {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setPort(8080);

        assertThat(proxyConfiguration.toString()).isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\n\n");
    }

    @Test
    public void checkToStringWhenHostExclusionsSet() {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setHostExclusions("some host");

        assertThat(proxyConfiguration.toString()).isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:80\nHost Exclusions: some host\n\n");
    }

    @Test
    public void checkToStringWhenWhenUsernameAndPasswordSet() {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost("http://10.10.50.43");
        proxyConfiguration.setUsername("Fred");
        proxyConfiguration.setPassword("monday");

        assertThat(proxyConfiguration.toString()).isEqualTo("Proxy Details:\n\nHost: http://10.10.50.43:80\nUsername: Fred\nPassword: monday\n\n");
    }

    @Test
    public void checkToStringReportsProxyServerNotUsedIfHostNotSet() {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();

        assertThat(proxyConfiguration.toString()).isEqualTo("Proxy server is not being used.\n");
    }
}