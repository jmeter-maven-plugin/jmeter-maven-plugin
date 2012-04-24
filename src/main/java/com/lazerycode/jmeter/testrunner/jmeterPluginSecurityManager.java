package com.lazerycode.jmeter.testrunner;

import java.security.Permission;

/**
 * Capture System.exit commands so that we can check to see if JMeter is trying to kill us without warning.
 */
public class jmeterPluginSecurityManager extends SecurityManager {
    @Override
    public void checkExit(int status) {
        super.checkExit(status);
        throw new ExitException(status);
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
    }

    @Override
    public void checkPermission(Permission perm) {
    }

}
