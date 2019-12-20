package com.tbot.ruler.plugins.jwavez.basicset;

import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.BooleanTogglePayload;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.things.ItemId;
import com.tbot.ruler.things.service.MessagePublisher;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BasicSetEmissionProducer {

    private ItemId actuatorId;
    private BasicSetValueMode valueMode;
    private MessagePublisher messagePublisher;

    private byte sourceNodeId;
    private byte turnOnValue;
    private byte turnOffValue;

    public void acceptCommandValue(byte commandValue) {
        messagePublisher.accept(Message.builder()
            .senderId(actuatorId)
            .payload(messagePayload(commandValue))
            .build());
    }

    private MessagePayload messagePayload(byte commandValue) {
        switch(valueMode) {
            case TOGGLE_VALUE:
                return BooleanTogglePayload.TOGGLE_PAYLOAD;
            case ON_OFF_VALUES:
                return BooleanUpdatePayload.of(commandValue == turnOnValue);
            default:
                throw new MessageProcessingException("Unexpected implementation inconsistency!");
        }
    }
}
