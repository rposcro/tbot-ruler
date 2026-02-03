package com.tbot.ruler.job;

import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.jobs.JobRunner;
import com.tbot.ruler.jobs.JobTrigger;
import com.tbot.ruler.jobs.JobTriggerContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JobRunnerTest {

    @Test
    public void shouldRunOnce_whenOneTimeTriggerIsUsed() {
        TestJob job = new TestJob();
        JobTrigger trigger = JobTrigger.oneTimeTrigger();
        JobRunner jobRunner = new JobRunner(job, trigger);

        jobRunner.run();
        assertEquals(1, job.runCounter);
        assertFalse(jobRunner.isRunning());
    }

    @Test
    public void shouldRunMultipleTimes() {
        int expectedCallCount = 3;
        TestJob job = new TestJob();
        JobTrigger trigger = new TestJobTrigger(expectedCallCount);
        JobRunner jobRunner = new JobRunner(job, trigger);

        jobRunner.run();
        assertEquals(expectedCallCount, job.runCounter);
        assertFalse(jobRunner.isRunning());
    }

    private class TestJob implements Job {

        int runCounter = 0;

        @Override
        public void doJob() {
            runCounter++;
        }
    }

    private class TestJobTrigger implements JobTrigger {

        private final int expectedCallCount;
        private int callCount = 0;

        private TestJobTrigger(int expectedCallCount) {
            this.expectedCallCount = expectedCallCount;
        }

        @Override
        public long nextDoJobTime(JobTriggerContext context) {
            callCount++;
            return System.currentTimeMillis();
        }

        @Override
        public boolean shouldRunAgain() {
            return callCount < expectedCallCount;
        }
    }
}
