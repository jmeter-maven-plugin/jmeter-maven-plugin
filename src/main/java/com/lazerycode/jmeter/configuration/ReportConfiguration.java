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
 *     <enable></enable>
 *     <outputDirectory></outputDirectory>
 *     <postFix></postFix>
 *     <xsltFile></xsltFile>
 * </reportConfig>
 * }
 * </pre>
 *
 * @author Arne Franken
 * @deprecated will be removed when separate reports plugin is released
 */
@Deprecated
public class ReportConfiguration {

    private File outputDirectory;
    private boolean outputDirectorySet = false;
    private String postfix = "-report.html";
    private boolean enable = false;
    private File xsltFile;

    /**
     * @return Absolute path of directory in which the reports are stored.
     */
    public String getOutputDirectoryAbsolutePath() {
        return this.outputDirectory != null ? this.outputDirectory.getAbsolutePath() : "";
    }

    /**
     * Directory in which the reports are stored.
     * @param outputDirectory
     */
    public void setOutputDirectory(File outputDirectory) {
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
        return this.enable;
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
        return "ReportConfiguration [ Enable=" + areReportsEnabled() + "OutputDirectory=" + (getOutputDirectoryAbsolutePath()!=null ? getOutputDirectoryAbsolutePath() : "") + ", PostFix=" + getPostfix() +
                ", XsltFile=" + (getXsltFile()!= null ? getXsltFile() : "") + " ]";
    }
}
