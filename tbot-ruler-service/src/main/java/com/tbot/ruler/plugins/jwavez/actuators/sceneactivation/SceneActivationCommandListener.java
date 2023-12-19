package com.tbot.ruler.plugins.jwavez.actuators.sceneactivation;

import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet;
import com.rposcro.jwavez.core.commands.types.SceneActivationCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SceneActivationCommandListener extends AbstractCommandListener<SceneActivationSet> {

    private final SceneActivationActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public SceneActivationCommandListener(SceneActivationActuator actuator, int sourceNodeId, int sceneId) {
        super(SceneActivationCommandType.SCENE_ACTIVATION_SET, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = command -> command.getSourceNodeId().getId() == (byte) sourceNodeId
                && ((SceneActivationSet) command).getSceneId() == (short) sceneId;
    }

    @Override
    public void handleCommand(SceneActivationSet command) {
        log.debug("Plugin Jwz: Handling scene activation set command");
        actuator.publishMessage();
    }
}
