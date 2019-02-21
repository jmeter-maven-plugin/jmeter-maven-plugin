package com.lazerycode.jmeter.mojo;

import com.lazerycode.jmeter.configuration.ProxyConfiguration;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.Generated;

import static org.assertj.core.api.Assertions.assertThat;

@Generated(value = "org.junit-tools-1.0.2")
public class AbstractJMeterMojoTest {

    private static final String HOST = "host";
    private static final int PORT = 0;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NON_HOST = "h1,h2";

    private AbstractJMeterMojo createTestSubject() {
        return new AbstractJMeterMojo() {
            @Override
            protected void doExecute() throws MojoExecutionException, MojoFailureException {
            }
        };
    }

    private Proxy createTestProxy() {
        Proxy testProxy = new Proxy();
        testProxy.setHost(HOST);
        testProxy.setPort(PORT);
        testProxy.setUsername(USERNAME);
        testProxy.setPassword(PASSWORD);
        testProxy.setNonProxyHosts(NON_HOST);
        return testProxy;
    }

    @Test
    public void testMavenProxy() throws Exception {

        AbstractJMeterMojo testSubject = createTestSubject();
        Proxy testProxy = createTestProxy();

        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settings.getActiveProxy()).thenReturn(testProxy);

        testSubject.settings = settings;
        testSubject.useMavenProxy = true;

        testSubject.execute();

        assertThat(testSubject.proxyConfig).isNotNull();
        assertThat(testSubject.proxyConfig.getHost()).isEqualTo(HOST);
        assertThat(testSubject.proxyConfig.getPort()).isEqualTo(Integer.toString(PORT));
        assertThat(testSubject.proxyConfig.getUsername()).isEqualTo(USERNAME);
        assertThat(testSubject.proxyConfig.getPassword()).isEqualTo(PASSWORD);
        assertThat(testSubject.proxyConfig.getHostExclusions()).isEqualTo(NON_HOST);
    }

    @Test
    public void ifSettingsAreNullProxyConfigIsNotSet() throws Exception {

        AbstractJMeterMojo testSubject = createTestSubject();
        Proxy testProxy = createTestProxy();

        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settings.getActiveProxy()).thenReturn(testProxy);

        testSubject.settings = null;
        testSubject.useMavenProxy = true;

        testSubject.execute();

        assertThat(testSubject.proxyConfig).isNull();
    }

    @Test
    public void testSpecificProxyPriority() throws Exception {

        AbstractJMeterMojo testSubject = createTestSubject();
        Proxy testProxy = createTestProxy();
        ProxyConfiguration testProxyConfig = new ProxyConfiguration();

        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settings.getActiveProxy()).thenReturn(testProxy);

        testSubject.settings = settings;
        testSubject.useMavenProxy = true;
        testSubject.proxyConfig = testProxyConfig;

        testSubject.execute();

        assertThat(testSubject.proxyConfig).isNotNull();
        assertThat(testSubject.proxyConfig).isEqualTo(testProxyConfig);
    }

    @Test
    public void testNoMavenProxy() throws Exception {

        AbstractJMeterMojo testSubject = createTestSubject();
        Proxy testProxy = createTestProxy();

        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settings.getActiveProxy()).thenReturn(testProxy);

        testSubject.settings = settings;
        testSubject.useMavenProxy = false;

        testSubject.execute();

        assertThat(testSubject.proxyConfig).isNull();
    }
}