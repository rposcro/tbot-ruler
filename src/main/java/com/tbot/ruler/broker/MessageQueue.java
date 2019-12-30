package com.tbot.ruler.broker;

import com.tbot.ruler.things.ItemId;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.things.service.MessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class MessageQueue implements MessagePublisher {

    private LinkedBlockingQueue<Message> messageQueue;

    @Autowired
    public MessageQueue(@Value("${ruler.broker.messageQueueLength:50}") int queueSize) {
        log.info("Emission queue length set to {}", queueSize);
        messageQueue = new LinkedBlockingQueue<>(queueSize);
    }

    public void publish(ItemId senderId, MessagePayload messagePayload) {
        accept(Message.builder()
                .senderId(senderId)
                .payload(messagePayload)
                .build());
    }

    public void publish(Message message) {
        accept(message);
    }

    @Override
    public void accept(Message message) {
        log.debug("Enqueued message from {} with payload {}", message.getSenderId().getValue(), message.getPayload().getClass().getSimpleName());
        messageQueue.add(message);
    }

    protected Message nextMessage() throws InterruptedException {
        return messageQueue.take();
    }
}
