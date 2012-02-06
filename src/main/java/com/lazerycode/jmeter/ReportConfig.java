package com.lazerycode.jmeter;

import java.io.File;

/**
 * Is used for configuration of all report related configuration.
 * <p>
 * Configuration in pom.xml:
 * <p>
 * <pre>
 * {@code
 * <reportConfig>
 *     <enable></enable>
 *     <outputDirectory></outputDirectory>
 *     <postFix></postFix>
 *     <xsltFile></xsltFile>
 * </reportConfig>
 * }
 * </pre>
 *
 * @author Arne Franken
 */
public class ReportConfig {

    private File outputDirectory;
    private String postfix = "-report.html";
    private boolean enable = true;
    private File xsltFile;

    /**
     * @return Directory in which the reports are stored.
     */
    //TODO: currently #setOutputDirectory is called in JMeterMojo#generateJMeterDirectoryTree().
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Directory in which the reports are stored.
     * @param outputDirectory
     */
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @return Postfix to add to report file.
     */
    public String getPostfix() {
        return postfix;
    }

    /**
     * Postfix to add to report file.
     * Default: "-report.html"
     * @param postfix
     */
    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    /**
     * @return Whether or not to generate reports after measurement.
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Whether or not to generate reports after measurement.
     * Default: {@link true Boolean.TRUE}
     * @param enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * @return Custom Xslt which is used to create the report.
     */
    public File getXsltFile() {
        return xsltFile;
    }

    /**
     * Custom Xslt which is used to create the report.
     * @param xsltFile
     */
    public void setXsltFile(File xsltFile) {
        this.xsltFile = xsltFile;
    }

    @Override
    public String toString() {
        return "ReportConfig [ Enable=" + isEnable() + "OutputDirectory=" + getOutputDirectory() + ", PostFix=" + getPostfix() +
                ", XsltFile=" + getXsltFile() + " ]";
    }
}
