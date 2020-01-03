package com.tbot.ruler.appliances;

import com.tbot.ruler.appliances.state.RGBWState;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.RGBWUpdatePayload;
import com.tbot.ruler.things.ApplianceId;
import lombok.Getter;

import java.util.Optional;

public class RGBWAppliance extends AbstractAppliance<RGBWState> {

    public RGBWAppliance(ApplianceId id, String name, String description) {
        super(id, name, description);
    }

    @Getter
    private Optional<RGBWState> colorState = Optional.empty();

    @Override
    public void acceptMessage(Message message) {
        processPayload(message.getPayload());
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload payload) {
        return Optional.of(Message.builder()
            .senderId(getId())
            .payload(processPayload(payload))
            .build());
    }

    @Override
    public Optional<RGBWState> getState() {
        return this.colorState;
    }

    private MessagePayload processPayload(MessagePayload messagePayload) {
        if (messagePayload instanceof RGBWUpdatePayload) {
            return processColor((RGBWUpdatePayload) messagePayload);
        } else {
            throw new MessageUnsupportedException("Unsupported message payload of class " + messagePayload.getClass());
        }
    }

    private MessagePayload processColor(RGBWUpdatePayload rgbw) {
        this.colorState = Optional.of(RGBWState.of(rgbw.getRed(), rgbw.getGreen(), rgbw.getBlue(), rgbw.getWhite()));
        return rgbw;
    }
}
