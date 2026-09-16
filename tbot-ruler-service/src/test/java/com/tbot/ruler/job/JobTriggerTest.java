package com.tbot.ruler.job;

import com.tbot.ruler.jobs.JobTrigger;
import com.tbot.ruler.jobs.JobTriggerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JobTriggerTest {

    @Test
    public void testOneTimeJobTrigger() {
        JobTrigger jobTrigger = JobTrigger.oneTimeTrigger();
        assertFalse(jobTrigger.shouldRunAgain());
        assertTrue(jobTrigger.nextDoJobTime(new JobTriggerContext()) <= System.currentTimeMillis());
    }

    @Test
    public void testInstantJobTrigger() {
        JobTrigger jobTrigger = JobTrigger.instantTrigger();
        assertTrue(jobTrigger.shouldRunAgain());
        assertTrue(jobTrigger.nextDoJobTime(new JobTriggerContext()) <= System.currentTimeMillis());
    }

    @Test
    public void testPeriodicalJobTrigger() {
        JobTrigger jobTrigger = JobTrigger.periodicalTrigger(1000);
        JobTriggerContext jobTriggerContext = JobTriggerContext.builder()
            .lastCompletionTime(System.currentTimeMillis())
            .build();
        assertTrue(jobTrigger.shouldRunAgain());
        assertEquals(jobTriggerContext.getLastCompletionTime() + 1000, jobTrigger.nextDoJobTime(jobTriggerContext));
    }
}
