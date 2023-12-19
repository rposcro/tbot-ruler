package com.tbot.ruler.plugins.jwavez.actuators.sensormultilevel;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SensorMultilevelEncapsulatedCommandListener extends AbstractCommandListener<MultiChannelCommandEncapsulation> {

    private SensorMultilevelActuator actuator;
    private JwzSupportedCommandParser supportedCommandParser;
    private CommandFilter commandFilter;

    @Builder
    public SensorMultilevelEncapsulatedCommandListener(
            SensorMultilevelActuator actuator,
            JwzSupportedCommandParser supportedCommandParser,
            int sourceNodeId,
            int sourceEndPointId) {
        super(SensorMultilevelCommandType.SENSOR_MULTILEVEL_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.supportedCommandParser = supportedCommandParser;
        this.commandFilter = CommandFilter.encapsulatedSourceFilter(sourceNodeId, sourceEndPointId);
    }

    @Override
    public void handleCommand(MultiChannelCommandEncapsulation commandEncapsulation) {
        log.debug("Plugin Jwz: Handling encapsulated sensor multilevel report command from {}-{}",
                commandEncapsulation.getSourceNodeId().getId(), commandEncapsulation.getSourceEndPointId());
        SensorMultilevelReport report = supportedCommandParser.parseCommand(
                ImmutableBuffer.overBuffer(commandEncapsulation.getEncapsulatedCommandPayload()),
                commandEncapsulation.getSourceNodeId());
        actuator.acceptCommand(report);
    }
}
