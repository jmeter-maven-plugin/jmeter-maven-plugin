package com.lazerycode.jmeter.reporting;

import com.lazerycode.jmeter.JMeterMojo;
import com.lazerycode.jmeter.configuration.ReportConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.List;

/**
 * ReportGenerator encapsules functions for generating reports
 *
 * @deprecated will be removed when separate reports plugin is released
 */
public class ReportGenerator extends JMeterMojo {

    private ReportConfiguration reportConfig;

    public ReportGenerator(ReportConfiguration reportConfig) {
        this.reportConfig = reportConfig;
    }

    /**
     * Create a report for every resultfile in the given list
     *
     * @param resultFiles list of resultfiles
     * @throws MojoExecutionException
     */
    public void makeReport(List<String> resultFiles) throws MojoExecutionException {
        if (reportConfig.areReportsEnabled()) {
            try {
                ReportTransformer transformer;
                transformer = new ReportTransformer(getXslt());
                getLog().info(" ");
                getLog().info("Building JMeter Report(s)...");
                for (String resultFile : resultFiles) {
                    final String outputFile = toOutputFileName(resultFile);
                    transformer.transform(resultFile, outputFile);
                    getLog().info(" ");
                    getLog().info("Raw results: " + resultFile);
                    getLog().info("Test report: " + outputFile);
                }
            } catch (FileNotFoundException e) {
                throw new MojoExecutionException("Error writing report file jmeter file.", e);
            } catch (TransformerException e) {
                throw new MojoExecutionException("Error transforming jmeter results", e);
            } catch (IOException e) {
                throw new MojoExecutionException("Error copying resources to jmeter results", e);
            }
        } else {
            //TODO: do we really want to log this if we remove reporting in 1.5?
            getLog().info("Report generation is currently disabled.");
            getLog().info("set <enable>true</enable> inside <reportConfig> to enable them.");
        }
    }

    //=======================================================================================================

    private InputStream getXslt() throws IOException {
        if (reportConfig.getXsltFile() == null) {
            //if we are using the default report, also copy the images out.
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/collapse.jpg"), new FileOutputStream(reportConfig.getOutputDirectoryAbsolutePath() + File.separator + "collapse.jpg"));
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/expand.jpg"), new FileOutputStream(reportConfig.getOutputDirectoryAbsolutePath() + File.separator + "expand.jpg"));
            return Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/jmeter-results-detail-report_21.xsl");
        } else {
            return new FileInputStream(reportConfig.getXsltFile());
        }
    }

    /**
     * returns the fileName with the configured reportPostfix
     *
     * @param fileName the String to modify
     * @return modified fileName
     */
    private String toOutputFileName(String fileName) {
        if (fileName.endsWith(".xml")) {
            return fileName.replace(".xml", reportConfig.getPostfix());
        } else {
            return fileName + reportConfig.getPostfix();
        }
    }
}
