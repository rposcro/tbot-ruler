package com.tbot.ruler.plugins.jwavez.basicset;

import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.message.Message;

import java.util.function.Consumer;

public class BasicSetMessageConsumer implements Consumer<Message> {

    @Override
    public void accept(Message message) {
        throw new MessageProcessingException("Message collection not supported yet");
    }
}
