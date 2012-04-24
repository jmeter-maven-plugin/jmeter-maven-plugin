package com.lazerycode.jmeter.threadhandling;

/**
 * Thread names added to this list will be used when scanning JMeterThreads directly after JMeter is called
 * The plugin will then wait for the thread to finish
 * TODO: find out which threadname works for GUI detection on other operating systems
 */
public enum JMeterThreads {

    STANDARD_JMETER_ENGINE("StandardJMeterEngine", false),
    GUI_THREAD_WINDOWS("AWT-Windows", true),
    GUI_THREAD_MACOSX("AWT-AppKit", true);

    private final Object[] threadData;

    JMeterThreads(Object... values) {
        this.threadData = values;
    }

    public String getThreadName() {
        return (String) this.threadData[0];
    }

    public boolean isGUIThread() {
        return (Boolean) this.threadData[1];
    }
}
