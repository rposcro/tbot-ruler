package com.tbot.ruler.plugins.agent.activator;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.agent.AgentActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

public class ThingActivationActuatorBuilder extends AgentActuatorBuilder {

    private static final String REFERENCE = "activator";

    public ThingActivationActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(
            ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        return ThingActivationActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .rulerThingAgent(rulerThingContext.getRulerThingAgent())
                .build();
    }
}
