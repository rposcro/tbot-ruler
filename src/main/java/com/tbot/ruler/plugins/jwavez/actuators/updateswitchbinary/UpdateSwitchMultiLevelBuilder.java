package com.tbot.ruler.plugins.jwavez.actuators.updateswitchbinary;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import com.tbot.ruler.subjects.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class UpdateSwitchMultiLevelBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "update-switch-binary";

    public UpdateSwitchMultiLevelBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity) {
        UpdateSwitchBinaryConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), UpdateSwitchBinaryConfiguration.class);
        UpdateSwitchBinaryActuator actuator = UpdateSwitchBinaryActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .messagePublisher(pluginContext.getMessagePublisher())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
        CommandListener<? extends ZWaveSupportedCommand> listener = buildCommandListener(actuator, configuration);
        pluginContext.getCommandRouteRegistry().registerListener(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, listener);
        return actuator;
    }

    private CommandListener<? extends ZWaveSupportedCommand> buildCommandListener(UpdateSwitchBinaryActuator actuator, UpdateSwitchBinaryConfiguration configuration) {
        if (isMultiChannelOn(configuration)) {
            return SwitchBinaryReportEncapsulatedListener.builder()
                    .actuator(actuator)
                    .commandParser(pluginContext.getJwzApplicationSupport().supportedCommandParser())
                    .sourceNodeId(configuration.getNodeId())
                    .sourceEndPointId(configuration.getNodeEndPointId())
                    .build();
        } else {
            return SwitchBinaryReportListener.builder()
                    .actuator(actuator)
                    .sourceNodeId(configuration.getNodeId())
                    .build();
        }
    }

    private boolean isMultiChannelOn(UpdateSwitchBinaryConfiguration configuration) {
        return configuration.getNodeEndPointId() != 0;
    }
}
