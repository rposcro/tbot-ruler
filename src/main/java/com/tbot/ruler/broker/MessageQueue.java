package com.tbot.ruler.broker;

import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePublisher;
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

    public void publish(String senderId, MessagePayload messagePayload) {
        publishMessage(Message.builder()
                .senderId(senderId)
                .payload(messagePayload)
                .build());
    }

    public void publish(Message message) {
        publishMessage(message);
    }

    @Override
    public void publishMessage(Message message) {
        log.debug("Enqueued message from {} with payload {}", message.getSenderId(), message.getPayload().getClass().getSimpleName());
        messageQueue.add(message);
    }

    protected Message nextMessage() throws InterruptedException {
        return messageQueue.take();
    }
}
