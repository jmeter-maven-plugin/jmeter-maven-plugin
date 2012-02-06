package com.lazerycode.jmeter;

/**
 * Value class that contains all remote related configuration
 */
public class RemoteConfig {

    /**
     * Stop remote servers when the test finishes
     */
    private boolean stop = false;

    /**
     * Start all remote servers as defined in jmeter.properties when the test starts
     */
    private boolean startAll = false;

    /**
     * Comma separated list of servers to start when starting tests
     */
    private String start = "";

    /**
     * Remote start and stop for every test, or once for the entire test suite of tests.
     * (Defaults to once for the entire suite of tests)
     */
    private boolean startAndStopOnce = true;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStartAll() {
        return startAll;
    }

    public void setStartAll(boolean startAll) {
        this.startAll = startAll;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public boolean isStartAndStopOnce() {
        return startAndStopOnce;
    }

    public void setStartAndStopOnce(boolean startAndStopOnce) {
        this.startAndStopOnce = startAndStopOnce;
    }

    @Override
    public String toString() {
        return "RemoteConfig [ "+"Start="+ getStart()+", Stop="+ isStop()+
                ", StartAndStopOnce="+ isStartAndStopOnce()+", StartAll="+ isStartAll()+" ]";
    }
}
