package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.rposcro.jwavez.core.commands.supported.centralscene.CentralSceneNotification;
import com.rposcro.jwavez.core.commands.types.CentralSceneCommandType;
import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.rposcro.jwavez.core.model.CentralSceneKeyAttribute.KEY_HELD_DOWN;
import static com.rposcro.jwavez.core.model.CentralSceneKeyAttribute.KEY_RELEASED;

@Slf4j
@Getter
public class CentralSceneHoldCommandListener extends AbstractCommandListener<CentralSceneNotification> {

    private final CentralSceneHoldActuator actuator;
    private final CommandFilter commandFilter;

    @Builder
    public CentralSceneHoldCommandListener(CentralSceneHoldActuator actuator, int sourceNodeId, int sceneId) {
        super(CentralSceneCommandType.CENTRAL_SCENE_NOTIFICATION, actuator.getUuid());
        this.actuator = actuator;
        this.commandFilter = command -> {
            CentralSceneNotification notification = (CentralSceneNotification) command;
            short keyAttributes = notification.getKeyAttributes();
            return notification.getSourceNodeId().getId() == (byte) sourceNodeId
                && notification.getSceneNumber() == (short) sceneId
                && (keyAttributes == KEY_HELD_DOWN.getCode() || keyAttributes == KEY_RELEASED.getCode());
        };
    }

    @Override
    public void handleCommand(CentralSceneNotification command) {
        log.debug("Plugin Jwz: Handling central scene hold notification command");
        CentralSceneKeyAttribute keyAttribute = CentralSceneKeyAttribute.ofCode(
            (byte) command.getKeyAttributes());
        actuator.handleCommandKey(keyAttribute);
    }
}
