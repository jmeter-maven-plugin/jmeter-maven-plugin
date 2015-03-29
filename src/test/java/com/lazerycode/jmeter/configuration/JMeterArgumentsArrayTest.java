package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.UtilityFunctions;
import org.apache.maven.plugin.MojoExecutionException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class JMeterArgumentsArrayTest {

	private final URL testFile = this.getClass().getResource("/test.jmx");
	private final String timestamp = new DateTime().year().getAsText();
	private final boolean disableGUI = true;
	private final boolean enableGUI = false;
	private String testFilePath;

	@Before
	public void setTestFileAbsolutePath() throws URISyntaxException {
		testFilePath = new File(this.testFile.toURI()).getAbsolutePath();
	}

	@Test(expected = MojoExecutionException.class)
	public void noTestSpecified() throws MojoExecutionException {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.buildArgumentsArray();
	}

	@Test(expected = MojoExecutionException.class)
	public void jMeterHomeEmpty() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "");
		testArgs.setTestFile(new File(this.testFile.toURI()));
		testArgs.buildArgumentsArray();
	}

	@Test(expected = MojoExecutionException.class)
	public void jMeterHomeNull() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, null);
		testArgs.setTestFile(new File(this.testFile.toURI()));
		testArgs.buildArgumentsArray();
	}

	@Test
	public void validateDefaultCommandLineOutputWithGUIDisabled() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		assertThat(testArgs.getResultsLogFileName(),
				is(not(equalTo(""))));
		assertThat(testArgs.getResultsLogFileName(),
				is(not(equalTo(null))));
		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/")));
	}

	@Test
	public void validateDefaultCommandLineOutputWithGUIEnabled() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(enableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		assertThat(testArgs.getResultsLogFileName(),
				is(equalTo(null)));
		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-d target/jmeter/")));
	}

	@Test
	public void validateJMeterCustomPropertiesFile() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		File testPropFile = new File("test.properties");
		testArgs.setACustomPropertiesFile(testPropFile);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -q " + testPropFile.getAbsolutePath())));
	}

	@Test
	public void validateSetRootLogLevel() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setLogRootOverride("DEBUG");

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -L DEBUG")));
	}

	@Test
	public void validateSetRootLogLevelWithWrongCase() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setLogRootOverride("info");

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -L INFO")));
	}

	@Test
	public void passingAEmptyRootLogLevelDoesNotSetAnything() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setLogRootOverride("");

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/")));
	}

	@Test
	public void passingANullRootLogLevelDoesNotSetAnything() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setLogRootOverride(null);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/")));
	}

	@Test
	public void validateJMeterSetProxyHost() throws Exception {
		ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
		proxyConfiguration.setHost("http://10.10.50.43");
		proxyConfiguration.setPort(8080);
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setProxyConfig(proxyConfiguration);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -H http://10.10.50.43 -P 8080")));
		assertThat(proxyConfiguration.toString(),
				is(equalTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\n\n")));
	}

	@Test
	public void validateJMeterSetProxyUsername() throws Exception {
		ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
		proxyConfiguration.setHost("http://10.10.50.43");
		proxyConfiguration.setPort(8080);
		proxyConfiguration.setUsername("god");
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setProxyConfig(proxyConfiguration);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -H http://10.10.50.43 -P 8080 -u god")));
		assertThat(proxyConfiguration.toString(),
				is(equalTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\nUsername: god\n\n")));
	}

	@Test
	public void validateProxyUsernameNotSetIfNoHost() throws Exception {
		ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
		proxyConfiguration.setUsername("god");
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setProxyConfig(proxyConfiguration);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/")));
		assertThat(proxyConfiguration.toString(),
				is(equalTo("Proxy server is not being used.\n")));
	}

	@Test
	public void validateJMeterSetProxyPassword() throws Exception {
		ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
		proxyConfiguration.setHost("http://10.10.50.43");
		proxyConfiguration.setPort(8080);
		proxyConfiguration.setPassword("changeme");
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setProxyConfig(proxyConfiguration);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -H http://10.10.50.43 -P 8080 -a changeme")));
		assertThat(proxyConfiguration.toString(),
				is(equalTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\nPassword: changeme\n\n")));
	}

	@Test
	public void validateProxyPasswordNotSetIfNoHost() throws Exception {
		ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
		proxyConfiguration.setPassword("changeme");
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setProxyConfig(proxyConfiguration);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/")));
		assertThat(proxyConfiguration.toString(),
				is(equalTo("Proxy server is not being used.\n")));
	}

	@Test
	public void validateSetNonProxyHosts() throws Exception {
		ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
		proxyConfiguration.setHost("http://10.10.50.43");
		proxyConfiguration.setPort(8080);
		proxyConfiguration.setHostExclusions("localhost|*.lazerycode.com");
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));
		testArgs.setProxyConfig(proxyConfiguration);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -H http://10.10.50.43 -P 8080 -N localhost|*.lazerycode.com")));
		assertThat(proxyConfiguration.toString(),
				is(equalTo("Proxy Details:\n\nHost: http://10.10.50.43:8080\nHost Exclusions: localhost|*.lazerycode.com\n\n")));
	}

	@Test
	public void validateProxyNonProxyHostsNotSetIfNoHost() throws Exception {
		ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
		proxyConfiguration.setHostExclusions("localhost|*.lazerycode.com");
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));
		testArgs.setProxyConfig(proxyConfiguration);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/")));
		assertThat(proxyConfiguration.toString(),
				is(equalTo("Proxy server is not being used.\n")));
	}

	@Test
	public void checkProxyDetailsReturnedWhenHostAndPortNotSet() {
		ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
		proxyConfiguration.setUsername("god");
		proxyConfiguration.setPassword("changeme");
		proxyConfiguration.setHostExclusions("localhost|*.lazerycode.com");

		assertThat(proxyConfiguration.toString(),
				is(equalTo("Proxy server is not being used.\n")));
	}

	@Test
	public void validateJMeterSetSocksProxyHost() throws Exception {
		SocksProxyConfiguration socksProxyConfiguration = new SocksProxyConfiguration();
		socksProxyConfiguration.setHost("http://10.10.50.43");
		socksProxyConfiguration.setPort(8080);
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setSocksProxyConfig(socksProxyConfiguration);

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -DsocksProxyHost http://10.10.50.43 -DsocksProxyPort 8080")));
		assertThat(socksProxyConfiguration.toString(),
				is(equalTo("SOCKS Proxy Details:\n\nHost: http://10.10.50.43:8080\n\n")));
	}

	@Test
	public void checkSocksProxyDetailsReturnedWhenHostAndPortNotSet() {
		SocksProxyConfiguration socksProxyConfiguration = new SocksProxyConfiguration();

		assertThat(socksProxyConfiguration.toString(),
				is(equalTo("SOCKS proxy server is not being used.\n")));
	}

	@Test
	public void validateSetRemoteStop() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setRemoteStop();

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -X")));
	}

	@Test
	public void validateSetRemoteStartAll() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setRemoteStart();

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -r")));
	}

	@Test
	public void validateSetRemoteStart() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setTestFile(new File(this.testFile.toURI()));

		testArgs.setRemoteStartServerList("server1, server2");

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + testArgs.getResultsLogFileName() + " -d target/jmeter/ -R server1, server2")));
	}

	@Test
	public void validateTestFileTimestampDisabled() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setResultsDirectory(File.separator + "tmp");
		testArgs.setResultsTimestamp(false);
		testArgs.setTestFile(new File(this.testFile.toURI()));

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + File.separator + "tmp" + File.separator + "test.jtl" + " -d target/jmeter/")));
	}

	@Test
	public void validateTestFileTimestampEnabledAndPrepended() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setResultsDirectory(File.separator + "tmp");
		testArgs.setResultsTimestamp(true);
		testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
		testArgs.setTestFile(new File(this.testFile.toURI()));

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + File.separator + "tmp" + File.separator + timestamp + "-test.jtl" + " -d target/jmeter/")));
	}

	@Test
	public void validateTestFileTimestampEnabledAndAppended() throws Exception {
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setResultsDirectory(File.separator + "tmp");
		testArgs.setResultsTimestamp(true);
		testArgs.appendTimestamp(true);
		testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
		testArgs.setTestFile(new File(this.testFile.toURI()));

		assertThat(UtilityFunctions.humanReadableCommandLineOutput(testArgs.buildArgumentsArray()),
				is(equalTo("-n -t " + testFilePath + " -l " + File.separator + "tmp" + File.separator + "test-" + timestamp + ".jtl" + " -d target/jmeter/")));
	}

	@Test
	public void resultsFileIsCSVFormat() throws Exception{
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setResultsDirectory(File.separator + "tmp");
		testArgs.setResultFileOutputFormatIsCSV(true);
		testArgs.setResultsTimestamp(true);
		testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
		testArgs.setTestFile(new File(this.testFile.toURI()));

		assertThat(testArgs.getResultsLogFileName(),
				is(equalTo(File.separator + "tmp" + File.separator + timestamp + "-test.csv")));
	}

	@Test
	public void resultsFileIsXMLFormat() throws Exception{
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setResultsDirectory(File.separator + "tmp");
		testArgs.setResultFileOutputFormatIsCSV(false);
		testArgs.setResultsTimestamp(true);
		testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
		testArgs.setTestFile(new File(this.testFile.toURI()));

		assertThat(testArgs.getResultsLogFileName(),
				is(equalTo(File.separator + "tmp" + File.separator + timestamp + "-test.jtl")));
	}

	@Test
	public void resultsFileDefaultsToXMLFormat() throws Exception{
		JMeterArgumentsArray testArgs = new JMeterArgumentsArray(disableGUI, "target/jmeter/");
		testArgs.setResultsDirectory(File.separator + "tmp");
		testArgs.setResultsTimestamp(true);
		testArgs.setResultsFileNameDateFormat(DateTimeFormat.forPattern("YYYY"));
		testArgs.setTestFile(new File(this.testFile.toURI()));



		assertThat(testArgs.getResultsLogFileName(),
				is(equalTo(File.separator + "tmp" + File.separator + timestamp + "-test.jtl")));
	}
}
