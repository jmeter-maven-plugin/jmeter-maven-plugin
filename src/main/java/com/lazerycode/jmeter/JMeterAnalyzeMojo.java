/**
 *
 */
package com.lazerycode.jmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xml.sax.SAXException;

import com.lazerycode.jmeter.configuration.CMDRunnerArgumentsArray;
import com.lazerycode.jmeter.parser.JMeterParser;
import com.lazerycode.jmeter.testrunner.CMDRunnerManager;
import com.lazerycode.jmeter.writer.Writer;

import freemarker.template.TemplateException;

/**
 *
 */
@Mojo(name = "analyze")
public class JMeterAnalyzeMojo extends JMeterAbstractMojo {

    /**
     * Sets the list of exclude patterns to use in directory scan for Test files.
     * Relative to testFilesDirectory.
     */
    @Parameter
    protected List<String> pluginsType;

    /**
     * Set the width of png
     */
    @Parameter(defaultValue = "1000")
    protected String width;

    /**
     * Set the height of png
     */
    @Parameter(defaultValue = "400")
    protected String height;

    public JMeterAnalyzeMojo() {
        super();
        // Init default value of pluginsType
        if (null == pluginsType) {
            pluginsType = new LinkedList<String>();
            pluginsType.add("ThreadsStateOverTime");
            pluginsType.add("ResponseCodesPerSecond");
            pluginsType.add("BytesThroughputOverTime");
            pluginsType.add("ResponseTimesOverTime");
            pluginsType.add("LatenciesOverTime");
            pluginsType.add("TransactionsPerSecond");
            pluginsType.add("ResponseTimesDistribution");
            pluginsType.add("ResponseTimesPercentiles");
            pluginsType.add("ThroughputVsThreads");
            pluginsType.add("TimesVsThreads");
        }
    }

    /**
     * Run all the JMeter tests.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTests) {
            getLog().info(" ");
            getLog().info("------------------------------------------------------------------------");
            getLog().info(" S K I P P I N G    A N A L Y Z E    P E R F O R M A N C E    T E S T S ");
            getLog().info("------------------------------------------------------------------------");
            getLog().info(" ");
            return;
        }
        getLog().info(" ");
        getLog().info("-------------------------------------------------------");
        getLog().info(" A N A L Y Z E    P E R F O R M A N C E    T E S T S");
        getLog().info("-------------------------------------------------------");
        getLog().info(" ");
        generateJMeterDirectoryTree();
        initialiseJMeterArgumentsArray(true);

        CMDRunnerArgumentsArray cmdRunnerArgumentsArray = new CMDRunnerArgumentsArray("Reporter", resourcesDir);
        cmdRunnerArgumentsArray.setHeight(height);
        cmdRunnerArgumentsArray.setWidth(width);

        CMDRunnerManager cmdRunnerManager = new CMDRunnerManager(testArgs , cmdRunnerArgumentsArray, pluginsType, suppressJMeterOutput, libExtDir);
        Map<File, Map<String, File>> allGraphes = cmdRunnerManager.generateGraphs();

        Writer writer = new Writer();
        for (File scenario : allGraphes.keySet()) {
            try {
                writer.write(analysisDir, scenario.getName(), allGraphes.get(scenario), new JMeterParser().parse(new FileReader(scenario)));
            } catch (Exception e) {
                new MojoExecutionException("Error during analyze report generating.", e);
            }
        }
    }

}
