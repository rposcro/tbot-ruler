package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.things.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class UpdateColorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "update-color";

    public UpdateColorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, JWaveZPluginContext pluginContext) {
        UpdateColorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(),  UpdateColorConfiguration.class);
        UpdateColorActuator actuator = UpdateColorActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .messagePublisher(pluginContext.getMessagePublisher())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();

        SwitchColorReportListener listener = new SwitchColorReportListener();
        pluginContext.getJwzSerialHandler().addCommandListener(SwitchColorCommandType.SWITCH_COLOR_REPORT, listener);
        listener.registerActuator(actuator);

        return actuator;
    }
}
