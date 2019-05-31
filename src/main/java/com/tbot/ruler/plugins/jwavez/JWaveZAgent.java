package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.enums.BasicCommandType;
import com.rposcro.jwavez.core.commands.enums.SceneActivationCommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.handlers.ApplicationCommandHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class JWaveZAgent {

    private SupportedCommandDispatcher commandDispatcher;
    private GeneralAsynchronousController jwzController;

    private SceneActivationHandler sceneActivationHandler;
    private BasicSetHandler basicSetHandler;

    public JWaveZAgent(String device) {
        this.commandDispatcher = commandDispatcher();
        this.jwzController = GeneralAsynchronousController.builder()
            .dongleDevice(device)
            .callbackHandler(ApplicationCommandHandler.builder()
                .supportedCommandDispatcher(commandDispatcher)
                .supportBroadcasts(false)
                .supportMulticasts(false)
                .build())
            .build();
        setupHandlers(commandDispatcher);
    }

    public void connect() {
        try {
            jwzController.connect();
            log.info("JWaveZ agent successfully connected to dongle device");
        } catch(SerialPortException e) {
            log.error("Could not connect to ZWave dongle device, JWaveZ agent not active!", e);
        }
    }

    private void setupHandlers(SupportedCommandDispatcher commandDispatcher) {
        this.sceneActivationHandler = new SceneActivationHandler();
        this.basicSetHandler = new BasicSetHandler();
        commandDispatcher.registerHandler(SceneActivationCommandType.SCENE_ACTIVATION_SET, sceneActivationHandler);
        commandDispatcher.registerHandler(BasicCommandType.BASIC_SET, basicSetHandler);
    }

    private SupportedCommandDispatcher commandDispatcher() {
        return new SupportedCommandDispatcher();
    }
}
