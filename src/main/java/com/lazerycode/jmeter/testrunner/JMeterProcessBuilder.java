package com.lazerycode.jmeter.testrunner;

import java.text.MessageFormat;

import com.lazerycode.jmeter.configuration.JMeterProcessJVMSettings;

public class JMeterProcessBuilder extends AbstractJavaProcessBuilder {

    public JMeterProcessBuilder(JMeterProcessJVMSettings settings) {
        super("ApacheJMeter.jar");
        if (null == settings) {
            settings = new JMeterProcessJVMSettings();
        }
        userSuppliedArguments.add(MessageFormat.format("-Xms{0}M", settings.getXms()));
        userSuppliedArguments.add(MessageFormat.format("-Xmx{0}M", settings.getXmx()));
        userSuppliedArguments.addAll(settings.getArguments());
    }

}
