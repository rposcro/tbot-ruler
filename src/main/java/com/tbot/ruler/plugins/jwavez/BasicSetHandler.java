package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class BasicSetHandler implements SupportedCommandHandler<BasicSet> {

    private Map<String, List<BasicSetEmitter>> emittersPerKey;

    public BasicSetHandler() {
        this.emittersPerKey = new HashMap<>();
    }

    @Override
    public void handleCommand(BasicSet command) {
        log.debug("Received scene activation command");
        byte commandValue = (byte) command.getValue();
        String emitterKey = emitterKey(command.getSourceNodeId().getId(), commandValue);
        Optional.ofNullable(emittersPerKey.get(emitterKey))
            .map(List::stream)
            .ifPresent(stream -> stream.forEach(emitter -> emitter.activateSignal(commandValue)));
    }

    public void registerEmitter(BasicSetEmitter emitter) {
        emittersPerKey.computeIfAbsent(emitterKey(emitter.getSourceNodeId(), emitter.getTurnOffValue()), key -> new LinkedList()).add(emitter);
        emittersPerKey.computeIfAbsent(emitterKey(emitter.getSourceNodeId(), emitter.getTurnOnValue()), key -> new LinkedList()).add(emitter);
    }

    public static String emitterKey(byte nodeId, byte commandValue) {
        return String.format("%02x-%02x", nodeId, commandValue);
    }
}
