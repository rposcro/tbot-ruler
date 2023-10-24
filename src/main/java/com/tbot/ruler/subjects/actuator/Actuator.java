package com.tbot.ruler.subjects.actuator;

import com.tbot.ruler.broker.MessageReceiver;
import com.tbot.ruler.broker.MessageSender;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.subjects.Subject;

public interface Actuator extends Subject, MessageReceiver, MessageSender {

    default void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }

    default ActuatorState getState() {
        return ActuatorState.builder()
                .actuatorUuid(getUuid())
                .build();
    };
}
