package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class SwitchColorReportHandler extends JWaveZCommandHandler<SwitchColorReport> {

    private List<UpdateColorEmitter> emitters;

    public SwitchColorReportHandler() {
        this.emitters = new LinkedList<>();
    }

    @Override
    public void handleCommand(SwitchColorReport command) {
        log.debug("Handling switch color report command");
        byte nodeId = command.getSourceNodeId().getId();
        emitters.stream()
                .filter(emitter -> emitter.acceptsReportCommand(nodeId))
                .forEach(emitter -> emitter.acceptCommand(command));
    }

    public void registerEmitter(UpdateColorEmitter emitter) {
        emitters.add(emitter);
    }
}
