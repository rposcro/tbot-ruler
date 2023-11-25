package com.tbot.ruler.task;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class AbstractTask implements Task {

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicBoolean isStopping = new AtomicBoolean(false);

    protected abstract void runIteration() throws InterruptedException;

    @Override
    public void run() {
        if (!isRunning.compareAndExchange(false, true)) {
            log.info("Task started: {}", this.getName());

            try {
                while (!isStopping.get()) {
                    runIteration();
                }
            } catch(InterruptedException e) {
                log.info("Task interrupted: {}", this.getName());
            }

            isRunning.set(false);
            isStopping.set(false);
            log.info("Task stopped: {}", this.getName());
        }
    }

    @Override
    public void stop() {
        log.info("Task stop requested: {}", this.getName());
        isStopping.set(true);
        while(!isStopping.get());
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }
}
