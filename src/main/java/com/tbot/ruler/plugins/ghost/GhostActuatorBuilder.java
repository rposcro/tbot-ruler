package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.subjects.Actuator;
import lombok.Getter;

public abstract class GhostActuatorBuilder {

    @Getter
    protected String reference;

    protected GhostActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(PluginBuilderContext pluginBuilderContext, ActuatorEntity actuatorEntity, GhostThingConfiguration configuration);
}
