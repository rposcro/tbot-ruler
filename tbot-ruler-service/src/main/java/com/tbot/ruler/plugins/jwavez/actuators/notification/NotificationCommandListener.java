package com.tbot.ruler.plugins.jwavez.actuators.notification;

import com.rposcro.jwavez.core.commands.supported.notification.NotificationReport;
import com.rposcro.jwavez.core.commands.types.NotificationCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class NotificationCommandListener extends AbstractCommandListener<NotificationReport> {

    private final NotificationActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public NotificationCommandListener(NotificationActuator actuator, int sourceNodeId) {
        super(NotificationCommandType.NOTIFICATION_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(NotificationReport notificationReport) {
        log.debug("Handling notification report command");
        actuator.acceptNotification(notificationReport);
    }
}
