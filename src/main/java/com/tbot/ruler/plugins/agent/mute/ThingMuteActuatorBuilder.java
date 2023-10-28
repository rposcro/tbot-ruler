package com.tbot.ruler.plugins.agent.mute;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.agent.ThingAgentActuatorBuilder;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class ThingMuteActuatorBuilder extends ThingAgentActuatorBuilder {

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
