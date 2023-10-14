package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.serial.handlers.ApplicationCommandHandler;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.jwavez.controller.CommandRouteRegistry;
import com.tbot.ruler.plugins.jwavez.controller.CommandRouter;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import com.tbot.ruler.plugins.jwavez.controller.JWaveZSerialController;
import com.tbot.ruler.plugins.jwavez.controller.MockedSerialController;
import com.tbot.ruler.plugins.jwavez.controller.SerialController;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.BasicThing;
import com.tbot.ruler.subjects.Thing;
import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.instantiateActuatorsBuilders;
import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;
import static java.lang.String.format;

@Getter
public class JWaveZPlugin extends AbstractSubject implements Plugin {

    private final RulerPluginContext rulerPluginContext;
    private final JWaveZPluginContext jwzPluginContext;
    private final Map<String, JWaveZActuatorBuilder> actuatorsBuilders;

    @Builder
    public JWaveZPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
        this.rulerPluginContext = rulerPluginContext;
        this.jwzPluginContext = prepareJwzContext(rulerPluginContext);
        this.actuatorsBuilders = instantiateActuatorsBuilders(
                JWaveZActuatorBuilder.class, "com.tbot.ruler.plugins.jwavez", jwzPluginContext).stream()
                .collect(Collectors.toMap(JWaveZActuatorBuilder::getReference, Function.identity()));
        this.asynchronousTasks = List.of(
                Task.startUpTask(() -> jwzPluginContext.getSerialController().connect()),
                Task.continuousTask(jwzPluginContext.getJwzCommandSender())
        );
    }

    @Override
    public Thing startUpThing(ThingEntity thingEntity) {
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
            throw new PluginException(format("Wrong actuator %s definition, unknown actuator reference %s, entity skipped",
                    actuatorEntity.getActuatorUuid(), actuatorEntity.getReference()));
        }
        return actuatorBuilder.buildActuator(actuatorEntity);
    }

    private JWaveZPluginContext prepareJwzContext(RulerPluginContext rulerPluginContext) {
        CommandRouteRegistry commandRouteRegistry = new CommandRouteRegistry();
        SerialController serialController = prepareSerialController(rulerPluginContext, commandRouteRegistry);
        CommandSender commandSender = CommandSender.builder()
                .serialController(serialController)
                .build();

        return JWaveZPluginContext.builder()
                .rulerPluginContext(rulerPluginContext)
                .messagePublisher(rulerPluginContext.getMessagePublisher())
                .serialController(serialController)
                .jwzApplicationSupport(JwzApplicationSupport.defaultSupport())
                .jwzCommandSender(commandSender)
                .commandRouteRegistry(commandRouteRegistry)
                .build();
    }

    private SerialController prepareSerialController(
            RulerPluginContext rulerPluginContext,
            CommandRouteRegistry commandRouteRegistry) {
        JWaveZPluginConfiguration configuration = parseConfiguration(rulerPluginContext.getPluginConfiguration(), JWaveZPluginConfiguration.class);

        if (configuration.isMockDevice()) {
            return MockedSerialController.builder()
                    .configuration(configuration)
                    .build();
        } else {
            CommandRouter commandRouter = CommandRouter.builder()
                    .commandRouteRegistry(commandRouteRegistry)
                    .supportedCommandParser(JwzApplicationSupport.defaultSupport().supportedCommandParser())
                    .build();
            ApplicationCommandHandler serialHandler = ApplicationCommandHandler.builder()
                    .supportBroadcasts(false)
                    .supportMulticasts(false)
                    .supportedCommandDispatcher(commandRouter)
                    .build();
            return JWaveZSerialController.builder()
                    .configuration(configuration)
                    .callbackHandler(serialHandler)
                    .build();
        }
    }
}
