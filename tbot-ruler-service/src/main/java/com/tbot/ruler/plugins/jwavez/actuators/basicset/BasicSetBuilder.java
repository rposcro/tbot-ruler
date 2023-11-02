package com.tbot.ruler.plugins.jwavez.actuators.basicset;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class BasicSetBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "basic-set";

    public BasicSetBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        BasicSetConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), BasicSetConfiguration.class);
        BasicSetActuator actuator = BasicSetActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .configuration(configuration)
                .messagePublisher(rulerThingContext.getMessagePublisher())
                .build();
        CommandListener<? extends ZWaveSupportedCommand> listener = buildCommandListener(actuator, configuration);
        pluginContext.getCommandRouteRegistry().registerListener(BasicCommandType.BASIC_SET, listener);
        return actuator;
    }

    private CommandListener<? extends ZWaveSupportedCommand> buildCommandListener(BasicSetActuator actuator, BasicSetConfiguration configuration) {
        if (configuration.isMultiChannelOn()) {
            return BasicSetEncapsulatedCommandListener.builder()
                    .supportedCommandParser(pluginContext.getJwzApplicationSupport().supportedCommandParser())
                    .actuator(actuator)
                    .sourceNodeId(configuration.getNodeId())
                    .sourceEndPointId(configuration.getNodeEndPointId())
                    .build();
        } else {
            return BasicSetCommandListener.builder()
                    .actuator(actuator)
                    .sourceNodeId(configuration.getNodeId())
                    .build();
        }
    }
}
