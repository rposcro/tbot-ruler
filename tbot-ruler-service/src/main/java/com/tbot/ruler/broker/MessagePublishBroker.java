package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.MessagePublicationReport.publicationReportBuilder;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.service.things.BindingsService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
public class MessagePublishBroker implements Job {

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
    public void doJob() {
        try {
            Message message = messageQueue.nextMessage();
            if (message != null) {
                MessagePublicationReport publicationReport = dispatchMessage(message);
                messageQueue.enqueueReport(publicationReport);
            }
        } catch(RuntimeException e) {
            log.error("Message Dispatch: Interrupted by unexpected internal error", e);
        }
    }

    private MessagePublicationReport dispatchMessage(Message message) {
        publicationReportBuilder reportBuilder = MessagePublicationReport.builder().originalMessage(message);
        Collection<String> receivers = extractReceivers(message);

        if (messagePublicationManager.isSenderSuspended(message.getSenderId())) {
            log.info("Message Dispatch: Suspended from sender {}", message.getSenderId());
            reportBuilder.senderSuspended(true);
        } else if (receivers.isEmpty()) {
            log.warn("Message Dispatch: No bindings found for sender {}", message.getSenderId());
        } else {
            receivers.stream()
                .forEach(receiverId -> {
                    try {
                        log.debug("Message Dispatch: Delivering from {} to {}", message.getSenderId(), receiverId);
                        if (deliverMessage(message, receiverId)) {
                            reportBuilder.successfulReceiver(receiverId);
                        } else {
                            reportBuilder.failedReceiver(receiverId);
                        }
                    } catch(Exception e) {
                        reportBuilder.failedReceiver(receiverId);
                        log.error("Message Dispatch: Failed to deliver from " + message.getSenderId() + " to " + receiverId, e);
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

    private boolean deliverMessage(Message message, String receiverUuid) {
        MessageReceiver messageReceiver = bindingsService.findReceiverByUuid(receiverUuid);
        if (messageReceiver == null) {
            log.warn("Message Dispatch: No active receiver found for uuid {}", receiverUuid);
            return false;
        }
        messageReceiver.acceptMessage(message);
        return true;
    }
}
