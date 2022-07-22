package com.tbot.ruler.message;

@FunctionalInterface
public interface MessagePublisher {

    void acceptMessage(Message message);
}
