package com.tbot.ruler.plugins.jwavez.updateswitchmultilevel;

import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class SwitchMultiLevelReportHandler extends JWaveZCommandHandler<SwitchMultilevelReport> {

    private List<UpdateSwitchMultiLevelEmitter> emitters;

    public SwitchMultiLevelReportHandler() {
        this.emitters = new LinkedList<>();
    }

    @Override
    public void handleCommand(SwitchMultilevelReport command) {
        log.debug("Handling switch multilevel report command");
        byte nodeId = command.getSourceNodeId().getId();
        emitters.stream()
                .filter(emitter -> emitter.acceptsReportCommand(nodeId))
                .forEach(emitter -> emitter.acceptCommand(command));
    }

    public void registerEmitter(UpdateSwitchMultiLevelEmitter emitter) {
        emitters.add(emitter);
    }
}
