package com.tbot.ruler.plugins.jwavez.actuators.updateswitchmultilevel;

import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class UpdateSwitchMultiLevelBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "update-switch-multilevel";

    private final SwitchMultiLevelReportListener listener;

    public UpdateSwitchMultiLevelBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
        this.listener = new SwitchMultiLevelReportListener();
        pluginContext.getJwzSerialHandler().addCommandListener(SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT, listener);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity) {
        UpdateSwitchMultiLevelConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), UpdateSwitchMultiLevelConfiguration.class);
        UpdateSwitchMultiLevelActuator actuator = UpdateSwitchMultiLevelActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .messagePublisher(pluginContext.getMessagePublisher())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();

        listener.registerActuator(actuator);
        return actuator;
    }
}
