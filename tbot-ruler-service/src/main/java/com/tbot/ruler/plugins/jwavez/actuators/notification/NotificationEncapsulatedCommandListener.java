package com.tbot.ruler.plugins.jwavez.actuators.notification;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.supported.notification.NotificationReport;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class NotificationEncapsulatedCommandListener implements CommandListener<MultiChannelCommandEncapsulation> {

    private NotificationActuator actuator;
    private JwzSupportedCommandParser supportedCommandParser;
    private CommandFilter commandFilter;

    @Builder
    public NotificationEncapsulatedCommandListener(
            NotificationActuator actuator,
            JwzSupportedCommandParser supportedCommandParser,
            int sourceNodeId,
            int sourceEndPointId) {
        this.actuator = actuator;
        this.supportedCommandParser = supportedCommandParser;
        this.commandFilter = CommandFilter.encapsulatedSourceFilter(sourceNodeId, sourceEndPointId);
    }

    @Override
    public void handleCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.debug("Handling encapsulated notification report command");
        NotificationReport notificationReport = supportedCommandParser.parseCommand(
                ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                commandEncapsulation.getSourceNodeId());
        actuator.acceptNotification(notificationReport);
    }
}
