package com.lazerycode.jmeter.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class StreamRedirector implements Runnable{
    private final InputStream inputStream;
    private final Consumer<String> logLine;

    public StreamRedirector(InputStream inputStream, Consumer<String> logLine) {
        this.inputStream = inputStream;
        this.logLine = logLine;
    }

    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(logLine);
    }
}
