package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.PluginsUtil;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.BasicThing;
import com.tbot.ruler.subjects.Thing;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

@Getter
public class GhostPlugin extends AbstractSubject implements Plugin {

    private final static Map<String, GhostActuatorBuilder> ACTUATORS_BUILDERS =
            PluginsUtil.instantiateActuatorsBuilders(GhostActuatorBuilder.class, "com.tbot.ruler.plugins.ghost").stream()
                .collect(Collectors.toMap(GhostActuatorBuilder::getReference, Function.identity()));

    private RulerPluginContext rulerPluginContext;

    @Builder
    public GhostPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
        this.rulerPluginContext = rulerPluginContext;
    }

    @Override
    public Thing startUpThing(ThingEntity thingEntity) {
        GhostThingConfiguration configuration = parseConfiguration(thingEntity.getConfiguration(), GhostThingConfiguration.class);

        GhostThingAgent thingAgent = new GhostThingAgent();
        GhostThingContext thingContext = GhostThingContext.builder()
                .rulerPluginContext(rulerPluginContext)
                .ghostThingAgent(thingAgent)
                .build();

        return BasicThing.builder()
                .uuid(thingEntity.getThingUuid())
                .name(thingEntity.getName())
                .description(thingEntity.getDescription())
                .actuators(thingEntity.getActuators().stream()
                        .map(entity -> buildActuator(thingContext, entity, configuration))
                        .collect(Collectors.toList()))
                .build();
    }

    private Actuator buildActuator(GhostThingContext thingContext, ActuatorEntity actuatorEntity, GhostThingConfiguration thingConfiguration) {
        GhostActuatorBuilder actuatorBuilder = ACTUATORS_BUILDERS.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(thingContext, actuatorEntity, thingConfiguration);
    }
}
