package com.rposcro.jwavez.serial.controllers.mock;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collections;
import java.util.Map;

@Builder
@Getter
public class JWaveZMockRules {

    @Singular
    private Map<Long, String> periodicCallbacks;
}
