package com.tbot.ruler.broker;

import com.tbot.ruler.exceptions.MessageException;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.MessagePublicationReport.publicationReportBuilder;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.service.things.BindingsService;
import com.tbot.ruler.task.AbstractTask;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
public class MessagePublishBroker extends AbstractTask {

    private final MessageQueueComponent messageQueue;
    private final BindingsService bindingsService;
    private final MessagePublicationManager messagePublicationManager;

    @Builder
    @Autowired
    public MessagePublishBroker(
            MessageQueueComponent messageQueue, BindingsService bindingsService, MessagePublicationManager messagePublicationManager) {
        this.messageQueue = messageQueue;
        this.bindingsService = bindingsService;
        this.messagePublicationManager = messagePublicationManager;
    }

    @Override
    public void runIteration() {
        try {
            Message message = messageQueue.nextMessage();
            MessagePublicationReport publicationReport = dispatchMessage(message);
            messageQueue.enqueueReport(publicationReport);
        } catch(Exception e) {
            log.error("Dispatch interrupted by unexpected internal error", e);
        }
    }

    private MessagePublicationReport dispatchMessage(Message message) {
        publicationReportBuilder reportBuilder = MessagePublicationReport.builder().originalMessage(message);
        Collection<String> receivers = extractReceivers(message);

        if (messagePublicationManager.isSenderSuspended(message.getSenderId())) {
            log.info("Dispatch from {}: Suspended", message.getSenderId());
            reportBuilder.senderSuspended(true);
        } else if (receivers.isEmpty()) {
            log.warn("Dispatch from {}: No bindings found", message.getSenderId());
        } else {
            receivers.stream()
                .forEach(receiverId -> {
                    try {
                        log.info("Dispatch from {}: Delivering to {}", message.getSenderId(), receiverId);
                        this.deliverMessage(message, receiverId);
                        reportBuilder.successfulReceiver(receiverId);
                    } catch(MessageException e) {
                        reportBuilder.failedReceiver(receiverId);
                        log.error("Dispatch from " + message.getSenderId() + ": Failed to deliver to " + receiverId, e);
                    }
                });
        }

        return reportBuilder.build();
    }

    private Collection<String> extractReceivers(Message message) {
        if (message.getReceiverId() != null) {
            return Collections.singleton(message.getReceiverId());
        } else {
            return bindingsService.findReceiversUuidsBySenderUuid(message.getSenderId());
        }
    }

    private void deliverMessage(Message message, String receiverId) {
        MessageReceiver messageReceiver = bindingsService.findReceiverByUuid(receiverId);
        messageReceiver.acceptMessage(message);
    }
}
