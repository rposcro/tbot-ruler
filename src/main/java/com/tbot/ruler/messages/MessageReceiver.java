package com.tbot.ruler.messages;

import com.tbot.ruler.messages.model.Message;

@FunctionalInterface
public interface MessageReceiver {

    void acceptMessage(Message message);
}
