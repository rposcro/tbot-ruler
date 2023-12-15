package com.tbot.ruler.plugins.jwavez.actuators.updateswitchmultilevel;

import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class UpdateSwitchMultiLevelBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "update-switch-multilevel";

    public UpdateSwitchMultiLevelBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        UpdateSwitchMultiLevelConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), UpdateSwitchMultiLevelConfiguration.class);
        UpdateSwitchMultiLevelActuator actuator = UpdateSwitchMultiLevelActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .messagePublisher(rulerThingContext.getMessagePublisher())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
        pluginContext.getCommandRouteRegistry().registerListener(
                SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT,
                SwitchMultiLevelReportListener.builder()
                        .actuator(actuator)
                        .sourceNodeId(configuration.getNodeId())
                        .build());
        return actuator;
    }
}
