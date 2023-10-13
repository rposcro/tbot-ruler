package com.tbot.ruler.plugins.jwavez.actuators.basicset;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.tbot.ruler.plugins.jwavez.controller.CommandFilter.encapsulatedSourceFilter;

@Slf4j
@Getter
public class BasicSetEncapsulatedCommandListener implements CommandListener<MultiChannelCommandEncapsulation> {

    private final BasicSetActuator actuator;
    private final JwzSupportedCommandParser supportedCommandParser;
    private final CommandFilter commandFilter;

    @Builder
    public BasicSetEncapsulatedCommandListener(
            BasicSetActuator actuator,
            JwzSupportedCommandParser supportedCommandParser,
            int sourceNodeId,
            int sourceEndPointId) {
        this.actuator = actuator;
        this.supportedCommandParser = supportedCommandParser;
        this.commandFilter = encapsulatedSourceFilter(sourceNodeId, sourceEndPointId);
    }

    @Override
    public void handleCommand(MultiChannelCommandEncapsulation encapsulation) {
        log.debug("Handling encapsulated basic set command");
        BasicSet command = supportedCommandParser.parseCommand(
                ImmutableBuffer.overBuffer(encapsulation.getEncapsulatedCommandPayload()),
                encapsulation.getSourceNodeId());
        byte commandValue = (byte) command.getValue();
        actuator.acceptCommandValue(commandValue);
    }
}
