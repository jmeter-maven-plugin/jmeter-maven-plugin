/**
 *
 */
package com.lazerycode.jmeter.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.lazerycode.jmeter.JMeterAnalyzeMojo;
import com.lazerycode.jmeter.parser.JmeterResults;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 *
 */
public class Writer {

    private static final String ROOT_TEMPLATE = "html/main.ftl";

    public static final String ISO8601_FORMAT = "yyyyMMdd'T'HHmmssZ";

    /**
     * Render results as text to a file
     *
     * @param testResults Map to generate output from
     * @throws java.io.IOException
     * @throws freemarker.template.TemplateException
     *
     */
    public void write(File resultDir, String inputFile, Map<String, File> jmeterGraphs, JmeterResults jmeterResults) throws IOException, TemplateException {
        java.io.Writer out = new FileWriter(new File(resultDir, inputFile + ".html"));
        try {
            Map<String,Object> rootMap = new HashMap<String,Object>();
            rootMap.put("jmeterGraphs", jmeterGraphs);
            rootMap.put("jmeterResults", jmeterResults);
            rootMap.put("summaryFilename", inputFile);
            renderText(rootMap, ROOT_TEMPLATE, out);
        } finally {
            out.flush();
            out.close();
        }
    }

    /**
     * Render given {@link com.lazerycode.jmeter.analyzer.parser.AggregatedResponses testResults} as text
     *
     * @param testResults results to render
     * @param rootTemplate the template that Freemarker starts rendering with
     * @param out         output to write to
     * @throws java.io.IOException
     * @throws freemarker.template.TemplateException
     *
     */
    protected void renderText(Map<String, Object> rootMap, String rootTemplate,
            java.io.Writer out) throws IOException, TemplateException {
        Template root = getTemplate(rootTemplate);

        // Merge data-model with template
        root.process(rootMap, out);
    }


    /**
     * Try to load template from custom location.
     * Load bundled template from classpath in case no custom template is available or an error occurs
     *
     * @param templateName name of the template
     *
     * @return the template
     *
     * @throws IOException
     */
    public static Template getTemplate(String templateName) throws IOException {
        Configuration configuration = new Configuration();

        //make maps work in Freemarker when map key is not a String
        BeansWrapper beansWrapper = BeansWrapper.getDefaultInstance();
        beansWrapper.setSimpleMapWrapper(true);
        configuration.setObjectWrapper(beansWrapper);

        //make sure that numbers are not formatted as 1,000 but as 1000 instead
        configuration.setNumberFormat("computer");
        configuration.setDateFormat(ISO8601_FORMAT);
        configuration.setAutoFlush(true);

        //custom location not configured. Load from classpath.
        configuration.setClassForTemplateLoading(JMeterAnalyzeMojo.class, "templates");
        Template template = configuration.getTemplate(templateName);

        return template;
    }
}
