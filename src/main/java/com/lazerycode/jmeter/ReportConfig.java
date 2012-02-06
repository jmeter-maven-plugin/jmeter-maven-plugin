package com.lazerycode.jmeter;

import java.io.File;

/**
 * Value class that contains all report related configuration
 */
public class ReportConfig {
    
    /**
     * Directory in which the reports are stored.
     */
    private File outputDirectory;

    /**
     * Postfix to add to report file.
     */
    private String postfix = "-report.html";

    /**
     * Whether or not to generate reports after measurement.
     */
    private boolean enable = true;

    /**
     * Custom Xslt which is used to create the report.
     */
    private File xsltFile;

    //TODO: currently #setOutputDirectory is called in JMeterMojo#generateJMeterDirectoryTree().
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public File getXsltFile() {
        return xsltFile;
    }

    public void setXsltFile(File xsltFile) {
        this.xsltFile = xsltFile;
    }
    
    public String toString() {
        return "ReportConfig [ Enable=" + isEnable() + "OutputDirectory=" + getOutputDirectory() + ", PostFix=" + getPostfix() +
                ", XsltFile=" + getXsltFile() + " ]";
    }
}
