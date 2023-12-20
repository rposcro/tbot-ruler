package com.tbot.ruler.plugins.jwavez.actuators.basicset;

import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.tbot.ruler.plugins.jwavez.controller.CommandFilter.sourceNodeFilter;

@Slf4j
@Getter
public class BasicSetCommandListener extends AbstractCommandListener<BasicSet> {

    private final BasicSetActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public BasicSetCommandListener(
            BasicSetActuator actuator,
            int sourceNodeId) {
        super(BasicCommandType.BASIC_SET, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(BasicSet command) {
        log.debug("Handling basic set command");
        byte commandValue = (byte) command.getValue();
        actuator.acceptCommandValue(commandValue);
    }
}
