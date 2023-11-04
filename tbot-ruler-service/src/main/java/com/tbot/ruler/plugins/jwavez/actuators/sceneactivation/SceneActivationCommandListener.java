package com.tbot.ruler.plugins.jwavez.actuators.sceneactivation;

import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SceneActivationCommandListener implements CommandListener<SceneActivationSet> {

    private final SceneActivationActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public SceneActivationCommandListener(SceneActivationActuator actuator, int sourceNodeId, int sceneId) {
        this.actuator = actuator;
        this.commandFilter = command -> command.getSourceNodeId().getId() == (byte) sourceNodeId
                && ((SceneActivationSet) command).getSceneId() == (short) sceneId;
    }

    @Override
    public void handleCommand(SceneActivationSet command) {
        log.debug("Handling scene activation set command");
        actuator.publishMessage();
    }
}
