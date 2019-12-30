package com.tbot.ruler.things.service;

import com.tbot.ruler.message.Message;

@FunctionalInterface
public interface MessageConsumer {

    void acceptMessage(Message message);
}
