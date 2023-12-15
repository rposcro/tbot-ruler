package com.tbot.ruler.jobs;

public interface Job {

    void doJob() throws InterruptedException;

    default String getName() {
        return this.toString();
    }
}
