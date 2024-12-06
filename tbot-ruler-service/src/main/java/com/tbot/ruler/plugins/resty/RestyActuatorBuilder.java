package com.tbot.ruler.plugins.resty;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class RestyActuatorBuilder {

    @Getter
    protected String reference;

    protected RestyActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(ActuatorEntity actuatorEntity, RulerPluginContext rulerPluginContext);

    public void destroyActuator(Actuator actuator) {
        log.info("No custom destroy action implemented for actuator builder {}", getClass().getName());
    }
}
