package com.tbot.ruler.plugins.ghost.activator;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostThingConfiguration;
import com.tbot.ruler.plugins.ghost.GhostThingContext;
import com.tbot.ruler.subjects.Actuator;

public class GhostThingActivationActuatorBuilder extends GhostActuatorBuilder {

    private static final String REFERENCE = "activator";

    public GhostThingActivationActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(GhostThingContext ghostThingContext, ActuatorEntity actuatorEntity, GhostThingConfiguration thingConfiguration) {
        return GhostThingActivationActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .ghostThingAgent(ghostThingContext.getGhostThingAgent())
                .build();
    }
}
