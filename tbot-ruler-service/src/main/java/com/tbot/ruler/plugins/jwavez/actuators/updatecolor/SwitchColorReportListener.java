package com.tbot.ruler.plugins.jwavez.actuators.updatecolor;

import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchColorReportListener implements CommandListener<SwitchColorReport> {

    private final UpdateColorActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public SwitchColorReportListener(UpdateColorActuator actuator, int sourceNodeId) {
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(SwitchColorReport command) {
        log.debug("Handling switch color report command");
        actuator.acceptCommand(command);
    }
}
