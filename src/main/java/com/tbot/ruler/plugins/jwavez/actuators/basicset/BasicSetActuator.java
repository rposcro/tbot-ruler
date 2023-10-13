package com.tbot.ruler.plugins.jwavez.actuators.basicset;

import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.Actuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class BasicSetActuator extends AbstractSubject implements Actuator {

    private final MessagePublisher messagePublisher;
    private final BasicSetValueMode valueMode;
    private final BasicSetConfiguration configuration;

    @Builder
    public BasicSetActuator(
        @NonNull String id,
        @NonNull String name,
        String description,
        @NonNull MessagePublisher messagePublisher,
        @NonNull BasicSetConfiguration configuration
    ) {
        super(id, name, description);
        this.messagePublisher = messagePublisher;
        this.valueMode = BasicSetValueMode.of(configuration.getValueMode());
        this.configuration = configuration;
    }

    @Override
    public void acceptMessage(Message message) {
    }

    @Override
    public void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }

    public void acceptCommandValue(byte commandValue) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getUuid())
                .payload(messagePayload(commandValue))
                .build());
    }

    private Object messagePayload(byte commandValue) {
        switch(valueMode) {
            case TOGGLE_VALUE:
                return BinaryStateClaim.TOGGLE;
            case ON_OFF_VALUES:
                return (commandValue == (byte) configuration.getTurnOnValue()) ?
                        BinaryStateClaim.SET_ON : BinaryStateClaim.SET_OFF;
            default:
                throw new MessageProcessingException("Unexpected implementation inconsistency!");
        }
    }
}
