package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.Message;
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
    private LinkedBlockingQueue<MessagePublicationReport> reportQueue;

    @Autowired
    public MessageQueueComponent(@Value("${ruler.broker.messageQueueLength:50}") int queueSize) {
        log.info("Message queue length set to {}", queueSize);
        messageQueue = new LinkedBlockingQueue<>(queueSize);
        reportQueue = new LinkedBlockingQueue<>(queueSize);
    }

    protected void enqueueMessage(Message message) {
        this.messageQueue.add(message);
        log.debug("Enqueued message from {} with payload {}",
                message.getSenderId(), message.getPayload().getClass().getSimpleName());
    }

    protected void enqueueReport(MessagePublicationReport publicationReport) {
        this.reportQueue.add(publicationReport);
        log.debug("Enqueued delivery report for {}", publicationReport.getOriginalMessage().getSenderId());
    }

    protected Message nextMessage() throws InterruptedException {
        return messageQueue.take();
    }

    protected Message nextMessage(long timeout) throws InterruptedException {
        return messageQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    protected MessagePublicationReport nextReport() throws InterruptedException {
        return reportQueue.take();
    }

    protected MessagePublicationReport nextReport(long timeout) throws InterruptedException {
        return reportQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }
}
