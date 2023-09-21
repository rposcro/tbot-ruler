package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.Actuator;
import lombok.Getter;

@Getter
public abstract class JWaveZActuatorBuilder {

    private String reference;

    protected JWaveZActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(ActuatorEntity actuatorEntity, JWaveZPluginContext pluginContext);
}
