package com.tbot.ruler.broker;

import com.tbot.ruler.exceptions.MessageException;
import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.DeliveryReport.DeliveryReportBuilder;
import com.tbot.ruler.message.MessageSender;
import com.tbot.ruler.service.DeliveryReportListenerService;
import com.tbot.ruler.service.things.BindingsService;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessageReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class MessageBroker implements Runnable {

    @Autowired
    private MessageQueue messageQueue;

    @Autowired
    private BindingsService bindingsService;

    @Autowired
    private DeliveryReportListenerService listenerService;

    @Override
    public void run() {
        while(true) {
            try {
                Message message = messageQueue.nextMessage();
                DeliveryReport deliveryReport = distributeMessage(message);
                deliverReport(message, deliveryReport);
            } catch(Exception e) {
                log.error("Message dispatching interrupted by unexpected internal error", e);
            }
        }
    }

    private DeliveryReport distributeMessage(Message message) {
        DeliveryReportBuilder reportBuilder = DeliveryReport.builder().originalMessage(message);
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

    private void deliverReport(Message message, DeliveryReport deliveryReport) {
        MessageSender messageSender = bindingsService.messageSenderById(message.getSenderId());
        messageSender.acceptDeliveryReport(deliveryReport);
        listenerService.acceptDeliveryReport(deliveryReport);
    }

    private void deliverMessage(Message message, String receiverId) {
        MessageReceiver messageReceiver = bindingsService.messageReceiverById(receiverId);
        messageReceiver.acceptMessage(message);
    }
}
