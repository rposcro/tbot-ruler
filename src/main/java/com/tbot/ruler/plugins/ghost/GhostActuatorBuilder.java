package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Getter;

public abstract class GhostActuatorBuilder {

    @Getter
    protected String reference;

    protected GhostActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(
            ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext, GhostPluginContext ghostPluginContext);
}
