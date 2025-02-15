package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.rposcro.jwavez.core.commands.supported.centralscene.CentralSceneNotification;
import com.rposcro.jwavez.core.commands.types.CentralSceneCommandType;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import com.tbot.ruler.subjects.actuator.BasicSenderActuator;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CentralScenePressCommandListener extends AbstractCommandListener<CentralSceneNotification> {

    private final BasicSenderActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public CentralScenePressCommandListener(BasicSenderActuator actuator, int sourceNodeId, int sceneId) {
        super(CentralSceneCommandType.CENTRAL_SCENE_NOTIFICATION, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = command -> command.getSourceNodeId().getId() == (byte) sourceNodeId
                && ((CentralSceneNotification) command).getSceneNumber() == (short) sceneId;
    }

    @Override
    public void handleCommand(CentralSceneNotification command) {
        log.debug("Plugin Jwz: Handling central scene press notification command");
        actuator.sendMessage();
    }
}
