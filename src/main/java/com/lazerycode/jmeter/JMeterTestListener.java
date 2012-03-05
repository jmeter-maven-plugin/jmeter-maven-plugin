package com.lazerycode.jmeter;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.testelement.TestListener;

public class JMeterTestListener extends AbstractListenerElement
        implements TestListener {

    boolean hasTestEnded = false;

    @Override
    public void testStarted() {

    }

    @Override
    public void testStarted(String string) {
        testStarted();
    }

    @Override
    public void testEnded() {
        this.hasTestEnded = true;
        System.out.println("***DETECTED END OF TEST***");
    }

    @Override
    public void testEnded(String string) {
        testEnded();
    }

    @Override
    public void testIterationStart(LoopIterationEvent lie) {
    }

    public boolean hasTestEnded(){
        return this.hasTestEnded;
    }
}