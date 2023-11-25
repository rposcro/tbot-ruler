package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.service.things.BindingsService;
import com.tbot.ruler.task.AbstractTask;
import lombok.Builder;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MessagePublicationReportBroker extends AbstractTask {

    private MessageQueueComponent messageQueue;
    private BindingsService bindingsService;
    private List<MessagePublicationReportListener> deliveryListeners;

    @Builder
    @Autowired
    public MessagePublicationReportBroker(
            MessageQueueComponent messageQueue,
            BindingsService bindingsService,
            @Singular List<MessagePublicationReportListener> deliveryListeners) {
        this.messageQueue = messageQueue;
        this.bindingsService = bindingsService;
        this.deliveryListeners = deliveryListeners;
    }

    @Override
    public void runIteration() {
        try {
            MessagePublicationReport publicationReport = messageQueue.nextReport();
            deliverReport(publicationReport);
        } catch(Exception e) {
            log.error("Message delivery report broker interrupted by unexpected internal error", e);
        }
    }

    private void deliverReport(MessagePublicationReport publicationReport) {
        MessageSender messageSender = bindingsService.findSenderByUuid(publicationReport.getOriginalMessage().getSenderId());

        if (messageSender != null) {
            messageSender.acceptPublicationReport(publicationReport);
            deliveryListeners.forEach(listener -> listener.publicationReportDelivered(publicationReport));
        } else {
            deliveryListeners.forEach(listener -> listener.publicationReportSkipped(publicationReport));
        }
    }
}
