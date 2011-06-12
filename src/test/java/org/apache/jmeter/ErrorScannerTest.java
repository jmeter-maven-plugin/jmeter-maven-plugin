package org.apache.jmeter;

import static org.junit.Assert.*;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class ErrorScannerTest {

	@Test
	public void testLineContainsForErrors() throws MojoFailureException {
		ErrorScanner scanner = new ErrorScanner(true, true);
		
		assertFalse(scanner.lineContainsForErrors("	<failure>false</failure>"));
		assertTrue(scanner.lineContainsForErrors("	<failure>true</failure>"));
		assertTrue(scanner.lineContainsForErrors("	<error>true</error>"));
		
		assertTrue(scanner.lineContainsForErrors("	<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"false\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "));
		assertFalse(scanner.lineContainsForErrors("	<httpSample t=\"44\" lt=\"44\" ts=\"1303959710655\" s=\"true\" lb=\"/energy/mets/day/{endDate}/{StartDate} Reversed Dates\" rc=\"400\" rm=\"Bad Request\" "));
	}

}
