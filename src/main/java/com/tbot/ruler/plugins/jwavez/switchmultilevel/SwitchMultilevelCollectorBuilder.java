package com.tbot.ruler.plugins.jwavez.switchmultilevel;

import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.plugins.jwavez.CollectorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.builder.dto.CollectorDTO;

public class SwitchMultilevelCollectorBuilder implements CollectorBuilder {

    private static final String SWITCH_PARAM_NODE_ID = "nodeId";
    private static final String SWITCH_PARAM_SWITCH_DURATION = "switchDuration";

    private final static String REFERENCE = "switch-multilevel";

    private final JWaveZThingContext thingContext;

    public SwitchMultilevelCollectorBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public SwitchMultilevelCollector buildCollector(CollectorDTO collectorDTO) {
        return SwitchMultilevelCollector.builder()
                .id(collectorDTO.getId())
                .name(collectorDTO.getName())
                .description(collectorDTO.getDescription())
                .switchDuration((byte) collectorDTO.getIntParameter(SWITCH_PARAM_SWITCH_DURATION, 0))
                .nodeId(new NodeId((byte) collectorDTO.getIntParameter(SWITCH_PARAM_NODE_ID)))
                .commandSender(thingContext.getJwzCommandSender())
                .applicationSupport(thingContext.getJwzApplicationSupport())
                .build();
    }
}
