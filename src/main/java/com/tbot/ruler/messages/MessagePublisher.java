package com.tbot.ruler.messages;

import com.tbot.ruler.messages.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {

    @Autowired
    private MessageQueueComponent messageQueue;

    public void publishMessage(Message message) {
        messageQueue.enqueueMessage(message);
    }
}
