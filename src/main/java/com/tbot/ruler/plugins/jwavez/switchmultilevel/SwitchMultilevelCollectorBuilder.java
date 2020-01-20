package com.tbot.ruler.plugins.jwavez.switchmultilevel;

import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.plugins.jwavez.JWaveZAgent;
import com.tbot.ruler.things.builder.dto.CollectorDTO;

public class SwitchMultilevelCollectorBuilder {

    private static final String SWITCH_PARAM_NODE_ID = "node-id";
    private static final String SWITCH_PARAM_SWITCH_DURATION = "switch-duration";

    public SwitchMultilevelCollector buildCollector(CollectorDTO collectorDTO, JWaveZAgent agent) {
        return SwitchMultilevelCollector.builder()
            .id(collectorDTO.getId())
            .name(collectorDTO.getName())
            .description(collectorDTO.getDescription())
            .switchDuration((byte) collectorDTO.getIntParameter(SWITCH_PARAM_SWITCH_DURATION, 0))
            .nodeId(new NodeId((byte) collectorDTO.getIntParameter(SWITCH_PARAM_NODE_ID)))
            .commandConsumer(agent.commandSender())
            .build();
    }
}
