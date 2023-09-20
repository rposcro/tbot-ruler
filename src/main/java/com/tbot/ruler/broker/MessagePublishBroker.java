package com.tbot.ruler.broker;

import com.tbot.ruler.exceptions.MessageException;
import com.tbot.ruler.broker.model.MessageDeliveryReport;
import com.tbot.ruler.broker.model.MessageDeliveryReport.DeliveryReportBuilder;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.service.things.BindingsService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
public class MessagePublishBroker implements Runnable {

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
    public void run() {
        while(true) {
            try {
                Message message = messageQueue.nextMessage();
                MessageDeliveryReport deliveryReport = dispatchMessage(message);
                messageQueue.enqueueDeliveryReport(deliveryReport);
            } catch(Exception e) {
                log.error("Dispatch interrupted by unexpected internal error", e);
            }
        }
    }

    private MessageDeliveryReport dispatchMessage(Message message) {
        DeliveryReportBuilder reportBuilder = MessageDeliveryReport.builder().originalMessage(message);
        Collection<String> consumers = bindingsService.findReceiversUuidsBySenderUuid(message.getSenderId());

        if (messagePublicationManager.isSenderSuspended(message.getSenderId())) {
            log.info("Dispatch from {}: Suspended", message.getSenderId());
            reportBuilder.senderSuspended(true);
        } else if (consumers.isEmpty()) {
            log.warn("Dispatch from {}: No bindings found", message.getSenderId());
        } else {
            consumers.stream()
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

    private void deliverMessage(Message message, String receiverId) {
        MessageReceiver messageReceiver = bindingsService.findReceiverByUuid(receiverId);
        messageReceiver.acceptMessage(message);
    }
}
