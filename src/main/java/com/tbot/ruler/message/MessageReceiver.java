package com.tbot.ruler.message;

@FunctionalInterface
public interface MessageReceiver {

    void acceptMessage(Message message);
}
