package com.tbot.ruler.plugins.jwavez.sceneactivation;

import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SceneActivationHandler extends JWaveZCommandHandler<SceneActivationSet> {

    private Map<String, List<SceneActivationEmitter>> emittersPerKey;

    public SceneActivationHandler() {
        this.emittersPerKey = new HashMap<>();
    }

    @Override
    public void handleCommand(SceneActivationSet command) {
        log.debug("Handling scene activation command");
        String emitterKey = SceneActivationEmitter.uniqueSceneKey(command.getSourceNodeId().getId(), (byte) command.getSceneId());
        Optional.ofNullable(emittersPerKey.get(emitterKey))
            .map(List::stream)
            .ifPresent(stream -> stream.forEach(SceneActivationEmitter::publishMessage));
    }

    public void registerEmitter(SceneActivationEmitter emitter) {
        emittersPerKey.computeIfAbsent(emitter.getUniqueSceneKey(), key -> new LinkedList()).add(emitter);
    }
}
