package com.tbot.ruler.plugins.jwavez.actuators.basicset;

import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.tbot.ruler.plugins.jwavez.controller.CommandFilter.sourceNodeFilter;

@Slf4j
@Getter
public class BasicSetCommandListener implements CommandListener<BasicSet> {

    private final BasicSetActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public BasicSetCommandListener(
            BasicSetActuator actuator,
            int sourceNodeId) {
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
