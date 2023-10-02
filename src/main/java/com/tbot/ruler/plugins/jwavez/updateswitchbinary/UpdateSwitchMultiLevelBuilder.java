package com.tbot.ruler.plugins.jwavez.updateswitchbinary;

import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class UpdateSwitchMultiLevelBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "update-switch-binary";

    private final SwitchBinaryReportListener listener;

    public UpdateSwitchMultiLevelBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
        this.listener = new SwitchBinaryReportListener(pluginContext.getJwzApplicationSupport());
        pluginContext.getJwzSerialHandler().addCommandListener(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, listener);
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

        listener.registerActuator(configuration.getNodeId(), configuration.getEndPointId(), actuator);
        return actuator;
    }
}
