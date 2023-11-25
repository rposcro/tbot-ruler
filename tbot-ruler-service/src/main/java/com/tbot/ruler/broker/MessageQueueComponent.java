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

    private final static long POLL_TIMEOUT = 1000;

    private LinkedBlockingQueue<Message> messageQueue;
    private LinkedBlockingQueue<MessagePublicationReport> reportQueue;

    @Autowired
    public MessageQueueComponent(@Value("${ruler.broker.messageQueueLength:50}") int queueSize) {
        log.info("Message queue length set to {}", queueSize);
        messageQueue = new LinkedBlockingQueue<>(queueSize);
        reportQueue = new LinkedBlockingQueue<>(queueSize);
    }

    public void emptyQueues() {
        messageQueue.clear();
        reportQueue.clear();
    }

    protected void enqueueMessage(Message message) {
        if (this.messageQueue.offer(message)) {
            log.debug("Enqueued message from {} with payload {}",
                    message.getSenderId(), message.getPayload().getClass().getSimpleName());
        } else {
            log.warn("Message queue full, dropped message from {}", message.getSenderId());
        }
    }

    protected void enqueueReport(MessagePublicationReport publicationReport) {
        if (this.reportQueue.offer(publicationReport)) {
            log.debug("Enqueued delivery report for {}", publicationReport.getOriginalMessage().getSenderId());
        } else {
            log.warn("Report queue full, dropped report message for {}", publicationReport.getOriginalMessage().getSenderId());
        }
    }

    protected Message nextMessage() {
        try {
            return messageQueue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    protected MessagePublicationReport nextReport() {
        try {
            return reportQueue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    protected MessagePublicationReport nextReport(long timeout) throws InterruptedException {
        return reportQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }
}
