package com.tbot.ruler.task;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class AbstractTask implements Task {

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicBoolean isStopping = new AtomicBoolean(false);

    protected abstract void runIteration();

    @Override
    public void run() {
        if (!isRunning.compareAndExchange(false, true)) {
            while (!isStopping.get()) {
                runIteration();
            }

            isRunning.set(false);
            isStopping.set(false);
        }
    }

    @Override
    public void stop() {
        isStopping.set(false);
        while(!isStopping.get());
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }
}
