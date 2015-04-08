package com.lazerycode.jmeter.testrunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class JMeterFilesScanerTest {

	private final URL testFilesDirectoryURL = this.getClass().getResource("/");
	private File testFilesDirectory;

	@Before
	public void init() {
		try {
			testFilesDirectory = new File(testFilesDirectoryURL.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNull() {
		List<String> testFilesIncluded = null;
		List<String> testFilesExcluded = new ArrayList<String>();
		List<String> testsFiles = new JMeterFilesScaner(testFilesDirectory, testFilesIncluded, testFilesExcluded)
				.generateTestList();
		assertThat(testsFiles.size(), is(equalTo(2)));
	}

	@Test
	public void testEmpty() {
		List<String> testFilesIncluded = new ArrayList<String>();
		List<String> testFilesExcluded = new ArrayList<String>();
		List<String> testsFiles = new JMeterFilesScaner(testFilesDirectory, testFilesIncluded, testFilesExcluded)
				.generateTestList();
		assertThat(testsFiles.size(), is(equalTo(0)));
	}

	@Test
	public void testFirstRow() {
		List<String> testFilesIncluded = new ArrayList<String>();
		List<String> testFilesExcluded = new ArrayList<String>();
		testFilesIncluded.add("test.jmx,test2.jmx");
		List<String> testsFiles = new JMeterFilesScaner(testFilesDirectory, testFilesIncluded, testFilesExcluded)
				.generateTestList();
		assertThat(testsFiles.size(), is(equalTo(2)));
	}

	@Test
	public void testFirstRow_withOutExtension() {
		List<String> testFilesIncluded = new ArrayList<String>();
		List<String> testFilesExcluded = new ArrayList<String>();
		testFilesIncluded.add("test,test2");
		List<String> testsFiles = new JMeterFilesScaner(testFilesDirectory, testFilesIncluded, testFilesExcluded)
				.generateTestList();
		assertThat(testsFiles.size(), is(equalTo(2)));
	}

}
