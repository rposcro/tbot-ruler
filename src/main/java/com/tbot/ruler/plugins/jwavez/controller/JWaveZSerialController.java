package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.ResponseHandler;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.FramesUtil;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginConfiguration;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import static com.tbot.ruler.util.LogArgument.argument;

@Slf4j
public class JWaveZSerialController implements SerialController {

    private final String device;
    private final int reconnectAttempts;
    private final int reconnectDelay;

    private GeneralAsynchronousController jwzController;
    private CallbackHandler callbackHandler;

    @Builder
    public JWaveZSerialController(JWaveZPluginConfiguration configuration, CallbackHandler callbackHandler) {
        this.reconnectAttempts = configuration.getReconnectAttempts();
        this.reconnectDelay = configuration.getReconnectDelay();
        this.device = configuration.getModuleDevice();
        this.callbackHandler = callbackHandler;
    }

    public void connect() {
        GeneralAsynchronousController jwzController = null;
        int retries = 0;
        while (jwzController == null && retries++ < reconnectAttempts) {
            try {
                log.info("JWaveZ agent connection, device {}, attempt #{} of {}", device, retries, reconnectAttempts);
                jwzController = buildJWaveZController(device);
                jwzController.connect();
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
        this.jwzController = jwzController;
    }

    public void sendRequest(SerialRequest serialRequest) throws SerialException {
        jwzController.requestCallbackFlow(serialRequest);
    }

    public boolean isConnected() {
        return this.jwzController != null;
    }

    private GeneralAsynchronousController buildJWaveZController(String device) {
        return GeneralAsynchronousController.builder()
                .dongleDevice(device)
                .callbackHandler(callbackHandler)
                .responseHandler(buildResponseHandler())
                .build();
    }

    private ResponseHandler buildResponseHandler() {
        return (buffer) -> {
            log.debug("Response frame received: {}", argument(() -> FramesUtil.asFineString(buffer)));
        };
    }
}
