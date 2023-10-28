package com.tbot.ruler.plugins.agent.trigger;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.agent.AgentActuatorBuilder;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

public class ThingTriggerActuatorBuilder extends AgentActuatorBuilder {

    private static final String REFERENCE = "trigger";

    public ThingTriggerActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext thingContext) {
        return ThingTriggerActuator.builder()
                .actuatorEntity(actuatorEntity)
                .thingContext(thingContext)
                .build();
    }
}
