package com.lazerycode.jmeter;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FailureScannerTest {

    private final Log logger = null;
    private final FailureScanner reportFailures = new FailureScanner(false, logger);
    private final FailureScanner suppressFailures = new FailureScanner(true, logger);

    @Test
    public void validateCheckLineForFailureTest() {
        assertThat(reportFailures.checkLineForFailures("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"false\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\">"),
                is(equalTo(true)));
        assertThat(reportFailures.checkLineForFailures("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"true\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\">"),
                is(equalTo(false)));
        assertThat(reportFailures.getFailureCount(),
                is(equalTo(1)));
    }

    @Test
    public void validateIgnoreFailuresTest() {
        assertThat(suppressFailures.checkLineForFailures("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"true\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\">"),
                is(equalTo(false)));
        assertThat(suppressFailures.getFailureCount(),
                is(equalTo(0)));
    }

    @Test
    public void validateResetFailureCountTest() {
        reportFailures.checkLineForFailures("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"false\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\">");
        assertThat(reportFailures.getFailureCount(),
                is(equalTo(1)));
        reportFailures.resetFailureCount();
        assertThat(reportFailures.getFailureCount(),
                is(equalTo(0)));
    }

    @Test
    public void jtlFormatFileWithFailuresTest() throws Exception {
        URI testResultsFile = this.getClass().getResource("/jtl2-1-fail.jtl").toURI();
        assertThat(reportFailures.hasTestPassed(new File(testResultsFile)),
                is(equalTo(false)));
        assertThat(reportFailures.getFailureCount(),
                is(equalTo(2)));
    }

    @Test
    public void jtlFormatFileWithNoFailuresTest() throws Exception {
        URI testResultsFile = this.getClass().getResource("/jtl2-1-pass.jtl").toURI();
        assertThat(reportFailures.hasTestPassed(new File(testResultsFile)),
                is(equalTo(true)));
        assertThat(reportFailures.getFailureCount(),
                is(equalTo(0)));
    }
}
