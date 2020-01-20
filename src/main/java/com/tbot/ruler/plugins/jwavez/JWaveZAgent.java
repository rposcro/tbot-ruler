package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.enums.BasicCommandType;
import com.rposcro.jwavez.core.commands.enums.SceneActivationCommandType;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.handlers.ApplicationCommandHandler;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.plugins.jwavez.basicset.BasicSetHandler;
import com.tbot.ruler.plugins.jwavez.sceneactivation.SceneActivationHandler;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

@Slf4j
@Getter
public class JWaveZAgent {

    private static final String PARAM_DEVICE = "module-device";
    private static final String PARAM_RECONNECT_ATTEMPTS = "reconnect-attempts";
    private static final String PARAM_RECONNECT_DELAY = "reconnect-delay";

    private static final int DEFAULT_SECONDS_BETWEEN_RETRIES = 5;
    private static final int DEFAULT_MAX_RETRIES = 36;

    private SupportedCommandDispatcher commandDispatcher;
    private GeneralAsynchronousController jwzController;
    private String device;

    private SceneActivationHandler sceneActivationHandler;
    private BasicSetHandler basicSetHandler;

    private final int reconnectAttempts;
    private final int reconnectDelay;

    private final AtomicInteger callbackId = new AtomicInteger(1);

    public JWaveZAgent(ThingBuilderContext builderContext) {
        this.reconnectAttempts = builderContext.getThingDTO().getIntParameter(PARAM_RECONNECT_ATTEMPTS, DEFAULT_MAX_RETRIES);
        this.reconnectDelay = builderContext.getThingDTO().getIntParameter(PARAM_RECONNECT_DELAY, DEFAULT_SECONDS_BETWEEN_RETRIES);
        this.device = builderContext.getThingDTO().getStringParameter(PARAM_DEVICE);
        this.commandDispatcher = commandDispatcher();
        setupHandlers(commandDispatcher);
    }

    public void connect() {
        int retries = 0;
        while (this.jwzController == null && retries++ < reconnectAttempts) {
            try {
                log.info("JWaveZ agent connection, attempt #{}", retries);
                GeneralAsynchronousController jwzController = jwavezController(device, commandDispatcher);
                jwzController.connect();
                this.jwzController = jwzController;
                log.info("JWaveZ agent successfully connected to dongle device");
            } catch(SerialPortException e) {
                log.error("Could not connect to ZWave dongle device, JWaveZ agent not active!", e);
                try {
                    Thread.sleep(reconnectDelay * 1000);
                } catch(InterruptedException itre) {
                    log.warn("Agent's retry delay sleep interrupted!", itre);
                }
            }
        }
    }

    public BiConsumer<NodeId, ZWaveControlledCommand> commandSender() {
        return (nodeId, command) -> {
            if (jwzController == null) {
                throw new MessageProcessingException("JWaveZ controller is not active!");
            }
            try {
                SerialRequest request = SendDataRequest.createSendDataRequest(nodeId, command, nextCallbackId());
                jwzController.requestCallbackFlow(request);
            } catch(SerialException e) {
                throw new MessageProcessingException("Exception when sending command!", e);
            }
        };
    }

    private GeneralAsynchronousController jwavezController(String device, SupportedCommandDispatcher commandDispatcher) {
        return GeneralAsynchronousController.builder()
            .dongleDevice(device)
            .callbackHandler(ApplicationCommandHandler.builder()
                .supportedCommandDispatcher(commandDispatcher)
                .supportBroadcasts(false)
                .supportMulticasts(false)
                .build())
            .build();
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

    private byte nextCallbackId() {
        return (byte) callbackId.accumulateAndGet(1, (current, add) -> (++current > 250 ? 1 : current));
    }
}
