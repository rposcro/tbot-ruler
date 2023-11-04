package com.tbot.ruler.plugins.jwavez.actuators.notification;

import com.rposcro.jwavez.core.commands.supported.notification.NotificationReport;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class NotificationCommandListener implements CommandListener<NotificationReport> {

    private final NotificationActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public NotificationCommandListener(NotificationActuator actuator, int sourceNodeId) {
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(NotificationReport notificationReport) {
        log.debug("Handling notification report command");
        actuator.acceptNotification(notificationReport);
    }
}
