package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SunWatchActuatorBuilder {

    protected static final String VALUE_ON = "on";

    @Getter
    protected String reference;

    protected SunWatchActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(
            ActuatorEntity actuatorEntity,
            RulerThingContext rulerThingContext,
            SunLocale eventLocale) throws PluginException;

    public void destroyActuator(Actuator actuator) {
        log.info("No custom destroy action implemented for actuator builder {}", getClass().getName());
    }

    protected Message emitterMessage(ActuatorEntity actuatorEntity, String signalValue) {
        return Message.builder()
                .senderId(actuatorEntity.getActuatorUuid())
                .payload(OnOffState.of(VALUE_ON.equalsIgnoreCase(signalValue)))
                .build();
    }
}
