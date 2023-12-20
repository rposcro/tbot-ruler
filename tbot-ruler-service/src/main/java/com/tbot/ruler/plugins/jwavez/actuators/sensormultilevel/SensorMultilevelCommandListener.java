package com.tbot.ruler.plugins.jwavez.actuators.sensormultilevel;

import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SensorMultilevelCommandListener extends AbstractCommandListener<SensorMultilevelReport> {

    private SensorMultilevelActuator actuator;
    private CommandFilter commandFilter;

    @Builder
    public SensorMultilevelCommandListener(
            SensorMultilevelActuator actuator,
            int sourceNodeId) {
        super(SensorMultilevelCommandType.SENSOR_MULTILEVEL_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(SensorMultilevelReport report) {
        log.debug("Plugin Jwz: Handling sensor multilevel report command from {}", report.getSourceNodeId().getId());
        actuator.acceptCommand(report);
    }
}
