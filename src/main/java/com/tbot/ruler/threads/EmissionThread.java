package com.tbot.ruler.threads;

import com.tbot.ruler.broker.model.Message;

import java.util.function.Consumer;

public interface EmissionThread {

    Runnable getRunnable();

    static EmissionThread ofMessage(Consumer<Message> signalConsumer, Message message) {
        return () -> () -> signalConsumer.accept(message);
    }

    static EmissionThread ofRunnable(Runnable runnable) {
        return () -> runnable;
    }
}
