package com.tbot.ruler.plugins.sunwatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public abstract class AbstractActuatorBuilder {

    protected static final String VALUE_ON = "on";

    public abstract String getReference();
    public abstract Actuator buildActuator(ThingBuilderContext builderContext, SunLocale eventLocale) throws PluginException;

    protected EmitterDTO findEmitterDTO(String emitterReference, ThingBuilderContext builderContext) throws PluginException {
        return builderContext.getThingDTO().getEmitters().stream()
                .filter(emitterDTO -> emitterDTO.getRef().equals(emitterReference))
                .findFirst()
                .orElseThrow(() -> new PluginException("Cannot find DTO for emitter reference " + emitterReference));
    }

    protected Message emitterMessage(EmitterDTO emitterDTO, String signalValue) {
        return Message.builder()
            .senderId(emitterDTO.getId())
            .payload(OnOffState.of(VALUE_ON.equalsIgnoreCase(signalValue)))
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
