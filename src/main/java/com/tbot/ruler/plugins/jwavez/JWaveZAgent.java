package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.CommandTypesRegistry;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
@Getter
public class JWaveZAgent {

    private String device;
    private GeneralAsynchronousController jwzController;
    private Map<CommandType, JWaveZCommandHandler<? extends ZWaveSupportedCommand>> commandHandlersMap;

    private final int reconnectAttempts;
    private final int reconnectDelay;

    private final AtomicInteger callbackId = new AtomicInteger(1);

    public JWaveZAgent(JWaveZThingConfiguration configuration, Map<CommandType, JWaveZCommandHandler<? extends ZWaveSupportedCommand>> commandHandlersMap) {
        this.commandHandlersMap = commandHandlersMap;
        this.reconnectAttempts = configuration.getReconnectAttempts();
        this.reconnectDelay = configuration.getReconnectDelay();
        this.device = configuration.getModuleDevice();
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

    public JWaveZCommandSender getCommandSender() {
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
            .callbackHandler(buildCallbackHandler())
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

    private Consumer<ViewBuffer> buildCallbackHandler() {
        return new InterceptableCallbackHandler()
            .addCallbackInterceptor(buildApplicationCommandInterceptor())
            .addViewBufferInterceptor((buffer) -> {
                if (log.isDebugEnabled()) {
                    log.debug("Callback frame received: {}", BufferUtil.bufferToString(buffer));
                }
            });
    }

    private ApplicationCommandInterceptor buildApplicationCommandInterceptor() {
        ApplicationCommandInterceptor interceptor = new ApplicationCommandInterceptor();
        commandHandlersMap.entrySet().stream().forEach(entry -> interceptor.registerCommandHandler(entry.getKey(), entry.getValue()));
        interceptor.registerCommandHandler(MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP, buildCommandEncapsulationHandler());
        return interceptor;
    }

    private SupportedCommandHandler<MultiChannelCommandEncapsulation> buildCommandEncapsulationHandler() {
        return encapsulation -> {
            try {
                CommandType commandType = CommandTypesRegistry.decodeCommandType(
                        encapsulation.getEncapsulatedCommandClass(), encapsulation.getEncapsulatedCommandCode());
                JWaveZCommandHandler commandHandler = commandHandlersMap.get(commandType);
                if (commandHandler == null) {
                    log.info("Encapsulated command type " + commandType + " is not supported");
                } else {
                    commandHandler.handleEncapsulatedCommand(encapsulation);
                }
            } catch(Exception e) {
                log.error("Failed to handle encapsulated command " + encapsulation.getEncapsulatedCommandClass()
                        + " with command type code " + encapsulation.getEncapsulatedCommandCode());
            }
        };
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
