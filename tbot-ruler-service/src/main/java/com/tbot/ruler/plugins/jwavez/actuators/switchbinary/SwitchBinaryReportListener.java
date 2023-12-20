package com.tbot.ruler.plugins.jwavez.actuators.switchbinary;

import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchBinaryReportListener extends AbstractCommandListener<BinarySwitchReport> {

    private final SwitchBinaryActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public SwitchBinaryReportListener(
            SwitchBinaryActuator actuator,
            int sourceNodeId) {
        super(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(BinarySwitchReport command) {
        log.debug("Plugin Jwz: Handling switch binary report command");
        OnOffState state = OnOffState.of(command.getValue() != 0);
        actuator.setState(state);
    }
}
