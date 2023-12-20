package com.tbot.ruler.plugins.jwavez.actuators.updateswitchbinary;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchBinaryReportEncapsulatedListener extends AbstractCommandListener<MultiChannelCommandEncapsulation> {

    private final UpdateSwitchBinaryActuator actuator;
    private final JwzSupportedCommandParser commandParser;
    private final CommandFilter commandFilter;

    @Builder
    public SwitchBinaryReportEncapsulatedListener(
            UpdateSwitchBinaryActuator actuator,
            JwzSupportedCommandParser commandParser,
            int sourceNodeId,
            int sourceEndPointId) {
        super(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.commandParser = commandParser;
        this.commandFilter = CommandFilter.encapsulatedSourceFilter(sourceNodeId, sourceEndPointId);
    }

    @Override
    public void handleCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.info("Plugin Jwz: Handling encapsulated switch binary report command");
        BinarySwitchReport report = commandParser.parseCommand(
                ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                commandEncapsulation.getSourceNodeId());
        actuator.acceptCommand(report);
    }
}
