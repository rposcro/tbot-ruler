package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.signals.SignalValueType;
import com.tbot.ruler.things.EmitterMetadata;
import com.tbot.ruler.things.ThingBuilderContext;
import com.tbot.ruler.things.dto.EmitterDTO;

public class BasicSetEmitterBuilder {

    private static final String BASIC_PARAM_SOURCE_NODE = "source-node-id";
    private static final String BASIC_PARAM_VALUE_MODE = "value-mode";
    private static final String BASIC_PARAM_TURN_ON_VALUE = "turn-on-value";
    private static final String BASIC_PARAM_TURN_OFF_VALUE = "turn-off-value";

    public BasicSetEmitter buildEmitter(JWaveZAgent agent, ThingBuilderContext context, EmitterDTO emitterDTO) {
        BasicSetEmitter emitter = BasicSetEmitter.builder()
            .id(emitterDTO.getId())
            .metadata(EmitterMetadata.builder()
                .id(emitterDTO.getId())
                .name(emitterDTO.getName())
                .description(emitterDTO.getName())
                .emittedSignalType(SignalValueType.OnOff)
                .build())
            .signalConsumer(context.getSignalConsumer())
            .sourceNodeId((byte) emitterDTO.getIntParameter(BASIC_PARAM_SOURCE_NODE))
            .valueMode(valueMode(emitterDTO))
            .turnOnValue((byte) emitterDTO.getIntParameter(BASIC_PARAM_TURN_ON_VALUE, 255))
            .turnOffValue((byte) emitterDTO.getIntParameter(BASIC_PARAM_TURN_OFF_VALUE, 0))
            .build()
            .init();
        agent.getBasicSetHandler().registerEmitter(emitter);
        return emitter;
    }

    private BasicSetValueMode valueMode(EmitterDTO emitterDTO) {
        return BasicSetValueMode.of(emitterDTO.getStringParameter(BASIC_PARAM_VALUE_MODE, "toggle"));
    }
}
