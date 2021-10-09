package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.results.ResultScanner;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkResultScannerTest {

	/*
	All ignored by default

	These are here so we can perform benchmarks against the scanner code whenever we want to check it against an alternative implementation
	To create the data file you can use an NPM module called datagen:

		npm install -g datagen
		datagen init
		# Now populate generated filed as noted below
		datagen gen -s 10000000 -w 1 --out-file data1.xml

	Header:

		<?xml version="1.0" encoding="UTF-8"?>
		<testResults version="1.2">

	Footer:

		</testResults>

	Segment:

		<httpSample t="1187" lt="{segment_id}" ts="1133521593546" s="true" lb="/my_webapp/root/auth" rc="302" rm="Moved Temporarily" tn="Thread Group 1-1" dt="text"/>
		<httpSample t="16" lt="{segment_id}" ts="1133521593562" s="false" lb="/my_webapp/root/;jsessionid=xxx" rc="302" rm="Moved Temporarily" tn="Thread Group 1-1" dt="text"/>
	*/

    private static final boolean COUNT_FAILURES = true;
    private static final boolean DO_NOT_COUNT_FAILURES = false;
    private static final boolean COUNT_SUCCESSES = true;
    private static final boolean DO_NOT_COUNT_SUCCESSES = false;
    private static final boolean DEFAULT_IS_CSV = false;
    private static final boolean DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES = false;
    private static final List<String> DEFAULT_FAILURE_LIST = new ArrayList<>();
    private static final String TEST_XML_FILE_LOCATION = "/Programming/OpenSource/jmeter-maven-plugin/data1.xml";

    @Ignore
    @Test
    public void countSuccessAndFailure() throws Exception {
        File resultsFile = new File(TEST_XML_FILE_LOCATION);
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_IS_CSV, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);

        System.out.println("Benchmark new FailureScanner implementation");
        final LocalDateTime start = LocalDateTime.now();
        System.out.println("Start time is " + start);
        fileScanner.parseResultFile(resultsFile);
        final LocalDateTime finish = LocalDateTime.now();
        System.out.println("Finish time is " + finish);
        Duration duration = Duration.between(start, finish);
        System.out.println("Total time taken: " + duration.getSeconds());
        System.out.println("PASSED: " + fileScanner.getSuccessCount());
        System.out.println("FAILED: " + fileScanner.getFailureCount());
    }

    @Ignore
    @Test
    public void countSuccessOnly() throws Exception {
        File resultsFile = new File(TEST_XML_FILE_LOCATION);
        ResultScanner fileScanner = new ResultScanner(DO_NOT_COUNT_SUCCESSES, COUNT_FAILURES, DEFAULT_IS_CSV, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);

        System.out.println("Benchmark new FailureScanner implementation - success only");
        final LocalDateTime start = LocalDateTime.now();
        System.out.println("Start time is " + start);
        fileScanner.parseResultFile(resultsFile);
        final LocalDateTime finish = LocalDateTime.now();
        System.out.println("Finish time is " + finish);
        Duration duration = Duration.between(start, finish);
        System.out.println("Total time taken: " + duration.getSeconds());
        System.out.println("PASSED: " + fileScanner.getSuccessCount());
        System.out.println("FAILED: " + fileScanner.getFailureCount());
    }

    @Ignore
    @Test
    public void countFailureOnly() throws Exception {
        File resultsFile = new File(TEST_XML_FILE_LOCATION);
        ResultScanner fileScanner = new ResultScanner(COUNT_SUCCESSES, DO_NOT_COUNT_FAILURES, DEFAULT_IS_CSV, DEFAULT_ONLY_FAIL_WHEN_MATCHING_FAILURE_MESSAGES, DEFAULT_FAILURE_LIST);

        System.out.println("Benchmark new FailureScanner implementation - failure only");
        final LocalDateTime start = LocalDateTime.now();
        System.out.println("Start time is " + start);
        fileScanner.parseResultFile(resultsFile);
        final LocalDateTime finish = LocalDateTime.now();
        System.out.println("Finish time is " + finish);
        Duration duration = Duration.between(start, finish);
        System.out.println("Total time taken: " + duration.getSeconds());
        System.out.println("PASSED: " + fileScanner.getSuccessCount());
        System.out.println("FAILED: " + fileScanner.getFailureCount());
    }
}
