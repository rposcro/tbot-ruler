package com.tbot.ruler.plugins.agent;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AgentActuatorBuilder {

    @Getter
    protected String reference;

    protected AgentActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext);

    public void destroyActuator(Actuator actuator) {
        log.info("No custom destroy action implemented for actuator builder {}", getClass().getName());
    }
}
