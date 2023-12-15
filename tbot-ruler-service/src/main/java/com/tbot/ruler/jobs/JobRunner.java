package com.tbot.ruler.jobs;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class JobRunner implements Runnable {

    private final Job job;
    private final JobTrigger jobTrigger;
    private final JobTriggerContext triggerContext;

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicBoolean isStopping = new AtomicBoolean(false);

    @Builder
    public JobRunner(Job job, JobTrigger jobTrigger) {
        this.job = job;
        this.jobTrigger = jobTrigger;
        this.triggerContext = new JobTriggerContext();
    }

    public JobRunner(JobBundle jobBundle) {
        this(jobBundle.getJob(), jobBundle.getJobTrigger());
    }

    @Override
    public final void run() {
        if (!isRunning.compareAndExchange(false, true)) {
            log.info("Job Runner: {} started", this.job.getName());

            try {
                while (!isStopping.get()) {
                    long nextDoJobTime = jobTrigger.nextDoJobTime(triggerContext);
                    log.info("Job Runner: Next emission time for {} is {}", job.getName(), new Date(nextDoJobTime));

                    long sleepTime = nextDoJobTime - System.currentTimeMillis();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }

                    job.doJob();

                    triggerContext.setLastScheduledExecutionTime(nextDoJobTime);
                    triggerContext.setLastCompletionTime(System.currentTimeMillis());
                }
            } catch(InterruptedException e) {
                log.info("Job Runner: {} interrupted", job.getName());
            }

            isStopping.set(false);
            isRunning.set(false);
            log.info("Job Runner: {} stopped", job.getName());
        } else {
            log.info("Job Runner: {} is already started", job.getName());
        }
    }

    public void stop() {
        log.info("Job Runner: {} stop requested: {}", job.getName());
        isStopping.set(true);
        while(!isStopping.get());
    }
}
