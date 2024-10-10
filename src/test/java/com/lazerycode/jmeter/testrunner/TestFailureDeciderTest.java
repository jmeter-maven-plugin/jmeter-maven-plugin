package com.lazerycode.jmeter.testrunner;

import com.lazerycode.jmeter.results.ResultScanner;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TestFailureDeciderTest {
    private static class MockResultScanner extends ResultScanner {

        public MockResultScanner(int successCount, int failureCount) {
            super(true, true, true, Collections.emptyList());
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        @Override
        public int getSuccessCount() {
            return successCount;
        }

        @Override
        public void parseResultFile(File file) {
            //NOOP
        }

        @Override
        public int getFailureCount() {
            return failureCount;
        }

        @Override
        public int getTotalCount() {
            return getSuccessCount() + getFailureCount();
        }

    }

    public TestFailureDeciderTest() {
        super();
    }

    @Test(expected = IllegalStateException.class)
    public void testRunChecksNotCalled() {
        ResultScanner resultScanner = new MockResultScanner(10, 10);
        TestFailureDecider decider = new TestFailureDecider(true, 2, resultScanner);

        assertThat(decider.failBuild()).isFalse();
    }

    @Test
    public void testIgnoreFailure() {
        ResultScanner resultScanner = new MockResultScanner(10, 10);
        TestFailureDecider decider = new TestFailureDecider(true, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isFalse();
        assertThat(decider.getErrorPercentage()).isEqualTo(50);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isTrue();
    }

    @Test
    public void testTakeIntoAccountFailure() {
        ResultScanner resultScanner = new MockResultScanner(10, 10);
        TestFailureDecider decider = new TestFailureDecider(false, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isTrue();
        assertThat(decider.getErrorPercentage()).isEqualTo(50);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateUnderThreshold() {
        ResultScanner resultScanner = new MockResultScanner(100, 1);
        TestFailureDecider decider = new TestFailureDecider(false, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isFalse();
        assertThat(decider.getErrorPercentage()).isEqualTo(0.99);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateEqualThreshold() {
        ResultScanner resultScanner = new MockResultScanner(100, 2);
        TestFailureDecider decider = new TestFailureDecider(false, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isFalse();
        assertThat(decider.getErrorPercentage()).isEqualTo(1.96);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateOverThreshold() {
        ResultScanner resultScanner = new MockResultScanner(100, 3);
        TestFailureDecider decider = new TestFailureDecider(false, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isTrue();
        assertThat(decider.getErrorPercentage()).isEqualTo(2.91);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateWithDefaultThreshold() {
        ResultScanner resultScanner = new MockResultScanner(100, 1);
        TestFailureDecider decider = new TestFailureDecider(false, 0, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isTrue();
        assertThat(decider.getErrorPercentage()).isEqualTo(0.99);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(0);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateOverThresholdRounding() {
        ResultScanner resultScanner = new MockResultScanner(10000, 3);
        TestFailureDecider decider = new TestFailureDecider(false, 0.02, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isTrue();
        assertThat(decider.getErrorPercentage()).isEqualTo(0.03);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(0.02);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateOverThresholdRounding2() {
        ResultScanner resultScanner = new MockResultScanner(10000, 2);
        TestFailureDecider decider = new TestFailureDecider(false, 0.02, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isFalse();
        assertThat(decider.getErrorPercentage()).isEqualTo(0.02);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(0.02);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void NoResultsFound() {
        ResultScanner resultScanner = new MockResultScanner(0, 0);
        TestFailureDecider decider = new TestFailureDecider(false, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isFalse();
        assertThat(decider.getErrorPercentage()).isEqualTo(0);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }
}
