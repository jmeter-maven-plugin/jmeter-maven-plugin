package com.lazerycode.jmeter.testrunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.DirectoryScanner;

/**
 * <pre>
 * 
 * Scan Project directories for JMeter Test Files according to includes and excludes
 * 
 *  If "includes" only has one row with separated values, 
 *  	then a new List is build with those values as rows
 *  	and the jmx extension will be added if it's needed
 * 
 * </pre>
 * 
 * @author s2o
 */
public class JMeterFilesScaner {

	private static final String ALL_JMX = "**/*.jmx";

	private static final String COMMA = ",";

	private static final String JMX = ".jmx";

	private final File testFilesDirectory;
	private final List<String> testFilesIncluded;
	private final List<String> testFilesExcluded;

	public JMeterFilesScaner(File testFilesDirectory, List<String> testFilesIncluded, List<String> testFilesExcluded) {
		this.testFilesDirectory = testFilesDirectory;
		this.testFilesIncluded = testFilesIncluded;
		this.testFilesExcluded = testFilesExcluded;
	}

	/**
	 * Get the list of jMeter files to be executed
	 * 
	 * @return
	 */
	protected List<String> generateTestList() {
		List<String> jmeterTestFiles = new ArrayList<String>();
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(this.testFilesDirectory);

		if (testFilesIncluded != null && testFilesIncluded.size() == 1 && testFilesIncluded.get(0).contains(COMMA)) {
			String[] jMeterSuites = testFilesIncluded.get(0).split(COMMA);
			int indexFile = 0;
			for (String file : jMeterSuites) {
				if (!file.trim().endsWith(JMX)) {
					file = file.trim().concat(JMX);
				}
				jMeterSuites[indexFile++] = file;
			}
			scanner.setIncludes(jMeterSuites);
		} else {
			scanner.setIncludes(this.testFilesIncluded == null ? new String[] { ALL_JMX } : this.testFilesIncluded
					.toArray(new String[jmeterTestFiles.size()]));
		}

		if (this.testFilesExcluded != null) {
			scanner.setExcludes(this.testFilesExcluded.toArray(new String[testFilesExcluded.size()]));
		}
		scanner.scan();
		final List<String> includedFiles = Arrays.asList(scanner.getIncludedFiles());
		jmeterTestFiles.addAll(includedFiles);

		return jmeterTestFiles;
	}

}
