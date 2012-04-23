package com.lazerycode.jmeter.configuration;

/**
 * This is used by the TestManager to configure remote start and stop settings for each test run.
 * <p>
 * Configuration in pom.xml:
 * <p>
 * <pre>
 * {@code
 * <remoteConfig>
 *     <stop></stop>
 *     <startAll></startAll>
 *     <start></start>
 *     <startAndStopOnce></startAndStopOnce>
 * </remoteConfig>
 * }
 * </pre>
 *
 * @author Arne Franken
 */
public class RemoteConfiguration {

    private boolean stop = false;
    private boolean startAll = false;
    private String start = null;
    private boolean startAndStopOnce = true;

    /**
     * @return Stop remote servers when the test finishes
     */
    public boolean isStop() {
        return stop;
    }

    /**
     * Stop remote servers when the test finishes
     * Default: {@link false Boolean.FALSE}
     * @param stop
     */
    public void setStop(boolean stop) {
        this.stop = stop;
    }

    /**
     * @return Start all remote servers as defined in jmeter.properties when the test starts
     */
    public boolean isStartAll() {
        return startAll;
    }

    /**
     * Start all remote servers as defined in jmeter.properties when the test starts
     * Default: {@link false Boolean.FALSE}
     * @param startAll
     */
    public void setStartAll(boolean startAll) {
        this.startAll = startAll;
    }

    /**
     * @return Comma separated list of servers to start when starting tests
     */
    public String getStart() {
        return start;
    }

    /**
     * Comma separated list of servers to start when starting tests
     * @param start
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * @return Remote start and stop for every test, or once for the entire test suite of tests.
     */
    public boolean isStartAndStopOnce() {
        return startAndStopOnce;
    }

    /**
     * Remote start and stop for every test, or once for the entire test suite of tests.
     * Default: {@link true Boolean.TRUE} (once for the entire suite of tests)
     * @param startAndStopOnce
     */
    public void setStartAndStopOnce(boolean startAndStopOnce) {
        this.startAndStopOnce = startAndStopOnce;
    }

    @Override
    public String toString() {
        //this method is used by maven when debug output is enabled
        return "RemoteConfiguration [ "+"Start="+ getStart()+", Stop="+ isStop()+
                ", StartAndStopOnce="+ isStartAndStopOnce()+", StartAll="+ isStartAll()+" ]";
    }
}
