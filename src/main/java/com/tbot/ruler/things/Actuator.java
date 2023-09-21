package com.tbot.ruler.things;

import com.tbot.ruler.broker.MessageReceiver;
import com.tbot.ruler.broker.MessageSender;
import com.tbot.ruler.broker.model.MessagePublicationReport;

public interface Actuator extends TaskBasedItem, MessageReceiver, MessageSender {

    default void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }
}
