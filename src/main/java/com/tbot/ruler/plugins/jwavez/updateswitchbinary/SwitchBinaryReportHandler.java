package com.tbot.ruler.plugins.jwavez.updateswitchbinary;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SwitchBinaryReportHandler extends JWaveZCommandHandler<BinarySwitchReport> {

    private Map<String, UpdateSwitchBinaryEmitter> emitters;
    private SupportedCommandParser commandParser;

    public SwitchBinaryReportHandler() {
        this.emitters = new HashMap<>();
        this.commandParser = SupportedCommandParser.defaultParser();
    }

    @Override
    public void handleCommand(BinarySwitchReport report) {
        log.debug("Handling switch binary report command");
        byte nodeId = report.getSourceNodeId().getId();
        UpdateSwitchBinaryEmitter emitter = emitters.get(computeKey(nodeId));

        if (emitter != null) {
            emitter.acceptCommand(report);
        }
    }

    @Override
    public void handleEncapsulatedCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.info("Handling encapsulated switch binary report command");
        String emittersKey = computeKey(commandEncapsulation.getSourceNodeId().getId(), commandEncapsulation.getSourceEndpointId());
        UpdateSwitchBinaryEmitter emitter = emitters.get(emittersKey);

        if (emitter != null) {
            BinarySwitchReport report = commandParser.parseCommand(
                    ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                    commandEncapsulation.getSourceNodeId());
            emitter.acceptCommand(report);
        }
    }

    public void registerEmitter(int nodeId, int endPointId, UpdateSwitchBinaryEmitter emitter) {
        emitters.put(endPointId == 0 ? computeKey((byte) nodeId) : computeKey((byte) nodeId, (byte) endPointId), emitter);
    }

    private String computeKey(byte sourceNodeId) {
        return String.format("%02x", sourceNodeId);
    }

    private String computeKey(byte sourceNodeId, byte sourceEndpointId) {
        return String.format("%02x-%02x", sourceNodeId, sourceEndpointId);
    }
}
