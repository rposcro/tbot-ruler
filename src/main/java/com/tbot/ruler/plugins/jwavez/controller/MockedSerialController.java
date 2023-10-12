package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.FramesUtil;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginConfiguration;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockedSerialController implements SerialController {

    private final JWaveZPluginConfiguration configuration;

    @Builder
    public MockedSerialController(JWaveZPluginConfiguration configuration, CallbackHandler callbackHandler) {
        this.configuration = configuration;
    }

    public void connect() {
        log.debug("Mocked serial controller 'connected'");
    }

    public boolean isConnected() {
        return true;
    }

    public void sendRequest(SerialRequest serialRequest) throws SerialException {
        log.debug("Mocked sending of serial request {}", FramesUtil.asFineString(serialRequest.getFrameData()));
    }
}
