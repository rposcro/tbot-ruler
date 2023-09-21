package com.tbot.ruler.subjects;

import com.tbot.ruler.broker.MessageReceiver;
import com.tbot.ruler.broker.MessageSender;
import com.tbot.ruler.broker.model.MessagePublicationReport;

public interface Actuator extends Subject, MessageReceiver, MessageSender {

    default void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }
}
