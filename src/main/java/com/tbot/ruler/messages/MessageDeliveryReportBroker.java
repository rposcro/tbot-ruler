package com.tbot.ruler.messages;

import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.service.things.BindingsService;
import lombok.Builder;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MessageDeliveryReportBroker implements Runnable {

    private MessageQueueComponent messageQueue;
    private BindingsService bindingsService;
    private List<MessageDeliveryReportListener> deliveryListeners;

    @Builder
    @Autowired
    public MessageDeliveryReportBroker(
            MessageQueueComponent messageQueue,
            BindingsService bindingsService,
            @Singular List<MessageDeliveryReportListener> deliveryListeners) {
        this.messageQueue = messageQueue;
        this.bindingsService = bindingsService;
        this.deliveryListeners = deliveryListeners;
    }

    @Override
    public void run() {
        while(true) {
            try {
                MessageDeliveryReport deliveryReport = messageQueue.nextDeliveryReport();
                deliverReport(deliveryReport);
            } catch(Exception e) {
                log.error("Message delivery report broker interrupted by unexpected internal error", e);
            }
        }
    }

    private void deliverReport(MessageDeliveryReport deliveryReport) {
        MessageSender messageSender = bindingsService.messageSenderById(deliveryReport.getOriginalMessage().getSenderId());
        messageSender.acceptDeliveryReport(deliveryReport);
        deliveryListeners.forEach(listener -> listener.deliveryReportCompleted(deliveryReport));
    }
}
