package com.tbot.ruler.job;

import com.tbot.ruler.jobs.JobTriggerContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JobTriggerContextTest {

    @Test
    public void shouldBeTheFirstRun() {
        JobTriggerContext context = JobTriggerContext.builder()
            .build();

        assertTrue(context.isFirstRun());
    }

    @Test
    public void shouldNotBeTheFirstRun() {
        JobTriggerContext context = JobTriggerContext.builder()
            .lastCompletionTime(System.currentTimeMillis())
            .build();

        assertFalse(context.isFirstRun());
    }
}
