package com.tbot.ruler.subjects.actuator;

import com.tbot.ruler.broker.MessageReceiver;
import com.tbot.ruler.broker.MessageSender;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.subjects.Subject;
import org.slf4j.LoggerFactory;

public interface Actuator extends Subject, MessageReceiver, MessageSender {

    default void triggerAction() {
    }

    default void acceptMessage(Message message) {
        LoggerFactory.getLogger(Actuator.class)
                .info("Actuator {} ignored message from {}", getUuid(), message.getSenderId());
    }

    default void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }

    default ActuatorState getState() {
        return ActuatorState.builder()
                .actuatorUuid(getUuid())
                .build();
    };
}
