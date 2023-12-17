package com.tbot.ruler.plugins.jwavez.actuators.switchbinary;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class SwitchBinaryBuilder extends JWaveZActuatorBuilder {

    private final static String REFERENCE = "switch-binary";

    public SwitchBinaryBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public SwitchBinaryActuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        SwitchBinaryConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SwitchBinaryConfiguration.class);
        SwitchBinaryActuator actuator = SwitchBinaryActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
        CommandListener<? extends ZWaveSupportedCommand> listener = buildCommandListener(actuator, configuration);
        pluginContext.getCommandRouteRegistry().registerListener(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, listener);
        return actuator;
    }

    private CommandListener<? extends ZWaveSupportedCommand> buildCommandListener(SwitchBinaryActuator actuator, SwitchBinaryConfiguration configuration) {
        if (configuration.isMultiChannelOn()) {
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
}
