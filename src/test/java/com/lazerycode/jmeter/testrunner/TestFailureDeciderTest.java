package com.lazerycode.jmeter.testrunner;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestFailureDeciderTest {
    private class MockResultScanner implements IResultScanner {
        private int successCount;
        private int failureCount;

        public MockResultScanner(int successCount, int failureCount) {
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        @Override
        public int getSuccessCount() {
            return successCount;
        }

        @Override
        public int getFailureCount() {
            return failureCount;
        }

    }

    public TestFailureDeciderTest() {
        super();
    }

    @Test(expected = IllegalStateException.class)
    public void testRunChecksNotCalled() {
        IResultScanner resultScanner = new MockResultScanner(10, 10);
        TestFailureDecider decider = new TestFailureDecider(true, 2, resultScanner);

        assertThat(decider.failBuild()).isFalse();
    }

    @Test
    public void testIgnoreFailure() {
        IResultScanner resultScanner = new MockResultScanner(10, 10);
        TestFailureDecider decider = new TestFailureDecider(true, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isFalse();
        assertThat(decider.getErrorPercentage()).isEqualTo(50);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isTrue();
    }

    @Test
    public void testTakeIntoAccountFailure() {
        IResultScanner resultScanner = new MockResultScanner(10, 10);
        TestFailureDecider decider = new TestFailureDecider(false, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isTrue();
        assertThat(decider.getErrorPercentage()).isEqualTo(50);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateUnderThreshold() {
        IResultScanner resultScanner = new MockResultScanner(100, 1);
        TestFailureDecider decider = new TestFailureDecider(false, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isFalse();
        assertThat(decider.getErrorPercentage()).isEqualTo(0.990099f);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateEqualThreshold() {
        IResultScanner resultScanner = new MockResultScanner(100, 2);
        TestFailureDecider decider = new TestFailureDecider(false, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isFalse();
        assertThat(decider.getErrorPercentage()).isEqualTo(1.9607844f);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateOverThreshold() {
        IResultScanner resultScanner = new MockResultScanner(100, 3);
        TestFailureDecider decider = new TestFailureDecider(false, 2, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isTrue();
        assertThat(decider.getErrorPercentage()).isEqualTo(2.9126215f);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(2);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateWithDefaultThreshold() {
        IResultScanner resultScanner = new MockResultScanner(100, 1);
        TestFailureDecider decider = new TestFailureDecider(false, 0, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isTrue();
        assertThat(decider.getErrorPercentage()).isEqualTo(0.990099f);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(0);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateOverThresholdRounding() {
        IResultScanner resultScanner = new MockResultScanner(10000, 3);
        TestFailureDecider decider = new TestFailureDecider(false, 0.02f, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isTrue();
        assertThat(decider.getErrorPercentage()).isEqualTo(0.029991003f);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(0.02f);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }

    @Test
    public void testErrorRateOverThresholdRounding2() {
        IResultScanner resultScanner = new MockResultScanner(10000, 2);
        TestFailureDecider decider = new TestFailureDecider(false, 0.02f, resultScanner);
        decider.runChecks();

        assertThat(decider.failBuild()).isFalse();
        assertThat(decider.getErrorPercentage()).isEqualTo(0.019996f);
        assertThat(decider.getErrorPercentageThreshold()).isEqualTo(0.02f);
        assertThat(decider.isIgnoreResultFailures()).isFalse();
    }
}
