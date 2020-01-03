package com.tbot.ruler.plugins.jwavez.switchcolor;

import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.plugins.jwavez.JWaveZAgent;
import com.tbot.ruler.things.builder.dto.CollectorDTO;

public class SwitchColorCollectorBuilder {

    private static final String SWITCH_PARAM_NODE_ID = "node-id";
    private static final String SWITCH_PARAM_COLOR_MODE = "color-mode";
    private static final String SWITCH_PARAM_SWITCH_DURATION = "switch-duration";

    public SwitchColorCollector buildCollector(CollectorDTO collectorDTO, JWaveZAgent agent) {
        return SwitchColorCollector.builder()
            .id(collectorDTO.getId())
            .name(collectorDTO.getName())
            .description(collectorDTO.getDescription())
            .switchDuration((byte) collectorDTO.getIntParameter(SWITCH_PARAM_SWITCH_DURATION, 0))
            .nodeId(new NodeId((byte) collectorDTO.getIntParameter(SWITCH_PARAM_NODE_ID)))
            .colorMode(colorMode(collectorDTO.getStringParameter(SWITCH_PARAM_COLOR_MODE)))
            .commandConsumer(agent.commandSender())
            .build();
    }

    private ColorMode colorMode(String colorModeParam) {
        colorModeParam = colorModeParam.trim().replace(' ', '_').toUpperCase();
        return ColorMode.valueOf(colorModeParam);
    }
}
