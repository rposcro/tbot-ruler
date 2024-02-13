package com.tbot.ruler.plugins.jwavez.actuators.meter;

import com.rposcro.jwavez.core.commands.supported.meter.MeterReport;
import com.rposcro.jwavez.core.commands.types.MeterCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MeterCommandListener extends AbstractCommandListener<MeterReport> {

    private MeterActuator actuator;
    private CommandFilter commandFilter;

    @Builder
    public MeterCommandListener(
            MeterActuator actuator,
            int sourceNodeId) {
        super(MeterCommandType.METER_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(MeterReport report) {
        log.debug("Plugin Jwz: Handling meter report command from {}", report.getSourceNodeId().getId());
        actuator.acceptMeterReport(report);
    }
}
