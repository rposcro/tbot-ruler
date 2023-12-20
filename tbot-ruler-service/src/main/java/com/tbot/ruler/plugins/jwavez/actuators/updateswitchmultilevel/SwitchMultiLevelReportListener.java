package com.tbot.ruler.plugins.jwavez.actuators.updateswitchmultilevel;

import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchMultiLevelReportListener extends AbstractCommandListener<SwitchMultilevelReport> {

    private final UpdateSwitchMultiLevelActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public SwitchMultiLevelReportListener(
            UpdateSwitchMultiLevelActuator actuator,
            int sourceNodeId) {
        super(SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(SwitchMultilevelReport command) {
        log.debug("Plugin Jwz: Handling switch multilevel report command");
        actuator.acceptCommand(command);
    }
}
