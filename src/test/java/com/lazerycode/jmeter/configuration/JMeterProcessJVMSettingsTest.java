package com.lazerycode.jmeter.configuration;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JMeterProcessJVMSettingsTest {


    private static final String JAVA_AWT_HEADLESS_TRUE = "-Djava.awt.headless=true";

    @Test
    public void validateDefaultJVMSettings() {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();

        assertThat(jMeterProcessJVMSettings.getJavaRuntime()).isEqualTo("java");
        assertThat(jMeterProcessJVMSettings.getXms()).isEqualTo(512);
        assertThat(jMeterProcessJVMSettings.getXmx()).isEqualTo(512);
        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(0);
    }

    @Test
    public void validateThatArgumentsCanBeAdded() {
        String serverPort = "-Dserver_port=8080";
        String hostname = "-Djava.rmi.server.hostname=foo";
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(0);

        jMeterProcessJVMSettings.addArgument(serverPort);

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(1);
        assertThat(jMeterProcessJVMSettings.getArguments().get(0)).isEqualTo(serverPort);

        jMeterProcessJVMSettings.addArgument(hostname);

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(2);
        assertThat(jMeterProcessJVMSettings.getArguments().get(0)).isEqualTo(serverPort);
        assertThat(jMeterProcessJVMSettings.getArguments().get(1)).isEqualTo(hostname);
    }

    @Test
    public void forceHeadlessWillOnlyBeAddedOnce() {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(0);

        jMeterProcessJVMSettings.setHeadlessDefaultIfRequired();

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(1);
        assertThat(jMeterProcessJVMSettings.getArguments().get(0)).isEqualTo(JAVA_AWT_HEADLESS_TRUE);

        jMeterProcessJVMSettings.setHeadlessDefaultIfRequired();

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(1);
        assertThat(jMeterProcessJVMSettings.getArguments().get(0)).isEqualTo(JAVA_AWT_HEADLESS_TRUE);
    }

    @Test
    public void forceHeadlessDoesNotAddAnythingIfHeadlessHasAlreadyBeenAddedViaArguments() {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(0);

        jMeterProcessJVMSettings.addArgument(JAVA_AWT_HEADLESS_TRUE);

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(1);
        assertThat(jMeterProcessJVMSettings.getArguments().get(0)).isEqualTo(JAVA_AWT_HEADLESS_TRUE);

        jMeterProcessJVMSettings.setHeadlessDefaultIfRequired();

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(1);
        assertThat(jMeterProcessJVMSettings.getArguments().get(0)).isEqualTo(JAVA_AWT_HEADLESS_TRUE);
    }

    @Test
    public void willNotAddArgumentIfItAlreadyExists() {
        JMeterProcessJVMSettings jMeterProcessJVMSettings = new JMeterProcessJVMSettings();

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(0);

        jMeterProcessJVMSettings.addArgument("some argument");

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(1);
        assertThat(jMeterProcessJVMSettings.getArguments().get(0)).isEqualTo("some argument");

        jMeterProcessJVMSettings.addArgument("some other argument");

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(2);
        assertThat(jMeterProcessJVMSettings.getArguments().get(0)).isEqualTo("some argument");
        assertThat(jMeterProcessJVMSettings.getArguments().get(1)).isEqualTo("some other argument");

        jMeterProcessJVMSettings.addArgument("some argument");

        assertThat(jMeterProcessJVMSettings.getArguments().size()).isEqualTo(2);
        assertThat(jMeterProcessJVMSettings.getArguments().get(0)).isEqualTo("some argument");
        assertThat(jMeterProcessJVMSettings.getArguments().get(1)).isEqualTo("some other argument");
    }
}
