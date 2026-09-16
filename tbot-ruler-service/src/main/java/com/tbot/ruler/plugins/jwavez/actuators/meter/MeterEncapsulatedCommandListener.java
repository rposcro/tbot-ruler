package com.tbot.ruler.plugins.jwavez.actuators.meter;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.meter.MeterReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.MeterCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MeterEncapsulatedCommandListener extends AbstractCommandListener<MultiChannelCommandEncapsulation> {

    private MeterActuator actuator;
    private JwzSupportedCommandParser supportedCommandParser;
    private CommandFilter commandFilter;

    @Builder
    public MeterEncapsulatedCommandListener(
            MeterActuator actuator,
            JwzSupportedCommandParser supportedCommandParser,
            int sourceNodeId,
            int sourceEndPointId) {
        super(MeterCommandType.METER_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.supportedCommandParser = supportedCommandParser;
        this.commandFilter = CommandFilter.encapsulatedSourceFilter(sourceNodeId, sourceEndPointId);
    }

    @Override
    public void handleCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.debug("Plugin Jwz: Handling encapsulated meter report command from {}-{}",
                commandEncapsulation.getSourceNodeId().getId(), commandEncapsulation.getSourceEndPointId());
        MeterReport report = supportedCommandParser.parseCommand(
                ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                commandEncapsulation.getSourceNodeId());
        actuator.acceptMeterReport(report);
    }
}
