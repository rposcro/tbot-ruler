package com.tbot.ruler.plugins.jwavez.notification;

import com.rposcro.jwavez.core.commands.types.NotificationCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class NotificationActuatorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "notification";

    private final NotificationCommandListener commandListener;

    public NotificationActuatorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
        this.commandListener = new NotificationCommandListener(pluginContext.getJwzApplicationSupport().supportedCommandParser());
        pluginContext.getJwzSerialHandler().addCommandListener(NotificationCommandType.NOTIFICATION_REPORT, commandListener);
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

        commandListener.registerActuator(actuator);
        return actuator;
    }
}
