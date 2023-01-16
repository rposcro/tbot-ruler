package com.tbot.ruler.message;

@FunctionalInterface
public interface MessagePublisher {

    void publishMessage(Message message);
}
