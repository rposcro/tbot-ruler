package com.tbot.ruler.plugins.jwavez.basicset;

import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class BasicSetCommandHandler extends JWaveZCommandHandler<BasicSet> {

    private List<BasicSetEmitter> emitters;

    public BasicSetCommandHandler() {
        this.emitters = new LinkedList<>();
    }

    @Override
    public void handleCommand(BasicSet command) {
        log.debug("Handling basic set command");
        byte nodeId = command.getSourceNodeId().getId();
        byte commandValue = (byte) command.getValue();
        emitters.stream()
                .filter(emitter -> emitter.acceptsCommand(nodeId))
                .forEach(emitter -> emitter.acceptCommandValue(commandValue));
    }

    @Override
    public void handleEncapsulatedCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.debug("Handling encapsulated basic set command");
        byte nodeId = commandEncapsulation.getSourceNodeId().getId();
        byte sourceEndpointId = commandEncapsulation.getSourceEndpointId();
        byte commandValue = commandEncapsulation.getEncapsulatedCommandPayload()[0];

        emitters.stream()
                .filter(emitter -> emitter.acceptsCommand(nodeId, sourceEndpointId))
                .forEach(emitter -> emitter.acceptCommandValue(commandValue));
    }

    public void registerEmitter(BasicSetEmitter emitter) {
        emitters.add(emitter);
    }
}
