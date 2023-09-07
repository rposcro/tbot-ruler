package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.rposcro.jwavez.core.model.ColorComponent;
import lombok.Getter;

import java.util.stream.Stream;

import static com.rposcro.jwavez.core.model.ColorComponent.BLUE;
import static com.rposcro.jwavez.core.model.ColorComponent.GREEN;
import static com.rposcro.jwavez.core.model.ColorComponent.RED;
import static com.rposcro.jwavez.core.model.ColorComponent.WARM_WHITE;

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
