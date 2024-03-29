package com.tbot.ruler.plugins.jwavez.actuators.switchbinary;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchBinaryReportEncapsulatedListener extends AbstractCommandListener<MultiChannelCommandEncapsulation> {

    private final SwitchBinaryActuator actuator;
    private final JwzSupportedCommandParser commandParser;
    private final CommandFilter commandFilter;

    @Builder
    public SwitchBinaryReportEncapsulatedListener(
            SwitchBinaryActuator actuator,
            JwzSupportedCommandParser commandParser,
            int sourceNodeId,
            int sourceEndPointId) {
        super(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.commandParser = commandParser;
        this.commandFilter = CommandFilter.encapsulatedSourceFilter(sourceNodeId, sourceEndPointId);
    }

    @Override
    public void handleCommand(MultiChannelCommandEncapsulation encapsulation) {
        log.debug("Plugin Jwz: Handling switch binary report command");
        BinarySwitchReport report = commandParser.parseCommand(
                ImmutableBuffer.overBuffer(encapsulation.getEncapsulatedCommandPayload()),
                encapsulation.getSourceNodeId());
        OnOffState state = OnOffState.of(report.getValue() != 0);
        actuator.setState(state);
    }
}
