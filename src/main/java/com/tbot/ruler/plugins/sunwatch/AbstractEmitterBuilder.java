package com.tbot.ruler.plugins.sunwatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public abstract class AbstractEmitterBuilder {

    protected static final String VALUE_ON = "on";

    public abstract String getReference();
    public abstract Emitter buildEmitter(ThingBuilderContext builderContext, SunEventLocale eventLocale) throws PluginException;

    protected EmitterDTO findEmitterDTO(String emitterReference, ThingBuilderContext builderContext) throws PluginException {
        return builderContext.getThingDTO().getEmitters().stream()
                .filter(emitterDTO -> emitterDTO.getRef().equals(emitterReference))
                .findFirst()
                .orElseThrow(() -> new PluginException("Cannot find DTO for emitter reference " + emitterReference));
    }

    protected Message emitterMessage(EmitterDTO emitterDTO, String signalValue) {
        return Message.builder()
            .senderId(emitterDTO.getId())
            .payload(BooleanUpdatePayload.of(VALUE_ON.equalsIgnoreCase(signalValue)))
            .build();
    }

    protected <T> T parseEmitterConfiguration(EmitterDTO emitterDTO, Class<T> clazz) throws PluginException {
        try {
            return new ObjectMapper().readerFor(clazz).readValue(emitterDTO.getConfigurationNode());
        } catch(IOException e) {
            throw new PluginException("Could not parse SunWatch emitter's configuration!", e);
        }
    }
}
