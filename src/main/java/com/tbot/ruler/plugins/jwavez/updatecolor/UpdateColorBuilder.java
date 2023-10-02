package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class UpdateColorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "update-color";

    private final SwitchColorReportListener listener;

    public UpdateColorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
        this.listener = new SwitchColorReportListener();
        pluginContext.getJwzSerialHandler().addCommandListener(SwitchColorCommandType.SWITCH_COLOR_REPORT, listener);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity) {
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

        listener.registerActuator(actuator);
        return actuator;
    }
}
