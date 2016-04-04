package com.lazerycode.jmeter.mojo;

import javax.annotation.Generated;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.junit.Test;
import org.mockito.Mockito;

import com.lazerycode.jmeter.configuration.ProxyConfiguration;

import static org.junit.Assert.*;

@Generated(value = "org.junit-tools-1.0.2")
public class AbstractJMeterMojoTest {
	
	private static final String host = "host";
	private static final int port = 0;
	private static final String username = "username";
	private static final String password = "password";
	private static final String nonHost = "h1,h2";

	private AbstractJMeterMojo createTestSubject() {
		return new AbstractJMeterMojo() {
			@Override
			protected void doExecute() throws MojoExecutionException, MojoFailureException {
			}
		};
	}
	
	private Proxy createTestProxy() {
		Proxy testProxy = new Proxy();
		testProxy.setHost(host);
		testProxy.setPort(port);
		testProxy.setUsername(username);
		testProxy.setPassword(password);
		testProxy.setNonProxyHosts(nonHost);
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
		
		assertNotNull(testSubject.proxyConfig);
		assertEquals(host, testSubject.proxyConfig.getHost());
		assertEquals(Integer.toString(port), testSubject.proxyConfig.getPort());
		assertEquals(username, testSubject.proxyConfig.getUsername());
		assertEquals(password, testSubject.proxyConfig.getPassword());
		assertEquals(nonHost, testSubject.proxyConfig.getHostExclusions());
		
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
		
		assertNotNull(testSubject.proxyConfig);
		assertEquals(testProxyConfig, testSubject.proxyConfig);
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
		
		assertNull(testSubject.proxyConfig);
	}
}