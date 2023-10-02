package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.PluginsUtil;
import com.tbot.ruler.subjects.BasicPlugin;
import com.tbot.ruler.subjects.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.BasicThing;
import com.tbot.ruler.subjects.Thing;
import com.tbot.ruler.exceptions.PluginException;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.instantiateActuatorsBuilders;
import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class GhostPluginBuilder implements PluginBuilder {

    private final Map<String, GhostActuatorBuilder> buildersMap;
    private final PluginBuilderContext pluginBuilderContext;

    public GhostPluginBuilder(PluginBuilderContext pluginBuilderContext) {
        this.pluginBuilderContext = pluginBuilderContext;
        this.buildersMap = PluginsUtil.instantiateActuatorsBuilders(GhostActuatorBuilder.class, "com.tbot.ruler.plugins.ghost").stream()
                .collect(Collectors.toMap(GhostActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Plugin buildPlugin(PluginEntity pluginEntity) {
        return BasicPlugin.builder()
                .uuid(pluginEntity.getPluginUuid())
                .name(pluginEntity.getName())
                .things(pluginEntity.getThings().stream()
                        .map(entity -> buildThing(entity))
                        .collect(Collectors.toList()))
                .build();
    }

    private Thing buildThing(ThingEntity thingEntity) {
        GhostThingConfiguration configuration = parseConfiguration(thingEntity.getConfiguration(), GhostThingConfiguration.class);

        return BasicThing.builder()
                .uuid(thingEntity.getThingUuid())
                .name(thingEntity.getName())
                .description(thingEntity.getDescription())
                .actuators(thingEntity.getActuators().stream()
                        .map(entity -> buildActuator(entity, configuration))
                        .collect(Collectors.toList()))
                .build();
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, GhostThingConfiguration thingConfiguration) {
        GhostActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, pluginBuilderContext.getMessagePublisher(), thingConfiguration);
    }
}
