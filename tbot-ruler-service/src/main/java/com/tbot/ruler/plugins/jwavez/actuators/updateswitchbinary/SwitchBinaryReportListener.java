package com.tbot.ruler.plugins.jwavez.actuators.updateswitchbinary;

import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchBinaryReportListener extends AbstractCommandListener<BinarySwitchReport> {

    private final UpdateSwitchBinaryActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public SwitchBinaryReportListener(UpdateSwitchBinaryActuator actuator, int sourceNodeId) {
        super(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(BinarySwitchReport report) {
        log.debug("Plugin Jwz: Handling switch binary report command");
        actuator.acceptCommand(report);
    }
}
