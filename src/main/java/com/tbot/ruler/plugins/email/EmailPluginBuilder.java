package com.tbot.ruler.plugins.email;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.plugins.PluginsUtil;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.BasicPlugin;
import com.tbot.ruler.subjects.BasicThing;
import com.tbot.ruler.subjects.Plugin;
import com.tbot.ruler.subjects.Thing;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.instantiateActuatorsBuilders;
import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class EmailPluginBuilder implements PluginBuilder {

    private final Map<String, EmailActuatorBuilder> buildersMap;
    private final PluginBuilderContext pluginBuilderContext;

    public EmailPluginBuilder(PluginBuilderContext pluginBuilderContext) {
        this.pluginBuilderContext = pluginBuilderContext;
        this.buildersMap = PluginsUtil.instantiateActuatorsBuilders(EmailActuatorBuilder.class, "com.tbot.ruler.plugins.email").stream()
                .collect(Collectors.toMap(EmailActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Plugin buildPlugin(PluginEntity pluginEntity) {
        return BasicPlugin.builder()
                .uuid(pluginEntity.getPluginUuid())
                .name(pluginEntity.getName())
                .things(pluginEntity.getThings().stream()
                        .map(thingEntity -> buildThing(thingEntity, pluginEntity))
                        .collect(Collectors.toList()))
                .build();
    }

    private Thing buildThing(ThingEntity thingEntity, PluginEntity pluginEntity) {
        return BasicThing.builder()
                .uuid(thingEntity.getThingUuid())
                .name(thingEntity.getName())
                .description(thingEntity.getDescription())
                .actuators(thingEntity.getActuators().stream()
                        .map(entity -> buildActuator(entity, pluginEntity))
                        .collect(Collectors.toList()))
                .build();
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, PluginEntity pluginEntity) {
        EmailActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        EmailSenderConfiguration senderConfiguration = parseConfiguration(
                pluginEntity.getConfiguration(), EmailSenderConfiguration.class);

        return actuatorBuilder.buildActuator(
                actuatorEntity,
                pluginBuilderContext,
                senderConfiguration);
    }
}
