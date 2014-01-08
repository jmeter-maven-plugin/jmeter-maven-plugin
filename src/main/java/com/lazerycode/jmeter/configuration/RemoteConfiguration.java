package com.lazerycode.jmeter.configuration;

import java.util.Map;

import com.lazerycode.jmeter.properties.JMeterPropertiesFiles;
import com.lazerycode.jmeter.properties.PropertyContainer;

/**
 * This is used by the TestManager to configure remote serverList and stopServersAfterTests settings for each test run.
 * <p/>
 * Configuration in pom.xml:
 * <p/>
 * <pre>
 * {@code
 * <remoteConfig>
 *     <stopServersAfterTests></stopServersAfterTests>
 *     <startServersBeforeTests></startServersBeforeTests>
 *     <serverList></serverList>
 *     <startAndStopServersForEachTest></startAndStopServersForEachTest>
 * </remoteConfig>
 * }
 * </pre>
 *
 * @author Arne Franken
 */
public class RemoteConfiguration {

    private boolean startServersBeforeTests = false;
    private boolean stopServersAfterTests = false;
    private boolean startAndStopServersForEachTest = false;
	private String serverList = null;
	private Map<JMeterPropertiesFiles, PropertyContainer> masterPropertiesMap = null;



	/**
     * @return Stop remote servers when the test finishes
     */
    public boolean isStopServersAfterTests() {
        return stopServersAfterTests;
    }

    /**
     * Stop remote servers when the test finishes
     * Default: {@link false Boolean.FALSE}
     *
     * @param stopServersAfterTests boolean
     */
    public void setStopServersAfterTests(boolean stopServersAfterTests) {
        this.stopServersAfterTests = stopServersAfterTests;
    }

    /**
     * @return Start all remote servers as defined in jmeter.properties when the test starts
     */
    public boolean isStartServersBeforeTests() {
        return startServersBeforeTests;
    }



	/**
     * Start all remote servers as defined in jmeter.properties when the test starts
     * Default: {@link false Boolean.FALSE}
     *
     * @param startServersBeforeTests boolean
     */
    public void setStartServersBeforeTests(boolean startServersBeforeTests) {
        this.startServersBeforeTests = startServersBeforeTests;
    }

    /**
     * @return Comma separated list of servers to serverList when starting tests
     */
    public String getServerList() {
        return serverList;
    }

    /**
     * Comma separated list of servers to serverList when starting tests
     *
     * @param serverList String
     */
    public void setServerList(String serverList) {
        this.serverList = serverList;
    }

    /**
     * @return Remote serverList and stopServersAfterTests for every test, or once for the entire test suite of tests.
     */
    public boolean isStartAndStopServersForEachTest() {
        return startAndStopServersForEachTest;
    }

    /**
     * Remote serverList and stopServersAfterTests for every test, or once for the entire test suite of tests.
     * Default: {@link true Boolean.TRUE} (once for the entire suite of tests)
     *
     * @param startAndStopServersForEachTest boolean
     */
    public void setStartAndStopServersForEachTest(boolean startAndStopServersForEachTest) {
        this.startAndStopServersForEachTest = startAndStopServersForEachTest;
    }

    /**
     * Remote configuration details formatted for command line output.
     *
     * @return String
     */
    @Override
    public String toString() {
        return "RemoteConfiguration [ " + "Start=" + getServerList() + ", Stop=" + isStopServersAfterTests() + ", StartAndStopOnce=" + isStartAndStopServersForEachTest() + ", StartAll=" + isStartServersBeforeTests() + " ]";
    }
    
    
    /**
     *  
     * @return propertycontainers with information specified in the various property sources. 
     */
    public Map<JMeterPropertiesFiles, PropertyContainer> getMasterPropertiesMap() {
		return masterPropertiesMap;
	}

	public void setMasterPropertiesMap(
			Map<JMeterPropertiesFiles, PropertyContainer> masterPropertiesMap) {
		this.masterPropertiesMap = masterPropertiesMap;
	}
}
