package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultMessagePublisher implements MessagePublisher {

    @Autowired
    private MessageQueueComponent messageQueue;

    public void publishMessage(Message message) {
        messageQueue.enqueueMessage(message);
    }
}
