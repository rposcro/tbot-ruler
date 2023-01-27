package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.rposcro.jwavez.core.constants.ColorComponent;
import lombok.Getter;

import java.util.stream.Stream;

import static com.rposcro.jwavez.core.constants.ColorComponent.BLUE;
import static com.rposcro.jwavez.core.constants.ColorComponent.GREEN;
import static com.rposcro.jwavez.core.constants.ColorComponent.RED;
import static com.rposcro.jwavez.core.constants.ColorComponent.WARM_WHITE;

@Getter
public enum ColorMode {

    RGBW_WARM(RED, GREEN, BLUE, WARM_WHITE);

    private ColorComponent[] components;
    private int[] componentCodes;

    ColorMode(ColorComponent... components) {
        this.components = components;
        this.componentCodes = Stream.of(components)
                .mapToInt(ColorComponent::getCode)
                .toArray();
    }
}
