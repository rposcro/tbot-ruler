package com.tbot.ruler.plugins.jwavez.actuators.switchcolor;

import com.rposcro.jwavez.core.model.ColorComponent;
import lombok.Getter;

import java.util.stream.Stream;

import static com.rposcro.jwavez.core.model.ColorComponent.BLUE;
import static com.rposcro.jwavez.core.model.ColorComponent.COLD_WHITE;
import static com.rposcro.jwavez.core.model.ColorComponent.GREEN;
import static com.rposcro.jwavez.core.model.ColorComponent.RED;
import static com.rposcro.jwavez.core.model.ColorComponent.WARM_WHITE;

@Getter
public enum ColorMode {

    RGB(RED, GREEN, BLUE),
    RGBW_WARM(RED, GREEN, BLUE, WARM_WHITE),
    RGBW_COLD(RED, GREEN, BLUE, COLD_WHITE);

    private ColorComponent[] components;
    private int[] componentCodes;

    ColorMode(ColorComponent... components) {
        this.components = components;
        this.componentCodes = Stream.of(components)
            .mapToInt(ColorComponent::getCode)
            .toArray();
    }
}
