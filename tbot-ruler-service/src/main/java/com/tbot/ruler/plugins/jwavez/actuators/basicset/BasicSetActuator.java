package com.tbot.ruler.plugins.jwavez.actuators.basicset;

import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.payload.BinaryClaim;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class BasicSetActuator extends AbstractActuator {

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

    public void acceptCommandValue(byte commandValue) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getUuid())
                .payload(messagePayload(commandValue))
                .build());
    }

    private Object messagePayload(byte commandValue) {
        switch(valueMode) {
            case TOGGLE_VALUE:
                return BinaryClaim.TOGGLE;
            case ON_OFF_VALUES:
                return (commandValue == (byte) configuration.getTurnOnValue()) ?
                        BinaryClaim.SET_ON : BinaryClaim.SET_OFF;
            default:
                throw new MessageProcessingException("Unexpected implementation inconsistency!");
        }
    }
}
