package com.tbot.ruler.plugins.jwavez.basicset;

import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.broker.model.MessageDeliveryReport;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Actuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class BasicSetActuator extends AbstractItem implements Actuator {

    private MessagePublisher messagePublisher;
    private BasicSetValueMode valueMode;
    private BasicSetConfiguration configuration;

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

    public boolean acceptsCommand(byte sourceNodeId) {
        return !configuration.isMultiChannelOn()
                && (byte) configuration.getNodeId() == sourceNodeId;
    }

    public boolean acceptsCommand(byte sourceNodeId, byte sourceEndpointId) {
        return configuration.isMultiChannelOn()
                && ((byte) configuration.getNodeId() == sourceNodeId)
                && (byte) configuration.getSourceEndPointId() == sourceEndpointId;
    }

    public void acceptCommandValue(byte commandValue) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getUuid())
                .payload(messagePayload(commandValue))
                .build());
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
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

    @Override
    public void acceptMessage(Message message) {
    }
}
