package com.tbot.ruler.plugins.jwavez.multichannel;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CommandEncapsulationHandler implements SupportedCommandHandler<MultiChannelCommandEncapsulation> {

    private final static Map<CommandType, EncapsulatedCommandProcessor> handlersPerCommand;

    static {
        handlersPerCommand = new HashMap<>();
        handlersPerCommand.put(MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP, new EncapsulatedBasicSetProcessor());
    }

    public void handleCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.debug("Received multi channel encapsulation command");
        CommandClass commandClass = commandEncapsulation.getEncapsulatedCommandClass();
        EncapsulatedCommandProcessor processor = handlersPerCommand.get(commandClass);
        if (processor == null) {
            log.info("Encapsulated command class " + commandClass + " is not supported");
            return;
        }
        processor.handle(commandEncapsulation);
    }
}
