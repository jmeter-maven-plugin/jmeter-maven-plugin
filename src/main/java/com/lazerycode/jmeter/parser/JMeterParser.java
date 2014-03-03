/**
 *
 */
package com.lazerycode.jmeter.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 */
public class JMeterParser {

    /**
     * Parses a JMeter Result XML file and provides a {@link AggregatedResponses} for every {@link Parser#getKey key}
     *
     * @param reader the JMeter xml file
     *
     * @return The AggregatedResponses for every thread group
     *
     * @throws IOException If reading fails
     * @throws SAXException  If parsing fails
     */
    public JmeterResults parse(Reader reader) throws IOException, SAXException {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            Parser parser = new Parser();
            saxParser.parse(new InputSource(reader), parser);
            return parser.getResults();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Parser could not be created ", e);
        }
    }

    /**
     * Parser does the heavy lifting.
     */
    private static class Parser extends DefaultHandler {

        private Set<String> nodeNames;

        private JmeterResults jmeterResult = new JmeterResults();

        /**
         * Constructor.
         * Fields configured from Environment
         */
        public Parser() {
            nodeNames = new HashSet<String>();
            nodeNames.add("httpSample");
            nodeNames.add("sample");
        }

        public JmeterResults getResults() {
            return jmeterResult;
        }

        @Override
        public void startElement(String u, String localName, String qName, Attributes attributes) throws SAXException {
            if (nodeNames.contains(localName) || nodeNames.contains(qName)) {
                String uri = attributes.getValue("lb");
                boolean success = Boolean.valueOf(attributes.getValue("s"));
                long timestamp = Long.parseLong(attributes.getValue("ts"));
                long duration = Long.parseLong(attributes.getValue("t"));

                jmeterResult.addValue(uri, success, duration, timestamp);
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            jmeterResult.finish();
        }
    }
}
