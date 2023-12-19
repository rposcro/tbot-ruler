package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.serial.handlers.ApplicationCommandHandler;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.sunwatch.SunWatchActuatorBuilder;
import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;
import com.tbot.ruler.plugins.jwavez.controller.CommandRouteRegistry;
import com.tbot.ruler.plugins.jwavez.controller.CommandRouter;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import com.tbot.ruler.plugins.jwavez.controller.JWaveZSerialController;
import com.tbot.ruler.plugins.jwavez.controller.MockedSerialController;
import com.tbot.ruler.plugins.jwavez.controller.SerialController;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.instantiateActuatorsBuilders;
import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;
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
        this.jobBundles = List.of(
                JobBundle.oneTimeJobBundle(() -> jwzPluginContext.getSerialController().connect()),
                JobBundle.continuousJobBundle(jwzPluginContext.getJwzCommandSender())
        );
    }

    @Override
    public Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        return buildActuator(actuatorEntity, rulerThingContext);
    }

    @Override
    public void stopActuator(Actuator actuator, String reference) {
        JWaveZActuatorBuilder builder = actuatorsBuilders.get(reference);
        if (builder == null) {
            throw new PluginException("Unknown builder reference " + reference);
        }
        builder.destroyActuator(actuator);
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        JWaveZActuatorBuilder actuatorBuilder = actuatorsBuilders.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException(format("Wrong actuator %s definition, unknown actuator reference %s, entity skipped",
                    actuatorEntity.getActuatorUuid(), actuatorEntity.getReference()));
        }
        return actuatorBuilder.buildActuator(actuatorEntity, rulerThingContext);
    }

    private JWaveZPluginContext prepareJwzContext(RulerPluginContext rulerPluginContext) {
        CommandRouteRegistry commandRouteRegistry = new CommandRouteRegistry();
        SerialController serialController = prepareSerialController(rulerPluginContext, commandRouteRegistry);
        CommandSender commandSender = CommandSender.builder()
                .serialController(serialController)
                .build();

        return JWaveZPluginContext.builder()
                .rulerPluginContext(rulerPluginContext)
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
