package com.tbot.ruler.plugins.agent;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Getter;

public abstract class ThingAgentActuatorBuilder {

    @Getter
    protected String reference;

    protected ThingAgentActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(
            ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext);
}
