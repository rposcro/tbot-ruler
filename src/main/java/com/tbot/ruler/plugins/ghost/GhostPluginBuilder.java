package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.BasicPlugin;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.exceptions.PluginException;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.findActuatorsBuilders;
import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class GhostPluginBuilder implements PluginBuilder {

    private final Map<String, GhostActuatorBuilder> buildersMap;

    public GhostPluginBuilder() {
        buildersMap = findActuatorsBuilders(GhostActuatorBuilder.class, "com.tbot.ruler.plugins.ghost").stream()
                .collect(Collectors.toMap(GhostActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Plugin buildPlugin(PluginBuilderContext builderContext) {
        return BasicPlugin.builder()
                .uuid(builderContext.getPluginEntity().getPluginUuid())
                .name(builderContext.getPluginEntity().getName())
                .things(builderContext.getPluginEntity().getThings().stream()
                        .map(entity -> buildThing(entity, builderContext))
                        .collect(Collectors.toList()))
                .build();
    }

    private Thing buildThing(ThingEntity thingEntity, PluginBuilderContext pluginBuilderContext) {
        GhostThingConfiguration configuration = parseConfiguration(thingEntity.getConfiguration(), GhostThingConfiguration.class);

        return BasicThing.builder()
                .uuid(thingEntity.getThingUuid())
                .name(thingEntity.getName())
                .description(thingEntity.getDescription())
                .actuators(thingEntity.getActuators().stream()
                        .map(entity -> buildActuator(entity, pluginBuilderContext, configuration))
                        .collect(Collectors.toList()))
                .build();
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, PluginBuilderContext builderContext, GhostThingConfiguration thingConfiguration) {
        GhostActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, builderContext.getMessagePublisher(), thingConfiguration);
    }
}
