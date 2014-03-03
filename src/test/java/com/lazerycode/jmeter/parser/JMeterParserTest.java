/**
 *
 */
package com.lazerycode.jmeter.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 */
public class JMeterParserTest {

    private final URL testFile = this.getClass().getResource("/jmeter-parser.jtl");

    private JmeterResults jmeterResults = null;

    @Before
    public void setTestFileAbsolutePath() throws FileNotFoundException, IOException, SAXException, URISyntaxException {
        JMeterParser jMeterParser = new JMeterParser();
        jmeterResults = jMeterParser.parse(new FileReader(new File(testFile.toURI())));
    }

    @Test
    public void testParse() throws IOException, SAXException, URISyntaxException {
        Assert.assertNotNull(jmeterResults);
        Assert.assertEquals(1393841436046L, jmeterResults.getStartTimestamp());
        Assert.assertEquals(1393841453339L, jmeterResults.getEndTimestamp());
        Assert.assertEquals(17, jmeterResults.getDurationTest());
        Assert.assertEquals("0h 0m 17s", jmeterResults.getDurationTestFormat());
        verifCounter(jmeterResults.getGlobalCounter(), 2770.4, 15.0, 0, 7, 1208, 1609, 47061, 99.9, 35, 0.1);
        Assert.assertEquals(3, jmeterResults.getUrisCounter().size());

        for (String key : jmeterResults.getUrisCounter().keySet()) {
            if (key.equals("Query 1")) {
                verifCounter(jmeterResults.getUrisCounter().get(key), 925.5, 16.0, 0, 7, 1200, 1539, 15722, 99.9, 11, 0.1);
            } else if (key.equals("Query 2")) {
                verifCounter(jmeterResults.getUrisCounter().get(key), 923.3, 13.0, 0, 7, 936, 1558, 15682, 99.9, 14, 0.1);
            } else if (key.equals("Query 3")) {
                verifCounter(jmeterResults.getUrisCounter().get(key), 921.6, 16.0, 0, 7, 1438, 1609, 15657, 99.9, 10, 0.1);
            } else {
                throw new IllegalAccessError(key + " not verified.");
            }
        }
    }

    private void verifCounter(Counter counter, double throughput, double average, long min, long percentile_50, long percentile_99_9, long max,
            long successCount, double successCountPercent, long errorCount, double errorCountPercent) {
        Assert.assertNotNull("Counter is null.", counter);
        Assert.assertEquals("Throughput incorrect.", throughput, counter.getThroughput(), 0);
        Assert.assertEquals("Average incorrect.", average, counter.getAverage(), 0);
        Assert.assertEquals("Min incorrect.", min, counter.getMin());
        Assert.assertEquals("50 percentile incorrect.", percentile_50, counter.getPercentile(50.0));
        Assert.assertEquals("99 percentile incorrect.", percentile_99_9, counter.getPercentile(99.9));
        Assert.assertEquals("Max incorrect.", max, counter.getMax());
        Assert.assertEquals("Success count incorrect.", successCount, counter.getSuccessCount());
        Assert.assertEquals("Success percent incorrect.", successCountPercent, counter.getSuccessCountPercent(), 0);
        Assert.assertEquals("Error count incorrect.", errorCount, counter.getErrorCount());
        Assert.assertEquals("Error percent incorrect.", errorCountPercent, counter.getErrorCountPercent(), 0);
        Assert.assertEquals("Total count incorrect.", successCount + errorCount, counter.getTotalCount());
    }

    @Test(expected = IllegalAccessError.class)
    public void testParseIllegalAccessAddValue() throws IOException, SAXException, URISyntaxException {
        jmeterResults.addValue("test", true, 1000, 1000);
    }

    @Test(expected = IllegalAccessError.class)
    public void testParseCounterIllegalAccessAddValue() throws IOException, SAXException, URISyntaxException {
        jmeterResults.getGlobalCounter().incrementCount(true, 17);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParsePercentileOutRangeMin() throws IOException, SAXException, URISyntaxException {
        jmeterResults.getGlobalCounter().getPercentile(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParsePercentileOutRangeMax() throws IOException, SAXException, URISyntaxException {
        jmeterResults.getGlobalCounter().getPercentile(100.1);
    }

}
