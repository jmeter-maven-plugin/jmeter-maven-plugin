package com.lazerycode.jmeter.testrunner;

public class ExitException extends SecurityException {

    private static final long serialVersionUID = 5544099211927987521L;
    public int status;

    public ExitException(int status) {
        super(Integer.toString(status));
        this.status = status;
    }

    public int getCode() {
        return status;
    }
}
