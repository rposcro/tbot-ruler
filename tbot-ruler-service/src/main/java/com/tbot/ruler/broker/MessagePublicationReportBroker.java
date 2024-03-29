package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.service.things.BindingsService;
import lombok.Builder;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MessagePublicationReportBroker implements Job {

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
    public void doJob() {
        try {
            MessagePublicationReport publicationReport = messageQueue.nextReport();
            if (publicationReport != null) {
                deliverReport(publicationReport);
            }
        } catch(Exception e) {
            log.error("Report Delivery: Interrupted by unexpected internal error", e);
        }
    }

    private void deliverReport(MessagePublicationReport publicationReport) {
        MessageSender messageSender = bindingsService.findSenderByUuid(publicationReport.getOriginalMessage().getSenderId());

        if (messageSender != null) {
            log.debug("Report Delivery: To {}", publicationReport.getOriginalMessage().getSenderId());
            messageSender.acceptPublicationReport(publicationReport);
            deliveryListeners.forEach(listener -> listener.publicationReportDelivered(publicationReport));
        } else {
            log.debug("Report Delivery: To unknown sender");
            deliveryListeners.forEach(listener -> listener.publicationReportSkipped(publicationReport));
        }
    }
}
