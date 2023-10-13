package com.tbot.ruler.plugins.jwavez.actuators.notification;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.NotificationCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import com.tbot.ruler.subjects.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class NotificationActuatorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "notification";

    public NotificationActuatorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity) {
        NotificationConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), NotificationConfiguration.class);
        NotificationActuator actuator = NotificationActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .configuration(configuration)
                .messagePublisher(pluginContext.getMessagePublisher())
                .build();
        CommandListener<? extends ZWaveSupportedCommand> listener = buildCommandListener(actuator, configuration);
        pluginContext.getCommandRouteRegistry().registerListener(NotificationCommandType.NOTIFICATION_REPORT, listener);
        return actuator;
    }

    private CommandListener<? extends ZWaveSupportedCommand> buildCommandListener(NotificationActuator actuator, NotificationConfiguration configuration) {
        if (configuration.isMultiChannelOn()) {
            return NotificationEncapsulatedCommandListener.builder()
                    .actuator(actuator)
                    .supportedCommandParser(pluginContext.getJwzApplicationSupport().supportedCommandParser())
                    .sourceNodeId(configuration.getSourceNodeId())
                    .build();
        } else {
            return NotificationCommandListener.builder()
                    .actuator(actuator)
                    .sourceNodeId(configuration.getSourceNodeId())
                    .build();
        }
    }
}
