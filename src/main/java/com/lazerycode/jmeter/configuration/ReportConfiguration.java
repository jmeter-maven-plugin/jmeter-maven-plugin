package com.lazerycode.jmeter.configuration;

import java.io.File;

/**
 * Is used for configuration of all report related configuration.
 * <p>
 * Configuration in pom.xml:
 * <p>
 * <pre>
 * {@code
 * <reportConfig>
 *     <enableReports></enableReports>
 *     <outputDirectory></outputDirectory>
 *     <postFix></postFix>
 *     <xsltFile></xsltFile>
 * </reportConfig>
 * }
 * </pre>
 *
 * @author Arne Franken
 */
public class ReportConfiguration {

    private File outputDirectory;
    private boolean outputDirectorySet = false;
    private String postfix = "-report.html";
    private boolean enableReports = false;
    private File xsltFile;

    /**
     * @return Absolute path of directory in which the reports are stored.
     */
    public String getOutputDirectoryAbsolutePath() {
        return this.outputDirectory.getAbsolutePath();
    }

    /**
     * Directory in which the reports are stored.
     * @param outputDirectory
     */
    public void createOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.outputDirectory.mkdirs();
        this.outputDirectorySet = true;
    }

    public boolean isOutputDirectorySet(){
        return this.outputDirectorySet;
    }

    /**
     * @return Postfix to add to report file.
     */
    public String getPostfix() {
        return this.postfix;
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
    public boolean areReportsEnabled() {
        return this.enableReports;
    }

    /**
     * Whether or not to generate reports after measurement.
     * Default: {@link true Boolean.TRUE}
     * @param enableReports
     */
    public void enableReports(boolean enableReports) {
        this.enableReports = enableReports;
    }

    /**
     * @return Custom Xslt which is used to create the report.
     */
    public File getXsltFile() {
        return this.xsltFile;
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
        return "ReportConfiguration [ Enable=" + areReportsEnabled() + "OutputDirectory=" + getOutputDirectoryAbsolutePath() + ", PostFix=" + getPostfix() +
                ", XsltFile=" + getXsltFile() + " ]";
    }
}
