package com.lazerycode.jmeter.configuration;

import com.lazerycode.jmeter.properties.ConfigurationFiles;
import com.lazerycode.jmeter.properties.PropertiesMapping;

import java.util.EnumMap;
import java.util.Map;

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
@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class RemoteConfiguration {

    private boolean startServersBeforeTests = false;
    private boolean stopServersAfterTests = false;
    private boolean startAndStopServersForEachTest = false;
    private String serverList = "";
    private Map<ConfigurationFiles, PropertiesMapping> propertiesMap = new EnumMap<>(ConfigurationFiles.class);

    /**
     * @return Stop remote servers when the test finishes
     */
    public boolean isStopServersAfterTests() {
        return stopServersAfterTests;
    }

    /**
     * @return Start all remote servers as defined in jmeter.properties when the test starts
     */
    public boolean isStartServersBeforeTests() {
        return startServersBeforeTests;
    }

    /**
     * @return Comma separated list of servers to serverList when starting tests
     */
    public String getServerList() {
        return serverList;
    }

    /**
     * @return Remote serverList and stopServersAfterTests for every test, or once for the entire test suite of tests.
     */
    public boolean isStartAndStopServersForEachTest() {
        return startAndStopServersForEachTest;
    }

    /**
     * @return propertycontainers with information specified in the various property sources.
     */
    public Map<ConfigurationFiles, PropertiesMapping> getPropertiesMap() {
        return propertiesMap;
    }

    public void setPropertiesMap(Map<ConfigurationFiles, PropertiesMapping> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    /**
     * Remote configuration details formatted for command line output.
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("RemoteConfiguration [StartServer=%s, StopServers=%s, StartAndStopServerForEachTest=%s, ServerList=%s]",
                startServersBeforeTests, stopServersAfterTests, startAndStopServersForEachTest, serverList);
    }
}
