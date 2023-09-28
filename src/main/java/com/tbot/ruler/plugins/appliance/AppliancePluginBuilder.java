package com.tbot.ruler.plugins.appliance;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.BasicPlugin;
import com.tbot.ruler.subjects.BasicThing;
import com.tbot.ruler.subjects.Plugin;
import com.tbot.ruler.subjects.Thing;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.findActuatorsBuilders;

public class AppliancePluginBuilder implements PluginBuilder {

    private final Map<String, ApplianceActuatorBuilder> buildersMap;

    public AppliancePluginBuilder() {
        buildersMap = findActuatorsBuilders(ApplianceActuatorBuilder.class, "com.tbot.ruler.plugins.appliance").stream()
                .collect(Collectors.toMap(ApplianceActuatorBuilder::getReference, Function.identity()));
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
        return BasicThing.builder()
                .uuid(thingEntity.getThingUuid())
                .name(thingEntity.getName())
                .description(thingEntity.getDescription())
                .actuators(thingEntity.getActuators().stream()
                        .map(entity -> buildActuator(entity, pluginBuilderContext))
                        .collect(Collectors.toList()))
                .build();
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, PluginBuilderContext builderContext) {
        ApplianceActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, builderContext.getMessagePublisher());
    }
}
