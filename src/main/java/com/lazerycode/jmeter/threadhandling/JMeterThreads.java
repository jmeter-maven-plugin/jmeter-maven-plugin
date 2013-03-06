package com.lazerycode.jmeter.threadhandling;

/**
 * Thread names added to this list will be used when scanning JMeterThreads directly after JMeter is called
 * The plugin will then wait for the thread to finish
 */
public enum JMeterThreads {

    STANDARD_JMETER_ENGINE("StandardJMeterEngine", false),
    GUI_THREAD_WINDOWS("AWT-Windows", true),
    GUI_THREAD_MACOSX("AWT-AppKit", true),
    GUI_THREAD_LINUX("AWT-XAWT", true);

    private final String threadName;
    private final boolean isGUIThread;

    JMeterThreads(String threadName, boolean isGUIThread) {
        this.threadName = threadName;
        this.isGUIThread = isGUIThread;
    }

    public String getThreadName() {
        return threadName;
    }

    public boolean isGUIThread() {
        return isGUIThread;
    }
}
