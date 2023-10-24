package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import lombok.Getter;

@Getter
public abstract class JWaveZActuatorBuilder {

    protected String reference;
    protected JWaveZPluginContext pluginContext;

    protected JWaveZActuatorBuilder(String reference, JWaveZPluginContext pluginContext) {
        this.reference = reference;
        this.pluginContext = pluginContext;
    }

    public abstract Actuator buildActuator(ActuatorEntity actuatorEntity);
}
