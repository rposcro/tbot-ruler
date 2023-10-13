package com.tbot.ruler.plugins.jwavez.actuators.updateswitchbinary;

import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchBinaryReportListener implements CommandListener<BinarySwitchReport> {

    private final UpdateSwitchBinaryActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public SwitchBinaryReportListener(UpdateSwitchBinaryActuator actuator, int sourceNodeId) {
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(BinarySwitchReport report) {
        log.debug("Handling switch binary report command");
        actuator.acceptCommand(report);
    }
}
