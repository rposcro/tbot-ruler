package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.BinarySetPayload;
import com.tbot.ruler.things.builder.dto.EmitterDTO;

public abstract class AbstractEmitterBuilder {

    private static final String VALUE_ON = "on";

    protected MessagePayload emitterPayload(EmitterDTO emitterDTO, String paramName) {
        return new BinarySetPayload(VALUE_ON.equalsIgnoreCase(emitterDTO.getConfig().getOrDefault(paramName, "off")));
    }

    protected Message emitterMessage(EmitterDTO emitterDTO, String paramName) {
        return Message.builder()
            .senderId(emitterDTO.getId())
            .payload(emitterPayload(emitterDTO, paramName))
            .build();
    }

    protected Long plusMinutes(EmitterDTO emitterDTO, String paramName) {
        return Long.parseLong(
            emitterDTO.getConfig().getOrDefault(paramName, "0"));
    }
}
