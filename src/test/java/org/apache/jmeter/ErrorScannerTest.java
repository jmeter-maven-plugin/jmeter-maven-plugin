package org.apache.jmeter;

import org.junit.Test;

import java.io.File;
import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ErrorScannerTest {

    @Test
    public void testLineContainsForErrors() {
        ErrorScanner scanner = new ErrorScanner(false, false);
        assertThat(scanner.lineContainsForErrors("<failure>false</failure>"), is(equalTo(false)));
        assertThat(scanner.lineContainsForErrors("<failure>true</failure>"), is(equalTo(true)));
        assertThat(scanner.lineContainsForErrors("<error>true</error>"), is(equalTo(true)));
        assertThat(scanner.lineContainsForErrors("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"false\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "), is(equalTo(true)));
        assertThat(scanner.lineContainsForErrors("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"true\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "), is(equalTo(false)));
        assertThat(scanner.getFailureCount(), is(equalTo(2)));
        assertThat(scanner.getErrorCount(), is(equalTo(1)));
    }

    @Test
    public void testLineContainsWhenIgnoringErrorsAndFailures() {
        ErrorScanner scanner = new ErrorScanner(true, true);
        assertThat(scanner.lineContainsForErrors("<failure>false</failure>"), is(equalTo(false)));
        assertThat(scanner.lineContainsForErrors("<failure>true</failure>"), is(equalTo(false)));
        assertThat(scanner.lineContainsForErrors("<error>true</error>"), is(equalTo(false)));
        assertThat(scanner.lineContainsForErrors("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"false\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "), is(equalTo(false)));
        assertThat(scanner.lineContainsForErrors("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"true\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "), is(equalTo(false)));
        assertThat(scanner.getFailureCount(), is(equalTo(0)));
        assertThat(scanner.getErrorCount(), is(equalTo(0)));
    }

    @Test
    public void jtlFormatFileWithFailures() throws Exception {
        ErrorScanner scanner = new ErrorScanner(false, false);
        URI testResultsFile = this.getClass().getResource("/jtl2-1-fail.xml").toURI();
        assertThat(scanner.hasTestPassed(new File(testResultsFile)), is(equalTo(false)));
        assertThat(scanner.getFailureCount(), is(equalTo(2)));
        assertThat(scanner.getErrorCount(), is(equalTo(0)));
    }

    @Test
    public void jtlFormatFileWithNoFailures() throws Exception {
        ErrorScanner scanner = new ErrorScanner(false, false);
        URI testResultsFile = this.getClass().getResource("/jtl2-1-pass.xml").toURI();
        assertThat(scanner.hasTestPassed(new File(testResultsFile)), is(equalTo(true)));
        assertThat(scanner.getFailureCount(), is(equalTo(0)));
        assertThat(scanner.getErrorCount(), is(equalTo(0)));
    }
}
