package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.MessagePublicationReport;

public interface MessagePublicationReportListener {

    default void publicationReportDelivered(MessagePublicationReport publicationReport) {
    }

    default void publicationReportSkipped(MessagePublicationReport publicationReport) {
    }
}
