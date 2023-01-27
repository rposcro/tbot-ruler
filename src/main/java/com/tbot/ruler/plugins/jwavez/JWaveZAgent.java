package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.CommandTypesRegistry;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.ResponseHandler;
import com.rposcro.jwavez.serial.utils.FrameUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.tbot.ruler.util.LogArgument.argument;

@Slf4j
@Getter
public class JWaveZAgent {

    private String device;
    private JWaveZCommandSender commandSender;
    private Map<CommandType, JWaveZCommandHandler<? extends ZWaveSupportedCommand>> commandHandlersMap;

    private final int reconnectAttempts;
    private final int reconnectDelay;

    private final AtomicInteger callbackId = new AtomicInteger(1);

    public JWaveZAgent(JWaveZThingConfiguration configuration, Map<CommandType, JWaveZCommandHandler<? extends ZWaveSupportedCommand>> commandHandlersMap) {
        this.commandHandlersMap = commandHandlersMap;
        this.reconnectAttempts = configuration.getReconnectAttempts();
        this.reconnectDelay = configuration.getReconnectDelay();
        this.device = configuration.getModuleDevice();
        this.commandSender = new JWaveZCommandSender();
    }

    public void connect() {
        GeneralAsynchronousController jwzController = null;
        int retries = 0;
        while (jwzController == null && retries++ < reconnectAttempts) {
            try {
                log.info("JWaveZ agent connection, device {}, attempt #{} of {}", device, retries, reconnectAttempts);
                jwzController = buildJwavezController(device);
                jwzController.connect();
                commandSender.setJwzController(jwzController);
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

    private GeneralAsynchronousController buildJwavezController(String device) {
        return GeneralAsynchronousController.builder()
            .dongleDevice(device)
            .callbackHandler(buildCallbackHandler())
            .responseHandler(responseHandler())
            .build();
    }

    private ResponseHandler responseHandler() {
        return (buffer) -> {
            log.debug("Response frame received: {}", argument(() -> FrameUtil.asFineString(buffer)));
        };
    }

    private CallbackHandler buildCallbackHandler() {
        return new InterceptableCallbackHandler()
            .addViewBufferInterceptor((buffer) -> log.debug("Callback frame received: {}", argument(() -> FrameUtil.asFineString(buffer))))
            .addCallbackInterceptor(buildApplicationCommandInterceptor());
    }

    private ApplicationCommandInterceptor buildApplicationCommandInterceptor() {
        ApplicationCommandInterceptor interceptor = new LoggingApplicationCommandCallbackInterceptor();
        commandHandlersMap.entrySet().stream().forEach(entry -> interceptor.registerCommandHandler(
                entry.getKey(), new LoggingCommandHandler(entry.getValue())));
        interceptor.registerCommandHandler(
                MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP, new LoggingCommandHandler(buildCommandEncapsulationHandler()));
        return interceptor;
    }

    private SupportedCommandHandler<MultiChannelCommandEncapsulation> buildCommandEncapsulationHandler() {
        return encapsulation -> {
            try {
                CommandType commandType = CommandTypesRegistry.decodeCommandType(
                        encapsulation.getEncapsulatedCommandClass(), encapsulation.getEncapsulatedCommandCode());
                JWaveZCommandHandler commandHandler = commandHandlersMap.get(commandType);
                if (commandHandler == null) {
                    log.info("Command encapsulation not handled ");
                } else {
                    log.debug("Handler found for command encapsulation: {}", commandHandler.getClass());
                    commandHandler.handleEncapsulatedCommand(encapsulation);
                }
            } catch(Exception e) {
                log.error(String.format("Failed to handle encapsulated command %s with command type code %s",
                        encapsulation.getEncapsulatedCommandClass(), encapsulation.getEncapsulatedCommandCode())
                        , e);
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

    @AllArgsConstructor
    private class LoggingCommandHandler<T extends ZWaveSupportedCommand> implements SupportedCommandHandler<T> {

        private SupportedCommandHandler<T> delegate;

        @Override
        public void handleCommand(T command) {
            log.debug("ZWave supported command received: {}", argument(() -> command.asNiceString()));
            delegate.handleCommand(command);
        }
    }

    private class LoggingApplicationCommandCallbackInterceptor extends ApplicationCommandInterceptor {
        @Override
        public void intercept(ZWaveCallback callback) {
            log.debug("Application command callback received: {}", argument(() ->  callback.asFineString()));
            super.intercept(callback);
        }
    }
}
