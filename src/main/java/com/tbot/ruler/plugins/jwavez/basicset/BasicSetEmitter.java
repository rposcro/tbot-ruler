package com.tbot.ruler.plugins.jwavez.basicset;

import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.MessagePublisher;
import com.tbot.ruler.message.payloads.BooleanTogglePayload;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.EmitterId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class BasicSetEmitter extends AbstractItem<EmitterId> implements Emitter {

    private MessagePublisher messagePublisher;
    private BasicSetValueMode valueMode;
    private BasicSetEmitterConfiguration configuration;

    @Builder
    public BasicSetEmitter(
        @NonNull EmitterId id,
        @NonNull String name,
        String description,
        @NonNull MessagePublisher messagePublisher,
        @NonNull BasicSetEmitterConfiguration configuration
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
        messagePublisher.acceptMessage(Message.builder()
                .senderId(this.getId())
                .payload(messagePayload(commandValue))
                .build());
    }

    @Override
    public void acceptDeliveryReport(DeliveryReport deliveryReport) {
    }

    private MessagePayload messagePayload(byte commandValue) {
        switch(valueMode) {
            case TOGGLE_VALUE:
                return BooleanTogglePayload.TOGGLE_PAYLOAD;
            case ON_OFF_VALUES:
                return BooleanUpdatePayload.of(commandValue == (byte) configuration.getTurnOnValue());
            default:
                throw new MessageProcessingException("Unexpected implementation inconsistency!");
        }
    }
}
