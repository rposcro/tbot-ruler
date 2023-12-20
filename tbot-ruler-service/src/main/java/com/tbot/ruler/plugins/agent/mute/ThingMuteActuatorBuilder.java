package com.tbot.ruler.plugins.agent.mute;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.agent.AgentActuatorBuilder;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class ThingMuteActuatorBuilder extends AgentActuatorBuilder {

    private static final String REFERENCE = "mute";

    public ThingMuteActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        ThingMuteActuatorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), ThingMuteActuatorConfiguration.class);
        return ThingMuteActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .configuration(configuration)
                .rulerThingContext(rulerThingContext)
                .build();
    }
}
