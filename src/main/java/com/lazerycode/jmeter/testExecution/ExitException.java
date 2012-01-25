package com.lazerycode.jmeter.testExecution;

public class ExitException extends SecurityException {

    private static final long serialVersionUID = 5544099211927987521L;
    public int _rc;

    public ExitException(int rc) {
        super(Integer.toString(rc));
        _rc = rc;
    }

    public int getCode() {
        return _rc;
    }
}
