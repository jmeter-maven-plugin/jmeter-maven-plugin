/**
 *
 */
package com.lazerycode.jmeter.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class JmeterResults {

    private Counter globalCounter = new Counter();

    private Map<String, Counter> urisCounter = new HashMap<String, Counter>();

    private long startTimestamp;

    private long endTimestamp;

    private boolean finish;

    public void addValue(String uri, boolean success, long duration, long timestamp) {
        if (finish) {
            throw new IllegalAccessError("finish method have already call. It's not possible to add values.");
        }
        //  Global attributes
        globalCounter.incrementCount(success, duration);

        if (startTimestamp > timestamp || startTimestamp == 0) {
            startTimestamp = timestamp;
        }
        if (endTimestamp < timestamp) {
            endTimestamp = timestamp;
        }

        // Uri attributes
        addUriCounter(uri, success, duration);
    }

    public void finish() {
        finish = true;
        globalCounter.finish(getDurationTest());
        for (Counter counter : urisCounter.values()) {
            counter.finish(getDurationTest());
        }
    }

    private void addUriCounter(String uri, boolean success, long duration) {
        Counter uriCounter = urisCounter.get(uri);
        if (null == uriCounter) {
            uriCounter = new Counter();
            urisCounter.put(uri, uriCounter);
        }
        uriCounter.incrementCount(success, duration);
    }

    public Counter getGlobalCounter() {
        return globalCounter;
    }

    public Map<String, Counter> getUrisCounter() {
        return urisCounter;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public long getDurationTest() {
        return TimeUnit.MILLISECONDS.toSeconds(endTimestamp - startTimestamp);
    }

    public String getDurationTestFormat() {
        long durationTest = getDurationTest();
        return new StringBuilder(String.valueOf(TimeUnit.SECONDS.toHours(durationTest))).append("h ")
                .append(TimeUnit.SECONDS.toMinutes(durationTest % 3600)).append("m ")
                .append(TimeUnit.SECONDS.toSeconds(durationTest % 60)).append("s").toString();
    }

}
