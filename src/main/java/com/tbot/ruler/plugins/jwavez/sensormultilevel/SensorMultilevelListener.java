package com.tbot.ruler.plugins.jwavez.sensormultilevel;

import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SensorMultilevelListener extends JWaveZCommandListener<SensorMultilevelReport> {

    private Map<String, List<SensorMultilevelActuator>> actuatorsPerKey;
    private JwzSupportedCommandParser supportedCommandParser;

    public SensorMultilevelListener(JwzSupportedCommandParser supportedCommandParser) {
        this.supportedCommandParser = supportedCommandParser;
        this.actuatorsPerKey = new HashMap<>();
    }

    public void registerActuator(byte sourceNodeId, SensorMultilevelActuator actuator) {
        actuatorsPerKey.computeIfAbsent(computeKey(sourceNodeId), key -> new LinkedList()).add(actuator);
    }

    public void registerActuator(byte sourceNodeId, byte sourceEndPointId, SensorMultilevelActuator actuator) {
        actuatorsPerKey.computeIfAbsent(computeKey(sourceNodeId, sourceEndPointId), key -> new LinkedList()).add(actuator);
    }

    @Override
    public void handleCommand(SensorMultilevelReport report) {
        log.debug("Handling sensor multilevel report command from {}", report.getSourceNodeId().getId());
        String actuatorKey = computeKey(report.getSourceNodeId().getId());
        Optional.ofNullable(actuatorsPerKey.get(actuatorKey))
            .map(List::stream)
            .ifPresent(stream -> stream.forEach(
                    actuator -> actuator.acceptCommand(report)));
    }

    @Override
    public void handleEncapsulatedCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.debug("Handling encapsulated sensor multilevel report command from {}-{}",
                commandEncapsulation.getSourceNodeId().getId(), commandEncapsulation.getSourceEndPointId());
        String actuatorKey = computeKey(commandEncapsulation.getSourceNodeId().getId(), commandEncapsulation.getSourceEndPointId());
        List<SensorMultilevelActuator> actuators = actuatorsPerKey.get(actuatorKey);

        if (actuators != null) {
            SensorMultilevelReport report = supportedCommandParser.parseCommand(
                    ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                    commandEncapsulation.getSourceNodeId());
            actuators.stream().forEach(actuator -> actuator.acceptCommand(report));
        }
    }

    private String computeKey(byte sourceNodeId) {
        return String.format("%02x", sourceNodeId);
    }

    private String computeKey(byte sourceNodeId, byte sourceEndpointId) {
        return String.format("%02x-%02x", sourceNodeId, sourceEndpointId);
    }
}
