package com.tbot.ruler.plugins.jwavez.actuators.switchmultilevel;

import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchMultiLevelReportListener implements CommandListener<SwitchMultilevelReport> {

    private final SwitchMultilevelActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public SwitchMultiLevelReportListener(
            SwitchMultilevelActuator actuator,
            int sourceNodeId) {
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(SwitchMultilevelReport command) {
        log.debug("Handling switch multilevel report command");
        actuator.acceptCommand(command);
    }
}
