package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.exceptions.PluginException;
import lombok.Getter;

public abstract class AbstractActuatorBuilder {

    protected static final String VALUE_ON = "on";

    @Getter
    protected String reference;

    protected AbstractActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(
            ActuatorEntity actuatorEntity,
            PluginBuilderContext pluginBuilderContext,
            SunLocale eventLocale) throws PluginException;

    protected Message emitterMessage(ActuatorEntity actuatorEntity, String signalValue) {
        return Message.builder()
                .senderId(actuatorEntity.getActuatorUuid())
                .payload(OnOffState.of(VALUE_ON.equalsIgnoreCase(signalValue)))
                .build();
    }
}
