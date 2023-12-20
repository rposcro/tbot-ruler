package com.tbot.ruler.plugins.email;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EmailActuatorBuilder {

    @Getter
    protected String reference;

    protected EmailActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(
            ActuatorEntity actuatorEntity,
            RulerPluginContext rulerPluginContext,
            EmailSenderConfiguration emailSenderConfiguration) throws PluginException;

    public void destroyActuator(Actuator actuator) {
        log.info("No custom destroy action implemented for actuator builder {}", getClass().getName());
    }
}
