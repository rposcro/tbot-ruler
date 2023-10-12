package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.exceptions.PluginException;
import lombok.Getter;

public abstract class SunWatchActuatorBuilder {

    protected static final String VALUE_ON = "on";

    @Getter
    protected String reference;

    protected SunWatchActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(
            ActuatorEntity actuatorEntity,
            RulerPluginContext rulerPluginContext,
            SunLocale eventLocale) throws PluginException;

    protected Message emitterMessage(ActuatorEntity actuatorEntity, String signalValue) {
        return Message.builder()
                .senderId(actuatorEntity.getActuatorUuid())
                .payload(OnOffState.of(VALUE_ON.equalsIgnoreCase(signalValue)))
                .build();
    }
}
