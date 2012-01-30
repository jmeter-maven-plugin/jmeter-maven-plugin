package com.lazerycode.jmeter;

import junit.framework.TestCase;
import java.io.File;
import java.net.URI;

public class ErrorScannerTest extends TestCase {

    public void testLineContainsForErrors() {
        ErrorScanner scanner = new ErrorScanner(false, false);
        assertFalse(scanner.checkLineForErrors("<failure>false</failure>"));
        assertTrue(scanner.checkLineForErrors("<failure>true</failure>"));
        assertTrue(scanner.checkLineForErrors("<error>true</error>"));
        assertTrue(scanner.checkLineForErrors("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"false\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "));
        assertFalse(scanner.checkLineForErrors("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"true\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "));
        assertEquals(scanner.getFailureCount(), 2);
        assertEquals(scanner.getErrorCount(), 1);
    }

    public void testLineContainsWhenIgnoringErrorsAndFailures() {
        ErrorScanner scanner = new ErrorScanner(true, true);
        assertFalse(scanner.checkLineForErrors("<failure>false</failure>"));
        assertFalse(scanner.checkLineForErrors("<failure>true</failure>"));
        assertFalse(scanner.checkLineForErrors("<error>true</error>"));
        assertFalse(scanner.checkLineForErrors("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"false\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "));
        assertFalse(scanner.checkLineForErrors("<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"true\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "));
        assertEquals(scanner.getFailureCount(), 0);
        assertEquals(scanner.getErrorCount(), 0);
    }

    public void testJtlFormatFileWithFailures() throws Exception {
        ErrorScanner scanner = new ErrorScanner(false, false);
        URI testResultsFile = this.getClass().getResource("/jtl2-1-fail.jtl").toURI();
        assertFalse(scanner.hasTestPassed(new File(testResultsFile)));
        assertEquals(scanner.getFailureCount(), 2);
        assertEquals(scanner.getErrorCount(), 0);
    }

    public void testJtlFormatFileWithNoFailures() throws Exception {
        ErrorScanner scanner = new ErrorScanner(false, false);
        URI testResultsFile = this.getClass().getResource("/jtl2-1-pass.jtl").toURI();
        assertTrue(scanner.hasTestPassed(new File(testResultsFile)));
        assertEquals(scanner.getFailureCount(), 0);
        assertEquals(scanner.getErrorCount(), 0);
    }
}
