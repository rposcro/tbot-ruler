package com.tbot.ruler.things;

import com.tbot.ruler.signals.EmitterSignal;

import java.util.function.Consumer;

public interface EmissionThread {

    Runnable getRunnable();

    static EmissionThread ofSignal(Consumer<EmitterSignal> signalConsumer, EmitterSignal signal) {
        return () -> {
            return () -> signalConsumer.accept(signal);
        };
    }

    static EmissionThread ofRunnable(Runnable runnable) {
        return () -> runnable;
    }
}
