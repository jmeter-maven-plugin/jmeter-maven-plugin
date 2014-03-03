/**
 *
 */
package com.lazerycode.jmeter.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Counter {

    private List<Long> durations = new ArrayList<Long>();

    private long successCount;

    private long errorCount;

    private long max;

    private long min;

    private long totalTime;

    private double durationTest;

    private boolean finish;

    public void incrementCount(boolean success, long duration) {
        if (finish) {
            throw new IllegalAccessError("finish method have already call. It's not possible to increment count.");
        }
        if (successCount == 0 && errorCount == 0) {
            max = min = duration;
        } else if (min > duration) {
            min = duration;
        } else if (max < duration) {
            max = duration;
        }

        if (success) {
            successCount++;
        } else {
            errorCount++;
        }
        durations.add(duration);
        totalTime += duration;
    }

    public void finish(double durationTest) {
        finish = true;
        this.durationTest = durationTest;
        Collections.sort(durations);
    }

    public long getSuccessCount() {
        return successCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public double getSuccessCountPercent() {
        return round(((double) successCount / getTotalCount()) * 100.0);
    }

    public double getErrorCountPercent() {
        return round(((double) errorCount / getTotalCount()) * 100.0);
    }

    public long getTotalCount() {
        return successCount + errorCount;
    }

    public long getMax() {
        return max;
    }

    public long getMin() {
        return min;
    }

    public double getThroughput() {
        return round(getTotalCount() / durationTest);
    }

    public double getAverage() {
        return round(totalTime / getTotalCount());
    }

    public long getPercentile(double percentile) {
        if (percentile <= 0 || percentile > 100) {
            throw new IllegalArgumentException("percentile must be between 0 and 100 : " + percentile);
        }
        return durations.get((int) Math.max(((durations.size() * percentile / 100) - 1), 0));
    }

    private double round(double number) {
        return Math.round(number * 10.0) / 10.0;
    }
}
