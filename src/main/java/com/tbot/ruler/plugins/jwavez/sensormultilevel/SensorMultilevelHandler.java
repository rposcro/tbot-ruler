package com.tbot.ruler.plugins.jwavez.sensormultilevel;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SensorMultilevelHandler extends JWaveZCommandHandler<SensorMultilevelReport> {

    private Map<String, List<SensorMultilevelEmitter>> emittersPerKey;
    private SupportedCommandParser supportedCommandParser;

    public SensorMultilevelHandler() {
        this.emittersPerKey = new HashMap<>();
        this.supportedCommandParser = SupportedCommandParser.defaultParser();
    }

    public void registerEmitter(byte sourceNodeId, SensorMultilevelEmitter emitter) {
        emittersPerKey.computeIfAbsent(computeKey(sourceNodeId), key -> new LinkedList()).add(emitter);
    }

    public void registerEmitter(byte sourceNodeId, byte sourceEndPointId, SensorMultilevelEmitter emitter) {
        emittersPerKey.computeIfAbsent(computeKey(sourceNodeId, sourceEndPointId), key -> new LinkedList()).add(emitter);
    }

    @Override
    public void handleCommand(SensorMultilevelReport report) {
        log.debug("Handling scene activation command");
        String emittersKey = computeKey(report.getSourceNodeId().getId());
        Optional.ofNullable(emittersPerKey.get(emittersKey))
            .map(List::stream)
            .ifPresent(stream -> stream.forEach(
                    emitter -> emitter.publishMessage(report)));
    }

    @Override
    public void handleEncapsulatedCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.debug("Handling encapsulated scene activation command");
        String emittersKey = computeKey(commandEncapsulation.getSourceNodeId().getId(), commandEncapsulation.getSourceEndpointId());
        List<SensorMultilevelEmitter> emitters = emittersPerKey.get(emittersKey);

        if (emitters != null) {
            SensorMultilevelReport report = supportedCommandParser.parseCommand(
                    ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                    commandEncapsulation.getSourceNodeId());
            emitters.stream().forEach(emitter -> emitter.publishMessage(report));
        }
    }

    private String computeKey(byte sourceNodeId) {
        return String.format("%02x", sourceNodeId);
    }

    private String computeKey(byte sourceNodeId, byte sourceEndpointId) {
        return String.format("%02x-%02x", sourceNodeId, sourceEndpointId);
    }
}
