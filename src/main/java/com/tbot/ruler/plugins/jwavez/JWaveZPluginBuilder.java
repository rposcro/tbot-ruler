package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.BasicPlugin;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.exceptions.PluginException;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.findActuatorsBuilders;
import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class JWaveZPluginBuilder implements PluginBuilder {

    @Override
    public Plugin buildPlugin(PluginBuilderContext builderContext) {
        JWaveZPluginContext pluginContext = preparePluginContext(builderContext);
        Map<String, JWaveZActuatorBuilder> buildersMap = findActuatorsBuilders(
                JWaveZActuatorBuilder.class, "com.tbot.ruler.plugins.jwavez", pluginContext)
                .stream()
                .collect(Collectors.toMap(JWaveZActuatorBuilder::getReference, Function.identity()));

        return BasicPlugin.builder()
                .uuid(builderContext.getPluginEntity().getPluginUuid())
                .name(builderContext.getPluginEntity().getName())
                .things(builderContext.getPluginEntity().getThings().stream()
                        .map(thingEntity -> buildThing(thingEntity, buildersMap))
                        .collect(Collectors.toList()))
                .build();
    }

    private Thing buildThing(ThingEntity thingEntity, Map<String, JWaveZActuatorBuilder> actuatorsBuilders) {
        return BasicThing.builder()
                .uuid(thingEntity.getThingUuid())
                .name(thingEntity.getName())
                .description(thingEntity.getDescription())
                .actuators(thingEntity.getActuators().stream()
                        .map(actuatorEntity -> buildActuator(actuatorEntity, actuatorsBuilders))
                        .collect(Collectors.toList()))
                .build();
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, Map<String, JWaveZActuatorBuilder> actuatorsBuilders) {
        JWaveZActuatorBuilder actuatorBuilder = actuatorsBuilders.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity);
    }

    private JWaveZPluginContext preparePluginContext(PluginBuilderContext builderContext) {
        PluginEntity pluginEntity = builderContext.getPluginEntity();
        JWaveZSerialHandler serialHandler = new JWaveZSerialHandler();
        JWaveZSerialController serialController = JWaveZSerialController.builder()
                .configuration(parseConfiguration(pluginEntity.getConfiguration(), JWaveZPluginConfiguration.class))
                .callbackHandler(serialHandler)
                .build();
        JWaveZCommandSender commandSender = JWaveZCommandSender.builder()
                .jwzController(serialController)
                .build();

        return JWaveZPluginContext.builder()
                .pluginBuilderContext(builderContext)
                .messagePublisher(builderContext.getMessagePublisher())
                .jwzApplicationSupport(JwzApplicationSupport.defaultSupport())
                .jwzCommandSender(commandSender)
                .build();
    }
}
