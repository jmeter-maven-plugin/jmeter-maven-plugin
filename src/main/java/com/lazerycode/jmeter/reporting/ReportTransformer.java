package com.lazerycode.jmeter.reporting;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Handles the xslt transform for the jmeter report.
 * 
 * @author Jon Roberts
 * @deprecated will be removed when separate reports plugin is released
 */
public class ReportTransformer {
		
	private final Transformer transformer;
	
	public ReportTransformer( InputStream xsl) throws TransformerConfigurationException {
		TransformerFactory tFactory = TransformerFactory.newInstance();		
		if (xsl == null) {
		    throw new NullPointerException("the input stream for the xsl was null.");
		}
		this.transformer = tFactory.newTransformer(new StreamSource(xsl));
	}

	public void transform(String inputFile, String outputFile)
            throws FileNotFoundException, TransformerException {

        transformer.transform(
            new StreamSource(inputFile),
            new StreamResult(new FileOutputStream(outputFile))
        );
	}

}
