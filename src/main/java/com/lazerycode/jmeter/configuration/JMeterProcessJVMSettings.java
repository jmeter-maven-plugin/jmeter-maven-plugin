package com.lazerycode.jmeter.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to specify JVM settings for the JMeter instance that will be run
 * <p/>
 * Configuration in pom.xml:
 * <p/>
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
    private static final String DISABLE_RUN_HEADLESS = "-Djava.awt.headless=false";

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

    public JMeterProcessJVMSettings addArgument(String argument) {
        arguments.add(argument);

        return this;
    }

    public JMeterProcessJVMSettings forceHeadless() {
        arguments.removeIf(argument -> argument.equals(DISABLE_RUN_HEADLESS));
        if (arguments.stream().noneMatch(argument -> argument.equals(RUN_HEADLESS))) {
            arguments.add(RUN_HEADLESS);
        }

        return this;
    }

    public String getJavaRuntime() {
        return this.javaRuntime;
    }
}
