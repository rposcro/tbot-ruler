package com.tbot.ruler.jobs;

import lombok.Getter;

@Getter
public class JobBundle {

    private final Job job;
    private final JobTrigger jobTrigger;

    public JobBundle(Job job, JobTrigger jobTrigger) {
        this.job = job;
        this.jobTrigger = jobTrigger;
    }

    public static JobBundle triggerableJobBundle(Job job, JobTrigger jobTrigger) {
        return new JobBundle(job, jobTrigger);
    }

    public static JobBundle periodicalJobBundle(Job job, long period) {
        return new JobBundle(job, JobTrigger.periodicalTrigger(period));
    }

    public static JobBundle continuousJobBundle(Job job) {
        return new JobBundle(job, JobTrigger.instantTrigger());
    }

    public static JobBundle oneTimeJobBundle(Job job) {
        return new JobBundle(job, JobTrigger.oneTimeTrigger());
    }
}
