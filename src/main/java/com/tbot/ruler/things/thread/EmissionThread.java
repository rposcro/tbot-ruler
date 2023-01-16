package com.tbot.ruler.things.thread;

import com.tbot.ruler.messages.model.Message;

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
