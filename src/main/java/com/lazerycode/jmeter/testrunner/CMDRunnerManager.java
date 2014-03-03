/**
 *
 */
package com.lazerycode.jmeter.testrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import com.lazerycode.jmeter.configuration.CMDRunnerArgumentsArray;
import com.lazerycode.jmeter.configuration.JMeterArgumentsArray;

/**
 *
 */
public class CMDRunnerManager {

    private Log log = new SystemStreamLog();

    private final JMeterArgumentsArray baseTestArgs;

    private final CMDRunnerArgumentsArray cmdRunnerArgumentsArray;

    private List<String> pluginsType;

    private File workCMDRunnerDir;

    private boolean suppressJMeterOutput;

    public CMDRunnerManager(JMeterArgumentsArray baseTestArgs, CMDRunnerArgumentsArray cmdRunnerArgumentsArray, List<String> pluginsType, boolean suppressJMeterOutput, File workCMDRunnerDir) {
        this.baseTestArgs = baseTestArgs;
        this.cmdRunnerArgumentsArray = cmdRunnerArgumentsArray;
        this.pluginsType = pluginsType;
        this.workCMDRunnerDir = workCMDRunnerDir;
        this.suppressJMeterOutput = suppressJMeterOutput;
    }

    public Map<File, Map<String, File>> generateGraphs() throws MojoExecutionException {
        Map<File, Map<String, File>> result = new HashMap<File, Map<String, File>>();
        File resultDir = new File(baseTestArgs.getResultsDirectory());

        if (resultDir.exists()) {
            for (File file : resultDir.listFiles()) {
                result.put(file, generateGraph(file));
            }
        }
        return result;
    }

    private Map<String, File> generateGraph(File inputFile) throws MojoExecutionException {
        Map<String, File> result = new LinkedHashMap<String, File>();
        Map<String, Process> processMap = new HashMap<String, Process>(pluginsType.size());

        for (String pluginType : pluginsType) {
            log.info("Launch generate graph " + pluginType);
            // Start the test.
            CMDRunnerProcessBuilder cmdRunnerProcessBuilder = new CMDRunnerProcessBuilder();
            cmdRunnerProcessBuilder.setWorkingDirectory(workCMDRunnerDir);
            cmdRunnerArgumentsArray.setInputFile(inputFile);
            cmdRunnerArgumentsArray.setPluginType(pluginType);
            cmdRunnerProcessBuilder.addArguments(cmdRunnerArgumentsArray.buildArgumentsArray());

            try {
                processMap.put(pluginType, cmdRunnerProcessBuilder.startProcess());
                result.put(pluginType, cmdRunnerArgumentsArray.getGeneratePath());
            } catch (IOException e) {
                log.error("Error generating " + pluginType, e);
            }
        }

        log.info("Wait generation...");

        for (String pluginType : processMap.keySet()) {
            try {
                Process process = processMap.get(pluginType);

                //Log process output
                if (!suppressJMeterOutput) {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = br.readLine()) != null) {
                            log.info(line);
                        }
                    } catch (IOException e) {
                        log.error("Error generating log for external process of " + pluginType, e);
                    }
                }
                int jMeterExitCode = process.waitFor();
                if (jMeterExitCode != 0) {
                    throw new MojoExecutionException("Test failed");
                }
                log.info("Completed generate graph: " + pluginType);
            } catch (InterruptedException ex) {
                log.info(" ");
                log.info("System Exit Detected!  Stopping Test...");
                log.info(" ");
            }
        }
        return result;
    }
}
