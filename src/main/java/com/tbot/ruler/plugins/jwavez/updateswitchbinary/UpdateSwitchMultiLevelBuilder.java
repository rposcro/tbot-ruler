package com.tbot.ruler.plugins.jwavez.updateswitchbinary;

import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.things.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class UpdateSwitchMultiLevelBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "update-switch-binary";

    private final JWaveZPluginContext pluginContext;

    public UpdateSwitchMultiLevelBuilder(JWaveZPluginContext pluginContext) {
        super(
                REFERENCE,
                SwitchBinaryCommandType.BINARY_SWITCH_REPORT,
                new SwitchBinaryReportListener(pluginContext.getJwzApplicationSupport()));
        this.pluginContext = pluginContext;
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity) {
        UpdateSwitchBinaryConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), UpdateSwitchBinaryConfiguration.class);
        UpdateSwitchBinaryActuator emitter = UpdateSwitchBinaryActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .messagePublisher(pluginContext.getMessagePublisher())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
        ((SwitchBinaryReportListener) getSupportedCommandHandler()).registerEmitter(configuration.getNodeId(), configuration.getEndPointId(), emitter);
        return emitter;
    }
}
