package com.tbot.ruler.broker;

import com.tbot.ruler.things.ItemId;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class MessageQueue {

    private LinkedBlockingQueue<Message> messageQueue;

    @Autowired
    public MessageQueue(@Value("${ruler.broker.messageQueueLength:50}") int queueSize) {
        log.info("Emission queue length set to {}", queueSize);
        messageQueue = new LinkedBlockingQueue<>(queueSize);
    }

    public void publish(ItemId senderId, MessagePayload messagePayload) {
        messageQueue.add(Message.builder()
                .senderId(senderId)
                .payload(messagePayload)
                .build());
    }

    protected Message nextMessage() throws InterruptedException {
        return messageQueue.take();
    }
}
