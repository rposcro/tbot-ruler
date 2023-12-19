package com.tbot.ruler.plugins.jwavez.actuators.updatecolor;

import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchColorReportListener extends AbstractCommandListener<SwitchColorReport> {

    private final UpdateColorActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public SwitchColorReportListener(UpdateColorActuator actuator, int sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(SwitchColorReport command) {
        log.debug("Plugin Jwz: Handling switch color report command");
        actuator.acceptCommand(command);
    }
}
