package com.tbot.ruler.plugins.jwavez.actuators.updatecolor;

import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class UpdateColorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "update-color";

    public UpdateColorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        UpdateColorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(),  UpdateColorConfiguration.class);
        UpdateColorActuator actuator = UpdateColorActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .messagePublisher(rulerThingContext.getMessagePublisher())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
        pluginContext.getCommandRouteRegistry().registerListener(
                SwitchColorCommandType.SWITCH_COLOR_REPORT,
                SwitchColorReportListener.builder()
                        .actuator(actuator)
                        .sourceNodeId(configuration.getNodeId())
                        .build()
        );
        return actuator;
    }
}
