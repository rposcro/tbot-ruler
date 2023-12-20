package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.Message;

@FunctionalInterface
public interface MessageReceiver {

    void acceptMessage(Message message);
}
