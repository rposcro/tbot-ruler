package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.serial.controllers.JWaveZMockController;
import com.rposcro.jwavez.serial.controllers.mock.JWaveZMockRules;
import com.rposcro.jwavez.serial.rxtx.CallbackHandler;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.FramesUtil;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginConfiguration;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockedSerialController implements SerialController {

    private final JWaveZPluginConfiguration configuration;
    private final JWaveZMockController controller;

    @Builder
    public MockedSerialController(JWaveZPluginConfiguration configuration, CallbackHandler callbackHandler) {
        this.configuration = configuration;
        this.controller = buildController(callbackHandler);
    }

    public void connect() {
        controller.connect();
        log.debug("Mocked serial controller 'connected'");
    }

    public boolean isConnected() {
        return true;
    }

    public void sendRequest(SerialRequest serialRequest) {
        log.debug("Mocked sending of serial request {}", FramesUtil.asFineString(serialRequest.getFrameData()));
    }

    private JWaveZMockController buildController(CallbackHandler callbackHandler) {
        JWaveZMockRules rules = JWaveZMockRules.builder()
                .periodicCallback(2000l, "01 10 00 04 00 05 0a 32 02 21 32 00 00 00 00 00 00 c7")
                .periodicCallback(3300l, "01 14 00 04 00 05 0e 32 02 21 44 00 00 01 fd 00 00 00 00 00 00 4d")
                .build();
        return JWaveZMockController.builder()
                .responseHandler(frame -> {})
                .callbackHandler(callbackHandler)
                .mockRules(rules)
                .build();
    }
}
