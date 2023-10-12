package com.tbot.ruler.plugins.jwavez.actuators.sceneactivation;

import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet;
import com.tbot.ruler.plugins.jwavez.controller.JWaveZCommandListener;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SceneActivationListener extends JWaveZCommandListener<SceneActivationSet> {

    private Map<String, List<SceneActivationActuator>> actuatorsPerKey;

    public SceneActivationListener() {
        this.actuatorsPerKey = new HashMap<>();
    }

    @Override
    public void handleCommand(SceneActivationSet command) {
        log.debug("Handling scene activation set command");
        String actuatorKey = SceneActivationActuator.uniqueSceneKey(command.getSourceNodeId().getId(), (byte) command.getSceneId());
        Optional.ofNullable(actuatorsPerKey.get(actuatorKey))
            .map(List::stream)
            .ifPresent(stream -> stream.forEach(SceneActivationActuator::publishMessage));
    }

    public void registerActuator(SceneActivationActuator actuator) {
        actuatorsPerKey.computeIfAbsent(actuator.getUniqueSceneKey(), key -> new LinkedList()).add(actuator);
    }
}
