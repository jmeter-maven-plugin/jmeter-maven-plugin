package com.lazerycode.jmeter.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to specify JVM settings for the JMeter instance that will be run
 * <br>
 * Configuration in pom.xml:
 * <br>
 * <pre>
 * {@code
 * 	<configuration>
 *      <jMeterProcessJVMSettings>
 *          <javaRuntime>${env.JAVA_HOME}/bin/java</javaRuntime>
 *          <xms>512</xms>
 *          <xmx>1024</xmx>
 *          <arguments>
 *              <argument>foo</argument>
 *          </arguments>
 *      </jMeterProcessJVMSettings>
 *  </configuration>
 * }
 * </pre>
 *
 * @author Mark Collin
 */
@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class JMeterProcessJVMSettings {

    private int xms = 512;
    private int xmx = 512;
    private String javaRuntime = "java";
    private List<String> arguments = new ArrayList<>();

    private static final String RUN_HEADLESS = "-Djava.awt.headless=true";
    private static final String HEADLESS_SETTING = "-Djava.awt.headless=";

    public JMeterProcessJVMSettings() {
        super();
    }

    public int getXms() {
        return xms;
    }

    public int getXmx() {
        return xmx;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public JMeterProcessJVMSettings addArgument(String newArgument) {
        if (arguments.stream().noneMatch(argument -> argument.equals(newArgument))) {
            arguments.add(newArgument);
        }

        return this;
    }

    public JMeterProcessJVMSettings setHeadlessDefaultIfRequired() {
        if (arguments.stream().noneMatch(argument -> argument.contains(HEADLESS_SETTING))) {
            addArgument(RUN_HEADLESS);
        }

        return this;
    }

    public String getJavaRuntime() {
        return this.javaRuntime;
    }
}
