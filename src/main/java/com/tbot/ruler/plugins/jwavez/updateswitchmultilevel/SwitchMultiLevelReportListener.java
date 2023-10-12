package com.tbot.ruler.plugins.jwavez.updateswitchmultilevel;

import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class SwitchMultiLevelReportListener extends JWaveZCommandListener<SwitchMultilevelReport> {

    private List<UpdateSwitchMultiLevelActuator> actuators;

    public SwitchMultiLevelReportListener() {
        this.actuators = new LinkedList<>();
    }

    @Override
    public void handleCommand(SwitchMultilevelReport command) {
        log.debug("Handling switch multilevel report command");
        byte nodeId = command.getSourceNodeId().getId();
        actuators.stream()
                .filter(emitter -> emitter.acceptsReportCommand(nodeId))
                .forEach(emitter -> emitter.acceptCommand(command));
    }

    public void registerActuator(UpdateSwitchMultiLevelActuator emitter) {
        actuators.add(emitter);
    }
}
