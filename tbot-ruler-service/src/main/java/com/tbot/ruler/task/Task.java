package com.tbot.ruler.task;

public interface Task extends Runnable {

    void run();
    void stop();
    boolean isRunning();

    default String getName() {
        return this.toString();
    }
}
