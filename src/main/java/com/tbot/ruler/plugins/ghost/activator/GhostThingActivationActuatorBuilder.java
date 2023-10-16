package com.tbot.ruler.plugins.ghost.activator;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

public class GhostThingActivationActuatorBuilder extends GhostActuatorBuilder {

    private static final String REFERENCE = "activator";

    public GhostThingActivationActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(
            ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext, GhostPluginContext ghostPluginContext) {
        return GhostThingActivationActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .rulerThingAgent(rulerThingContext.getRulerThingAgent())
                .build();
    }
}
