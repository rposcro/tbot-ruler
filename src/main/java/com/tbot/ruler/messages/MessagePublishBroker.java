package com.tbot.ruler.messages;

import com.tbot.ruler.exceptions.MessageException;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.MessageDeliveryReport.DeliveryReportBuilder;
import com.tbot.ruler.messages.model.Message;
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

    @Builder
    @Autowired
    public MessagePublishBroker(MessageQueueComponent messageQueue, BindingsService bindingsService) {
        this.messageQueue = messageQueue;
        this.bindingsService = bindingsService;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Message message = messageQueue.nextMessage();
                MessageDeliveryReport deliveryReport = distributeMessage(message);
                messageQueue.enqueueDeliveryReport(deliveryReport);
            } catch(Exception e) {
                log.error("Message dispatching interrupted by unexpected internal error", e);
            }
        }
    }

    private MessageDeliveryReport distributeMessage(Message message) {
        DeliveryReportBuilder reportBuilder = MessageDeliveryReport.builder().originalMessage(message);
        Collection<String> consumers = bindingsService.findBindedMessageConsumerIds(message.getSenderId());

        if (consumers.isEmpty()) {
            log.warn("No bindings found for messages from {}", message.getSenderId());
        } else {
            consumers.stream()
                .forEach(receiverId -> {
                    try {
                        log.info("Dispatching message from {} to {}", message.getSenderId(), receiverId);
                        this.deliverMessage(message, receiverId);
                        reportBuilder.successfulReceiver(receiverId);
                    } catch(MessageException e) {
                        reportBuilder.failedReceiver(receiverId);
                        log.error("Consumer failed to process message from " + message.getSenderId() + " to " + receiverId, e);
                    }
                });
        }

        return reportBuilder.build();
    }

    private void deliverMessage(Message message, String receiverId) {
        MessageReceiver messageReceiver = bindingsService.messageReceiverById(receiverId);
        messageReceiver.acceptMessage(message);
    }
}
