package com.lazerycode.jmeter.reporting;

import com.lazerycode.jmeter.JMeterMojo;
import com.lazerycode.jmeter.ReportConfig;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.List;

public class ReportGenerator extends JMeterMojo{

    private String reportPostfix;
    private File reportXslt;
    private File reportDir;
    private boolean createReports;
    private ReportConfig reportConfig;

    public ReportGenerator(String reportPostfix, File reportXslt, File reportDir, boolean createReports) {
        this.reportPostfix = reportPostfix;
        this.reportXslt = reportXslt;
        this.reportDir = reportDir;
        this.createReports = createReports;
    }

    public ReportGenerator(ReportConfig reportConfig) {
        this.reportConfig = reportConfig;
    }

    public void makeReport(List<String> results) throws MojoExecutionException {
        if (reportConfig.isEnable()) {
            try {
                ReportTransformer transformer;
                transformer = new ReportTransformer(getXslt());
                getLog().info(" ");
                getLog().info("Building JMeter Report(s)...");
                for (String resultFile : results) {
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
            getLog().info("Report generation disabled.");
        }
    }

    private InputStream getXslt() throws IOException {
        if (reportConfig.getXsltFile() == null) {
            //if we are using the default report, also copy the images out.
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/collapse.jpg"), new FileOutputStream(reportConfig.getOutputDirectory().getPath() + File.separator + "collapse.jpg"));
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("reports/expand.jpg"), new FileOutputStream(reportConfig.getOutputDirectory().getPath() + File.separator + "expand.jpg"));
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
