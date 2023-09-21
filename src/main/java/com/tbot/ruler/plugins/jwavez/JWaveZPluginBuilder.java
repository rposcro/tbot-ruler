package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.subjects.BasicPlugin;
import com.tbot.ruler.subjects.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.BasicThing;
import com.tbot.ruler.subjects.Thing;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.task.Task;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.findActuatorsBuilders;
import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class JWaveZPluginBuilder implements PluginBuilder {

    private final Map<String, JWaveZActuatorBuilder> actuatorsBuilders;

    public JWaveZPluginBuilder() {
        this.actuatorsBuilders = findActuatorsBuilders(
                JWaveZActuatorBuilder.class, "com.tbot.ruler.plugins.jwavez").stream()
                .collect(Collectors.toMap(JWaveZActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Plugin buildPlugin(PluginBuilderContext builderContext) {
        JWaveZPluginContext pluginContext = preparePluginContext(builderContext);

        return BasicPlugin.builder()
                .uuid(builderContext.getPluginEntity().getPluginUuid())
                .name(builderContext.getPluginEntity().getName())
                .things(builderContext.getPluginEntity().getThings().stream()
                        .map(thingEntity -> buildThing(thingEntity, pluginContext))
                        .collect(Collectors.toList()))
                .asynchronousTask(Task.startUpTask(() -> pluginContext.getJwzSerialController().connect()))
                .asynchronousTask(Task.continuousTask(pluginContext.getJwzCommandSender()))
                .build();
    }

    private Thing buildThing(ThingEntity thingEntity, JWaveZPluginContext pluginContext) {
        return BasicThing.builder()
                .uuid(thingEntity.getThingUuid())
                .name(thingEntity.getName())
                .description(thingEntity.getDescription())
                .actuators(thingEntity.getActuators().stream()
                        .map(actuatorEntity -> buildActuator(actuatorEntity, pluginContext))
                        .collect(Collectors.toList()))
                .build();
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, JWaveZPluginContext pluginContext) {
        JWaveZActuatorBuilder actuatorBuilder = actuatorsBuilders.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, pluginContext);
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
                .jwzSerialController(serialController)
                .jwzSerialHandler(serialHandler)
                .jwzCommandSender(commandSender)
                .build();
    }
}
