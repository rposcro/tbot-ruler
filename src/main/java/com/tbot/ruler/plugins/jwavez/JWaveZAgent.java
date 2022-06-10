package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.commands.types.SceneActivationCommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.plugins.jwavez.basicset.BasicSetHandler;
import com.tbot.ruler.plugins.jwavez.multichannel.CommandEncapsulationHandler;
import com.tbot.ruler.plugins.jwavez.sceneactivation.SceneActivationHandler;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
@Getter
public class JWaveZAgent {

    private static final String PARAM_DEVICE = "moduleDevice";
    private static final String PARAM_RECONNECT_ATTEMPTS = "reconnectAttempts";
    private static final String PARAM_RECONNECT_DELAY = "reconnectDelay";

    private static final int DEFAULT_SECONDS_BETWEEN_RETRIES = 5;
    private static final int DEFAULT_MAX_RETRIES = 36;

    private String device;
    private GeneralAsynchronousController jwzController;
    private SceneActivationHandler sceneActivationHandler;
    private BasicSetHandler basicSetHandler;
    private CommandEncapsulationHandler commandEncapsulationHandler;

    private final int reconnectAttempts;
    private final int reconnectDelay;

    private final AtomicInteger callbackId = new AtomicInteger(1);

    public JWaveZAgent(ThingBuilderContext builderContext) {
        this.reconnectAttempts = builderContext.getThingDTO().getIntParameter(PARAM_RECONNECT_ATTEMPTS, DEFAULT_MAX_RETRIES);
        this.reconnectDelay = builderContext.getThingDTO().getIntParameter(PARAM_RECONNECT_DELAY, DEFAULT_SECONDS_BETWEEN_RETRIES);
        this.device = builderContext.getThingDTO().getStringParameter(PARAM_DEVICE);
        this.sceneActivationHandler = new SceneActivationHandler();
        this.basicSetHandler = new BasicSetHandler();
        this.commandEncapsulationHandler = new CommandEncapsulationHandler();
    }

    public void connect() {
        int retries = 0;
        while (this.jwzController == null && retries++ < reconnectAttempts) {
            try {
                log.info("JWaveZ agent connection, attempt #{}", retries);
                GeneralAsynchronousController jwzController = jwavezController(device);
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
            if (log.isDebugEnabled()) {
                log.debug("Request frame sending: {}", bufferToString(command.getPayload()));
            }

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

    private GeneralAsynchronousController jwavezController(String device) {
        return GeneralAsynchronousController.builder()
            .dongleDevice(device)
            .callbackHandler(callbackHandler())
            .responseHandler(responseHandler())
            .build();
    }

    private Consumer<ViewBuffer> responseHandler() {
        return (buffer) -> {
            if (log.isDebugEnabled()) {
                log.debug("Response frame received: {}", BufferUtil.bufferToString(buffer));
            }
        };
    }

    private Consumer<ViewBuffer> callbackHandler() {
        return new InterceptableCallbackHandler()
            .addCallbackInterceptor(applicationCommandInterceptor())
            .addViewBufferInterceptor((buffer) -> {
                if (log.isDebugEnabled()) {
                    log.debug("Callback frame received: {}", BufferUtil.bufferToString(buffer));
                }
            });
    }

    private ApplicationCommandInterceptor applicationCommandInterceptor() {
        SupportedCommandDispatcher commandDispatcher = new SupportedCommandDispatcher()
            .registerHandler(SceneActivationCommandType.SCENE_ACTIVATION_SET, sceneActivationHandler)
            .registerHandler(BasicCommandType.BASIC_SET, basicSetHandler)
            .registerHandler(MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP, commandEncapsulationHandler);
        return ApplicationCommandInterceptor.builder()
            .supportedCommandDispatcher(commandDispatcher)
            .build();
    }

    private byte nextCallbackId() {
        return (byte) callbackId.accumulateAndGet(1, (current, add) -> (++current > 250 ? 1 : current));
    }

    private String bufferToString(ImmutableBuffer buffer) {
        StringBuffer string = new StringBuffer();
        IntStream.range(0, buffer.getLength())
            .forEach(index -> string.append(String.format("%02x ", buffer.getByte(index))));
        return string.toString();
    }

}
