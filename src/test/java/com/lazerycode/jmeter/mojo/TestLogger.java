package com.lazerycode.jmeter.mojo;

import org.apache.maven.plugin.logging.Log;

import java.util.ArrayList;

public class TestLogger implements Log {

    private ArrayList<CharSequence> debugContentContainer = new ArrayList<>();
    private ArrayList<CharSequence> infoContentContainer = new ArrayList<>();
    private ArrayList<CharSequence> warnContentContainer = new ArrayList<>();
    private ArrayList<CharSequence> errorContentContainer = new ArrayList<>();
    private ArrayList<Throwable> debugErrorContainer = new ArrayList<>();
    private ArrayList<Throwable> infoErrorContainer = new ArrayList<>();
    private ArrayList<Throwable> warnErrorContainer = new ArrayList<>();
    private ArrayList<Throwable> errorErrorContainer = new ArrayList<>();

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(CharSequence content) {
        debugContentContainer.add(content);
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
        debugContentContainer.add(content);
        debugErrorContainer.add(error);
    }

    @Override
    public void debug(Throwable error) {
        debugErrorContainer.add(error);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(CharSequence content) {
        infoContentContainer.add(content);
    }

    @Override
    public void info(CharSequence content, Throwable error) {
        infoContentContainer.add(content);
        infoErrorContainer.add(error);
    }

    @Override
    public void info(Throwable error) {
        infoErrorContainer.add(error);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(CharSequence content) {
        warnContentContainer.add(content);
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
        warnContentContainer.add(content);
        warnErrorContainer.add(error);
    }

    @Override
    public void warn(Throwable error) {
        warnErrorContainer.add(error);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(CharSequence content) {
        errorContentContainer.add(content);
    }

    @Override
    public void error(CharSequence content, Throwable error) {
        errorContentContainer.add(content);
        errorErrorContainer.add(error);
    }

    @Override
    public void error(Throwable error) {
        errorErrorContainer.add(error);
    }

    public ArrayList<CharSequence> getDebugContentContainer() {
        return debugContentContainer;
    }

    public ArrayList<CharSequence> getInfoContentContainer() {
        return infoContentContainer;
    }

    public ArrayList<CharSequence> getWarnContentContainer() {
        return warnContentContainer;
    }

    public ArrayList<CharSequence> getErrorContentContainer() {
        return errorContentContainer;
    }

    public ArrayList<Throwable> getDebugErrorContainer() {
        return debugErrorContainer;
    }

    public ArrayList<Throwable> getInfoErrorContainer() {
        return infoErrorContainer;
    }

    public ArrayList<Throwable> getWarnErrorContainer() {
        return warnErrorContainer;
    }

    public ArrayList<Throwable> getErrorErrorContainer() {
        return errorErrorContainer;
    }
}
