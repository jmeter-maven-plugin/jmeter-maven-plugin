package com.lazerycode.jmeter.configuration;

import static com.lazerycode.jmeter.configuration.CMDRunnerCommandLineArguments.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.maven.plugin.MojoExecutionException;

/**
 *
 */
public class CMDRunnerArgumentsArray {

    private final TreeSet<CMDRunnerCommandLineArguments> argumentList = new TreeSet<CMDRunnerCommandLineArguments>();

    private String tool;

    private File resourcesDir;

    private String width;

    private String height;

    private File inputFile;

    private String pluginType;

    private String resultFileExtension;

    public CMDRunnerArgumentsArray(String tool, File resourcesDir) {
        this.tool = tool;
        this.resourcesDir = resourcesDir;
        updateArgumentList(tool, TOOL);
        setResultFileOutputFormatIsPNG(true);
    }

    public void setWidth(String width) {
        this.width = width;
        updateArgumentList(width, WIDTH);
    }

    public void setHeight(String height) {
        this.height = height;
        updateArgumentList(height, HEIGHT);
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
        updateArgumentList(inputFile, INPUT_JTL);
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
        updateArgumentList(pluginType, PLUGIN_TYPE);
    }

    public void setResultFileOutputFormatIsPNG(boolean isPNGFormat) {
        if (isPNGFormat) {
            resultFileExtension = ".png";
            argumentList.remove(GENERATE_CSV);
            argumentList.add(GENERATE_PNG);
        } else {
            resultFileExtension = ".csv";
            argumentList.remove(GENERATE_PNG);
            argumentList.add(GENERATE_CSV);
        }
    }

    public File getGeneratePath() {
        if (null == resourcesDir || null == inputFile || null == pluginType || null == resultFileExtension) {
            throw new IllegalStateException("Mandatory attributes are null - resourcesDir : "
                    + resourcesDir + ",inputFile : " + inputFile + ",pluginType : "
                    + pluginType + ",resultFileException : " + resultFileExtension);
        }
        return new File(new StringBuilder(resourcesDir.getAbsolutePath())
                .append(File.separator).append(inputFile.getName()).append("-")
                .append(pluginType).append(resultFileExtension).toString());
    }

    /**
     * Generate an arguments array representing the command line options you want to send to CMDRunner.
     * The order of the array is determined by the order the values in CMDRunnerCommandLineArguments are defined.
     *
     * @return An array representing the command line sent to JMeter
     * @throws MojoExecutionException
     */
    public List<String> buildArgumentsArray() throws MojoExecutionException {
        if (!argumentList.contains(TOOL))
            throw new MojoExecutionException("No tool specified!");
        if (!argumentList.contains(PLUGIN_TYPE))
            throw new MojoExecutionException("No plugin-type specified!");
        if (!argumentList.contains(INPUT_JTL))
            throw new MojoExecutionException("No input-file specified!");
        if (!argumentList.contains(GENERATE_CSV) && !argumentList.contains(GENERATE_PNG))
            throw new MojoExecutionException("No generate-type (PNG/CSV) specified!");
        if (!argumentList.contains(PLUGIN_TYPE))
            throw new MojoExecutionException("No plugin-type specified!");

        List<String> argumentsArray = new ArrayList<String>();

        for (CMDRunnerCommandLineArguments argument : argumentList) {
            String commandLineArgument = argument.getCommandLineArgument();
            argumentsArray.add(commandLineArgument);
            switch (argument) {
                case GENERATE_CSV:
                case GENERATE_PNG:
                    argumentsArray.add(getGeneratePath().getAbsolutePath());
                    break;
                case HEIGHT:
                    argumentsArray.add(height);
                    break;
                case INPUT_JTL:
                    argumentsArray.add(inputFile.getAbsolutePath());
                    break;
                case PLUGIN_TYPE:
                    argumentsArray.add(pluginType);
                    break;
                case TOOL:
                    argumentsArray.add(tool);
                    break;
                case WIDTH:
                    argumentsArray.add(width);
                    break;
                default:
                    argumentsArray.remove(commandLineArgument);
                    break;
            }
        }
        return argumentsArray;
    }

    private void updateArgumentList(Object attr, CMDRunnerCommandLineArguments argument) {
        if (null == attr) {
            argumentList.remove(argument);
        } else {
            argumentList.add(argument);
        }
    }
}
