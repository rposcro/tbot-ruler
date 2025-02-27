package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.rposcro.jwavez.core.commands.supported.centralscene.CentralSceneNotification;
import com.rposcro.jwavez.core.commands.types.CentralSceneCommandType;
import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@Getter
public class CentralSceneHoldCommandListener extends AbstractCommandListener<CentralSceneNotification> {

    private final CentralSceneHoldActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public CentralSceneHoldCommandListener(CentralSceneHoldActuator actuator, int sourceNodeId, int sceneId) {
        super(CentralSceneCommandType.CENTRAL_SCENE_NOTIFICATION, actuator.getUuid());
        Set<Byte> keyAttributes = Set.of(
            CentralSceneKeyAttribute.KEY_HELD_DOWN.getCode(), CentralSceneKeyAttribute.KEY_RELEASED.getCode());
        this.actuator = actuator;
        this.commandFilter = command ->
                command.getSourceNodeId().getId() == (byte) sourceNodeId
                && ((CentralSceneNotification) command).getSceneNumber() == (short) sceneId
                && keyAttributes.contains(((CentralSceneNotification) command).getKeyAttributes());
    }

    @Override
    public void handleCommand(CentralSceneNotification command) {
        log.debug("Plugin Jwz: Handling central scene press notification command");
        CentralSceneKeyAttribute keyAttribute = CentralSceneKeyAttribute.ofCode(
            (byte) command.getKeyAttributes());
        actuator.handleCommandKey(keyAttribute);
    }
}
