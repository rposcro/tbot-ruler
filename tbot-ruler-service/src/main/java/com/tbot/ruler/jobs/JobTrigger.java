package com.tbot.ruler.jobs;

public interface JobTrigger {

    long nextDoJobTime(JobTriggerContext context);

    default boolean shouldRunAgain() {
        return true;
    }

    static JobTrigger instantTrigger() {
        return context -> System.currentTimeMillis();
    }

    static JobTrigger periodicalTrigger(long period) {
        return context -> context.getLastCompletionTime() + period;
    }

    static JobTrigger oneTimeTrigger() {
        return new JobTrigger() {
            @Override
            public long nextDoJobTime(JobTriggerContext context) {
                return System.currentTimeMillis();
            }

            @Override
            public boolean shouldRunAgain() {
                return false;
            }
        };
    }
}
