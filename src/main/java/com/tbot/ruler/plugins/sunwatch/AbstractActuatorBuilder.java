package com.tbot.ruler.plugins.sunwatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public abstract class AbstractActuatorBuilder {

    protected static final String VALUE_ON = "on";

    public abstract String getReference();
    public abstract Actuator buildActuator(ThingBuilderContext builderContext, SunLocale eventLocale) throws PluginException;

    protected ActuatorDTO findActuatorDTO(String actuatorReference, ThingBuilderContext builderContext) throws PluginException {
        return builderContext.getThingDTO().getActuators().stream()
                .filter(dto -> dto.getRef().equals(actuatorReference))
                .findFirst()
                .orElseThrow(() -> new PluginException("Cannot find DTO for actuator reference " + actuatorReference));
    }

    protected Message emitterMessage(ActuatorDTO actuatorDTO, String signalValue) {
        return Message.builder()
            .senderId(actuatorDTO.getUuid())
            .payload(OnOffState.of(VALUE_ON.equalsIgnoreCase(signalValue)))
            .build();
    }

    protected <T> T parseActuatorConfiguration(ActuatorDTO actuatorDTO, Class<T> clazz) throws PluginException {
        try {
            return new ObjectMapper().readerFor(clazz).readValue(actuatorDTO.getConfigurationNode());
        } catch(IOException e) {
            throw new PluginException("Could not parse SunWatch actuator's configuration!", e);
        }
    }
}
