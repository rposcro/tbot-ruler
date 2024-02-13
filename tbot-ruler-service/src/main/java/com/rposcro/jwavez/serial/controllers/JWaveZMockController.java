package com.rposcro.jwavez.serial.controllers;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.controllers.mock.JWaveZMockRules;
import com.rposcro.jwavez.serial.controllers.mock.PeriodicFrame;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.ResponseHandler;
import lombok.Builder;
import lombok.NonNull;

import java.util.PriorityQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

public class JWaveZMockController {

    private final JWaveZMockRules mockRules;
    private final ResponseHandler responseHandler;
    private final CallbackHandler callbackHandler;

    private final PriorityQueue<PeriodicFrame> periodicCallbacks;
    private final AtomicBoolean isRunning;
    private final AtomicBoolean isTerminated;

    @Builder
    public JWaveZMockController(
            @NonNull JWaveZMockRules mockRules,
            @NonNull ResponseHandler responseHandler,
            @NonNull CallbackHandler callbackHandler) {
        this.mockRules = mockRules;
        this.responseHandler = responseHandler;
        this.callbackHandler = callbackHandler;

        this.isRunning = new AtomicBoolean(false);
        this.isTerminated = new AtomicBoolean(false);
        this.periodicCallbacks = new PriorityQueue<>();

        mockRules.getPeriodicCallbacks().entrySet().forEach(entry -> {
            PeriodicFrame frame = new PeriodicFrame(entry.getValue(), entry.getKey());
            periodicCallbacks.add(frame);
        });
    }

    public void connect() {
        if (!isTerminated.get() && isRunning.compareAndSet(false, true)) {
            ForkJoinPool.commonPool().execute(getRunnable());
        }
    }

    public void close() {
        isTerminated.set(true);
        isRunning.set(false);
    }

    private Runnable getRunnable() {
        return () -> {
            try {
                while (!isTerminated.get()) {
                    Thread.sleep(1000);
                    processPeriodicCallbacks();
                }
            } catch(InterruptedException e) {
                isTerminated.set(true);
                isRunning.set(false);
                throw new RuntimeException("Mock controller interrupted!", e);
            }
        };
    }

    private void processPeriodicCallbacks() {
        long now = System.currentTimeMillis();
        boolean hasNext = true;
        while(hasNext) {
            PeriodicFrame periodic = periodicCallbacks.peek();
            if (periodic == null || now < periodic.getNextTriggerTime()) {
                hasNext = false;
            } else {
                triggerCallback(periodic.getFrameBuffer());
                periodic.setNextTriggerTime(now + periodic.getPeriodMilliseconds());
                periodicCallbacks.poll();
                periodicCallbacks.add(periodic);
            }
        };
    }

    private void triggerCallback(ImmutableBuffer frame) {
        callbackHandler.accept(frame);
    }
}
