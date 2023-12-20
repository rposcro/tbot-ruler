package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.MessagePublicationReport;

@FunctionalInterface
public interface MessageSender {

    void acceptPublicationReport(MessagePublicationReport publicationReport);
}
