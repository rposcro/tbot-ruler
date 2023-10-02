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

import static com.tbot.ruler.plugins.PluginsUtil.instantiateActuatorsBuilders;
import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class JWaveZPluginBuilder implements PluginBuilder {

    private final PluginBuilderContext pluginBuilderContext;

    public JWaveZPluginBuilder(PluginBuilderContext pluginBuilderContext) {
        this.pluginBuilderContext = pluginBuilderContext;
    }

    @Override
    public Plugin buildPlugin(PluginEntity pluginEntity) {
        JWaveZPluginContext pluginContext = preparePluginContext(pluginEntity);
        Map<String, JWaveZActuatorBuilder> actuatorsBuilders = instantiateActuatorsBuilders(
                JWaveZActuatorBuilder.class, "com.tbot.ruler.plugins.jwavez", pluginContext).stream()
                .collect(Collectors.toMap(JWaveZActuatorBuilder::getReference, Function.identity()));

        return BasicPlugin.builder()
                .uuid(pluginEntity.getPluginUuid())
                .name(pluginEntity.getName())
                .things(pluginEntity.getThings().stream()
                        .map(thingEntity -> buildThing(thingEntity, actuatorsBuilders))
                        .collect(Collectors.toList()))
                .asynchronousTask(Task.startUpTask(() -> pluginContext.getSerialController().connect()))
                .asynchronousTask(Task.continuousTask(pluginContext.getJwzCommandSender()))
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

    private JWaveZPluginContext preparePluginContext(PluginEntity pluginEntity) {
        JWaveZSerialHandler serialHandler = new JWaveZSerialHandler();
        SerialController serialController = prepareSerialController(pluginEntity, serialHandler);
        JWaveZCommandSender commandSender = JWaveZCommandSender.builder()
                .serialController(serialController)
                .build();

        return JWaveZPluginContext.builder()
                .pluginBuilderContext(pluginBuilderContext)
                .messagePublisher(pluginBuilderContext.getMessagePublisher())
                .serialController(serialController)
                .jwzApplicationSupport(JwzApplicationSupport.defaultSupport())
                .jwzSerialHandler(serialHandler)
                .jwzCommandSender(commandSender)
                .build();
    }

    private SerialController prepareSerialController(PluginEntity pluginEntity, JWaveZSerialHandler serialHandler) {
        JWaveZPluginConfiguration configuration = parseConfiguration(pluginEntity.getConfiguration(), JWaveZPluginConfiguration.class);

        if (configuration.isMockDevice()) {
            return MockedSerialController.builder()
                    .configuration(configuration)
                    .build();
        } else {
            return JWaveZSerialController.builder()
                    .configuration(configuration)
                    .callbackHandler(serialHandler)
                    .build();
        }
    }
}
