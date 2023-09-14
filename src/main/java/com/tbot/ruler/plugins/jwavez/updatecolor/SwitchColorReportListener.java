package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class SwitchColorReportListener extends JWaveZCommandListener<SwitchColorReport> {

    private List<UpdateColorActuator> emitters;

    public SwitchColorReportListener() {
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

    public void registerEmitter(UpdateColorActuator emitter) {
        emitters.add(emitter);
    }
}
