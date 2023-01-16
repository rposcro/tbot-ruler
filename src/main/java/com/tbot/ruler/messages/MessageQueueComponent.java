package com.tbot.ruler.messages;

import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MessageQueueComponent {

    private LinkedBlockingQueue<Message> messageQueue;
    private LinkedBlockingQueue<MessageDeliveryReport> deliveryReportQueue;

    @Autowired
    public MessageQueueComponent(@Value("${ruler.broker.messageQueueLength:50}") int queueSize) {
        log.info("Message queue length set to {}", queueSize);
        messageQueue = new LinkedBlockingQueue<>(queueSize);
        deliveryReportQueue = new LinkedBlockingQueue<>(queueSize);
    }

    protected void enqueueMessage(Message message) {
        this.messageQueue.add(message);
        log.debug("Enqueued message from {} with payload {}", message.getSenderId(), message.getPayload().getClass().getSimpleName());
    }

    protected void enqueueDeliveryReport(MessageDeliveryReport deliveryReport) {
        this.deliveryReportQueue.add(deliveryReport);
        log.debug("Enqueued delivery report for {}", deliveryReport.getOriginalMessage().getSenderId());
    }

    protected Message nextMessage() throws InterruptedException {
        return messageQueue.take();
    }

    protected Message nextMessage(long timeout) throws InterruptedException {
        return messageQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    protected MessageDeliveryReport nextDeliveryReport() throws InterruptedException {
        return deliveryReportQueue.take();
    }

    protected MessageDeliveryReport nextDeliveryReport(long timeout) throws InterruptedException {
        return deliveryReportQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }
}
