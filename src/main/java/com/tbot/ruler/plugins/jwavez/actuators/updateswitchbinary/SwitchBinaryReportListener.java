package com.tbot.ruler.plugins.jwavez.actuators.updateswitchbinary;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.tbot.ruler.plugins.jwavez.controller.JWaveZCommandListener;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SwitchBinaryReportListener extends JWaveZCommandListener<BinarySwitchReport> {

    private final Map<String, UpdateSwitchBinaryActuator> emitters;
    private final JwzSupportedCommandParser commandParser;

    public SwitchBinaryReportListener(JwzApplicationSupport applicationSupport) {
        this.emitters = new HashMap<>();
        this.commandParser = applicationSupport.supportedCommandParser();
    }

    @Override
    public void handleCommand(BinarySwitchReport report) {
        log.debug("Handling switch binary report command");
        byte nodeId = report.getSourceNodeId().getId();
        UpdateSwitchBinaryActuator emitter = emitters.get(computeKey(nodeId));

        if (emitter != null) {
            emitter.acceptCommand(report);
        }
    }

    @Override
    public void handleEncapsulatedCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.info("Handling encapsulated switch binary report command");
        String emittersKey = computeKey(commandEncapsulation.getSourceNodeId().getId(), commandEncapsulation.getSourceEndPointId());
        UpdateSwitchBinaryActuator emitter = emitters.get(emittersKey);

        if (emitter != null) {
            BinarySwitchReport report = commandParser.parseCommand(
                    ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                    commandEncapsulation.getSourceNodeId());
            emitter.acceptCommand(report);
        }
    }

    public void registerActuator(int nodeId, int endPointId, UpdateSwitchBinaryActuator emitter) {
        emitters.put(endPointId == 0 ? computeKey((byte) nodeId) : computeKey((byte) nodeId, (byte) endPointId), emitter);
    }

    private String computeKey(byte sourceNodeId) {
        return String.format("%02x", sourceNodeId);
    }

    private String computeKey(byte sourceNodeId, byte sourceEndpointId) {
        return String.format("%02x-%02x", sourceNodeId, sourceEndpointId);
    }
}
