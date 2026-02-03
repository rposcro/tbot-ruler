package com.tbot.ruler.jobs;

public interface Job {

    void doJob() throws InterruptedException;

    default String getJobName() {
        return this.toString();
    }

    static Job namedJob(String name, Runnable job) {
        return new Job() {
            @Override
            public void doJob() {
                job.run();
            }

            @Override
            public String getJobName() {
                return name;
            }
        };
    }
}
