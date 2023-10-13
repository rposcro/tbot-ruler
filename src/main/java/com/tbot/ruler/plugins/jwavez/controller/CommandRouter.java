package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.listeners.SupportedCommandDispatcher;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.tbot.ruler.util.LogArgument.argument;

@Slf4j
@Builder
public class CommandRouter extends SupportedCommandDispatcher {

    private final JwzSupportedCommandParser supportedCommandParser;
    private final CommandRouteRegistry commandRouteRegistry;

    @Override
    public void dispatchCommand(ZWaveSupportedCommand command) {
        log.debug("ZWave supported command received: {}", argument(() -> command.asNiceString()));
        routeCommand(command);
//        if (command instanceof MultiChannelCommandEncapsulation) {
//            routeEncapsulatedCommand((MultiChannelCommandEncapsulation) command);
//        } else {
//            routeCommand(command);
//        }
    }

    private void routeCommand(ZWaveSupportedCommand command) {
        List<CommandListener<ZWaveSupportedCommand>> listeners = commandRouteRegistry.findListeners(command.getCommandType());
        listeners.stream()
                .filter(listener -> listener.getCommandFilter().accepts(command))
                .forEach(listener -> listener.handleCommand(command));
    }

//    private void routeEncapsulatedCommand(MultiChannelCommandEncapsulation encapsulation) {
//        try {
//            ZWaveSupportedCommand command = supportedCommandParser.parseCommand(
//                    ImmutableBuffer.overBuffer(encapsulation.getEncapsulatedCommandPayload()),
//                    encapsulation.getSourceNodeId());
//            List<CommandListener<? extends ZWaveSupportedCommand>> listeners = commandRouteRegistry.findListeners(command.getCommandType());
//            listeners.stream()
//                    .filter(listener -> listener.getCommandFilter().accepts(encapsulation))
//                    .forEach(listener -> listener.handleCommand(command));
//        } catch(Exception e) {
//            log.error(String.format("Failed to handle encapsulated command %s with command type code %s",
//                            encapsulation.getEncapsulatedCommandClass(), encapsulation.getEncapsulatedCommandCode())
//                    , e);
//        }
//    }
}
