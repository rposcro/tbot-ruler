package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.CommandTypesRegistry;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.listeners.SupportedCommandListener;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.interceptors.FrameBufferInterceptor;
import com.rposcro.jwavez.serial.utils.FramesUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tbot.ruler.util.LogArgument.argument;

@Slf4j
@Getter
public class JWaveZSerialHandler extends InterceptableCallbackHandler {

    private final Map<CommandType, LoggingCommandListener<? extends ZWaveSupportedCommand>> commandListenersMap;
    private final ApplicationCommandInterceptor applicationCommandInterceptor;

    private final AtomicInteger callbackId = new AtomicInteger(1);

    public JWaveZSerialHandler() {
        this.commandListenersMap = new HashMap<>();
        this.applicationCommandInterceptor = new LoggingApplicationCommandCallbackInterceptor();

        applicationCommandInterceptor.registerCommandHandler(
                MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP,
                new LoggingCommandListener(buildCommandEncapsulationListener()));

        addCallbackInterceptor(applicationCommandInterceptor);
        addFrameBufferInterceptor(frameBufferInterceptor());
    }

    public void addCommandListener(CommandType commandType, JWaveZCommandListener<? extends ZWaveSupportedCommand> listener) {
        LoggingCommandListener<? extends ZWaveSupportedCommand> loggingListener = new LoggingCommandListener<>(listener);
        this.commandListenersMap.put(commandType, loggingListener);
        this.applicationCommandInterceptor.registerCommandHandler(commandType, loggingListener);
    }

    private FrameBufferInterceptor frameBufferInterceptor() {
        return buffer -> log.debug("Callback frame received: {}", argument(() -> FramesUtil.asFineString(buffer)));
    }

    private SupportedCommandListener<MultiChannelCommandEncapsulation> buildCommandEncapsulationListener() {
        return encapsulation -> {
            try {
                CommandType commandType = CommandTypesRegistry.decodeCommandType(
                        CommandClass.ofCode(encapsulation.getEncapsulatedCommandClass()), encapsulation.getEncapsulatedCommandCode());
                LoggingCommandListener commandListener = commandListenersMap.get(commandType);
                if (commandListener == null) {
                    log.info("Command encapsulation not handled ");
                } else {
                    log.debug("Listener found for command encapsulation: {}", commandListener.delegate.getClass());
                    ((JWaveZCommandListener) commandListener.delegate).handleEncapsulatedCommand(encapsulation);
                }
            } catch(Exception e) {
                log.error(String.format("Failed to handle encapsulated command %s with command type code %s",
                        encapsulation.getEncapsulatedCommandClass(), encapsulation.getEncapsulatedCommandCode())
                        , e);
            }
        };
    }

    @AllArgsConstructor
    private class LoggingCommandListener<T extends ZWaveSupportedCommand> implements SupportedCommandListener<T> {

        private SupportedCommandListener<T> delegate;

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
