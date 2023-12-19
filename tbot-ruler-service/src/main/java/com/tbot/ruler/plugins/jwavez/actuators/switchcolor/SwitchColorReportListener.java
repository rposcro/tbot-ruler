package com.tbot.ruler.plugins.jwavez.actuators.switchcolor;

import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.model.ColorComponent;
import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.plugins.jwavez.actuators.updatecolor.ColorMode;
import com.tbot.ruler.plugins.jwavez.controller.AbstractCommandListener;
import com.tbot.ruler.plugins.jwavez.controller.CommandFilter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Getter
public class SwitchColorReportListener extends AbstractCommandListener<SwitchColorReport> {

    private final SwitchColorActuator actuator;
    private final CommandFilter commandFilter;
    private final ColorMode colorMode;

    private final ColorComponentsCollector componentsCollector;

    @Builder
    public SwitchColorReportListener(SwitchColorActuator actuator, ColorMode colorMode, int sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_REPORT, actuator.getUuid());
        this.actuator = actuator;
        this.colorMode = colorMode;
        this.commandFilter = CommandFilter.sourceNodeFilter(sourceNodeId);
        this.componentsCollector = new ColorComponentsCollector();
    }

    @Override
    public void handleCommand(SwitchColorReport command) {
        log.debug("Plugin Jwz: Handling switch color report command");
        componentsCollector.components[command.getColorComponentId()] = command.getCurrentValue();
        if (componentsCollector.isComplete()) {
            actuator.setState(componentsCollector.toColor());
            componentsCollector.reset();
        }
    }

    private class ColorComponentsCollector {
        int[] components = new int[Stream.of(ColorComponent.values()).mapToInt(ColorComponent::getCode).max().getAsInt()];

        private ColorComponentsCollector() {
            reset();
        }

        private boolean isComplete() {
            for (int idx: colorMode.getComponentCodes()) {
                if (components[idx] < 0) {
                    return false;
                };
            }
            return true;
        }

        private void reset() {
            Arrays.fill(components, -1);
        }

        private RGBWColor toColor() {
            return RGBWColor.of(
                    components[ColorComponent.RED.getCode()],
                    components[ColorComponent.GREEN.getCode()],
                    components[ColorComponent.BLUE.getCode()],
                    components[ColorComponent.WARM_WHITE.getCode()]);
        }
    }
}
