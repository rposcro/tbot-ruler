package com.tbot.ruler.plugins.jwavez.notification;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.supported.notification.NotificationReport;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class NotificationCommandListener extends JWaveZCommandListener<NotificationReport> {

    private List<NotificationActuator> actuators;
    private JwzSupportedCommandParser supportedCommandParser;

    public NotificationCommandListener(JwzSupportedCommandParser supportedCommandParser) {
        this.supportedCommandParser = supportedCommandParser;
        this.actuators = new LinkedList<>();
    }

    @Override
    public void handleCommand(NotificationReport notificationReport) {
        log.debug("Handling notification report command");
        byte nodeId = notificationReport.getSourceNodeId().getId();
        actuators.stream()
                .filter(emitter -> emitter.acceptsCommand(nodeId))
                .forEach(emitter -> emitter.acceptNotification(notificationReport));
    }

    @Override
    public void handleEncapsulatedCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.debug("Handling encapsulated notification report command");
        byte nodeId = commandEncapsulation.getSourceNodeId().getId();
        byte sourceEndpointId = commandEncapsulation.getSourceEndPointId();

        NotificationReport notificationReport = supportedCommandParser.parseCommand(
                ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                commandEncapsulation.getSourceNodeId());

        actuators.stream()
                .filter(emitter -> emitter.acceptsCommand(nodeId, sourceEndpointId))
                .forEach(emitter -> emitter.acceptNotification(notificationReport));
    }

    public void registerActuator(NotificationActuator actuator) {
        actuators.add(actuator);
    }
}
