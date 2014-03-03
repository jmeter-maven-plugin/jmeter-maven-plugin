/**
 *
 */
package com.lazerycode.jmeter.configuration;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class CMDRunnerArgumentsArrayTest {

    @Test(expected = MojoExecutionException.class)
    public void testBuildArgumentsArrayWithoutPluginType() throws MojoExecutionException {
        CMDRunnerArgumentsArray cmdRunnerArgumentsArray = new CMDRunnerArgumentsArray("Tool", new File(""));
        cmdRunnerArgumentsArray.setInputFile(new File("inputFile.jtl"));
        System.out.print(cmdRunnerArgumentsArray.buildArgumentsArray());
    }

    @Test(expected = MojoExecutionException.class)
    public void testBuildArgumentsArrayWithoutInputFile() throws MojoExecutionException {
        CMDRunnerArgumentsArray cmdRunnerArgumentsArray = new CMDRunnerArgumentsArray("Tool", new File(""));
        cmdRunnerArgumentsArray.setPluginType("ThreadsStateOverTime");
        System.out.print(cmdRunnerArgumentsArray.buildArgumentsArray());
    }

    @Test()
    public void testBuildArgumentsArrayDefault() throws MojoExecutionException {
        String tool = "Tool";
        String pluginType = "ThreadsStateOverTime";
        String inputFile = "inputFile.jtl";
        CMDRunnerArgumentsArray cmdRunnerArgumentsArray = new CMDRunnerArgumentsArray(tool, new File(""));
        cmdRunnerArgumentsArray.setPluginType(pluginType);
        cmdRunnerArgumentsArray.setInputFile(new File(inputFile));
        List<String> arguments = cmdRunnerArgumentsArray.buildArgumentsArray();
        Assert.assertEquals(8, arguments.size());
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.TOOL, tool);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.GENERATE_PNG, inputFile + "-" + pluginType + ".png");
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.INPUT_JTL, inputFile);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.PLUGIN_TYPE, pluginType);
    }

    @Test()
    public void testBuildArgumentsArrayWithCSV() throws MojoExecutionException {
        String tool = "Tool";
        String pluginType = "ThreadsStateOverTime";
        String inputFile = "inputFile.jtl";
        CMDRunnerArgumentsArray cmdRunnerArgumentsArray = new CMDRunnerArgumentsArray(tool, new File(""));
        cmdRunnerArgumentsArray.setPluginType(pluginType);
        cmdRunnerArgumentsArray.setInputFile(new File(inputFile));
        cmdRunnerArgumentsArray.setResultFileOutputFormatIsPNG(false);
        List<String> arguments = cmdRunnerArgumentsArray.buildArgumentsArray();
        Assert.assertEquals(8, arguments.size());
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.TOOL, tool);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.GENERATE_CSV, inputFile + "-" + pluginType + ".csv");
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.INPUT_JTL, inputFile);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.PLUGIN_TYPE, pluginType);
    }

    @Test()
    public void testBuildArgumentsArrayDefaultWithHeight() throws MojoExecutionException {
        String tool = "Tool";
        String pluginType = "ThreadsStateOverTime";
        String inputFile = "inputFile.jtl";
        String height = "800";
        CMDRunnerArgumentsArray cmdRunnerArgumentsArray = new CMDRunnerArgumentsArray(tool, new File(""));
        cmdRunnerArgumentsArray.setPluginType(pluginType);
        cmdRunnerArgumentsArray.setInputFile(new File(inputFile));
        cmdRunnerArgumentsArray.setHeight(height);
        List<String> arguments = cmdRunnerArgumentsArray.buildArgumentsArray();
        Assert.assertEquals(10, arguments.size());
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.TOOL, tool);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.GENERATE_PNG, inputFile + "-" + pluginType + ".png");
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.INPUT_JTL, inputFile);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.PLUGIN_TYPE, pluginType);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.HEIGHT, height);
    }

    @Test()
    public void testBuildArgumentsArrayDefaultWithHeightAndWidth() throws MojoExecutionException {
        String tool = "Tool";
        String pluginType = "ThreadsStateOverTime";
        String inputFile = "inputFile.jtl";
        String height = "800";
        String width = "600";
        CMDRunnerArgumentsArray cmdRunnerArgumentsArray = new CMDRunnerArgumentsArray(tool, new File(""));
        cmdRunnerArgumentsArray.setPluginType(pluginType);
        cmdRunnerArgumentsArray.setInputFile(new File(inputFile));
        cmdRunnerArgumentsArray.setHeight(height);
        cmdRunnerArgumentsArray.setWidth(width);
        List<String> arguments = cmdRunnerArgumentsArray.buildArgumentsArray();
        Assert.assertEquals(12, arguments.size());
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.TOOL, tool);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.GENERATE_PNG, inputFile + "-" + pluginType + ".png");
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.INPUT_JTL, inputFile);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.PLUGIN_TYPE, pluginType);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.HEIGHT, height);
        verifContainsArgs(arguments, CMDRunnerCommandLineArguments.WIDTH, width);
    }

    private void verifContainsArgs(List<String> arguments, CMDRunnerCommandLineArguments cmdRunnerCommandLineArguments, String value) {
        int index = arguments.indexOf(cmdRunnerCommandLineArguments.getCommandLineArgument());
        Assert.assertTrue(index >= 0);
        if (null != value && (index + 1) < arguments.size()) {
            Assert.assertTrue(arguments.get(index + 1), arguments.get(index + 1).contains(value));
        }
    }

}
