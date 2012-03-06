package com.lazerycode.jmeter;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.testelement.TestListener;

public class JMeterTestListener extends AbstractListenerElement implements TestListener {

    private boolean isTestStillRunning = true;

    @Override
    public void testStarted() {
        this.isTestStillRunning = true;
    }

    @Override
    public void testStarted(String string) {
        testStarted();
    }

    @Override
    public void testEnded() {
        this.isTestStillRunning = false;
    }

    @Override
    public void testEnded(String string) {
        testEnded();
    }

    @Override
    public void testIterationStart(LoopIterationEvent lie) {
    }

    public boolean isTestStillRunning(){
        return this.isTestStillRunning;
    }
}