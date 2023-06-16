package com.tbot.ruler.plugins.jwavez.basicset;

import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class BasicSetCommandListener extends JWaveZCommandListener<BasicSet> {

    private List<BasicSetEmitter> emitters;
    private JwzSupportedCommandParser supportedCommandParser;

    public BasicSetCommandListener(JwzSupportedCommandParser supportedCommandParser) {
        this.supportedCommandParser = supportedCommandParser;
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
        byte sourceEndpointId = commandEncapsulation.getSourceEndPointId();

        BasicSet basicSet = supportedCommandParser.parseCommand(
                ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                commandEncapsulation.getSourceNodeId());

        emitters.stream()
                .filter(emitter -> emitter.acceptsCommand(nodeId, sourceEndpointId))
                .forEach(emitter -> emitter.acceptCommandValue((byte) basicSet.getValue()));
    }

    public void registerEmitter(BasicSetEmitter emitter) {
        emitters.add(emitter);
    }
}
