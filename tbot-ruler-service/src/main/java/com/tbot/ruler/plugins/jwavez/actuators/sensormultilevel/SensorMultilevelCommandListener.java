package com.tbot.ruler.plugins.jwavez.actuators.sensormultilevel;

import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SensorMultilevelCommandListener implements CommandListener<SensorMultilevelReport> {

    private SensorMultilevelActuator actuator;
    private CommandFilter commandFilter;

    @Builder
    public SensorMultilevelCommandListener(
            SensorMultilevelActuator actuator,
            int sourceNodeId) {
        this.actuator = actuator;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
    }

    @Override
    public void handleCommand(SensorMultilevelReport report) {
        log.debug("Handling sensor multilevel report command from {}", report.getSourceNodeId().getId());
        actuator.acceptCommand(report);
    }
}
