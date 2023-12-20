package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import lombok.Getter;

public abstract class DeputyActuatorBuilder {

    @Getter
    protected String reference;

    protected DeputyActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(
            ActuatorEntity actuatorEntity,
            DeputyPluginContext deputyPluginContext);
}
