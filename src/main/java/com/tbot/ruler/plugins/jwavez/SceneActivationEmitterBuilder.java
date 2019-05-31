package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.signals.SignalValueType;
import com.tbot.ruler.things.EmitterMetadata;
import com.tbot.ruler.things.ThingBuilderContext;
import com.tbot.ruler.things.dto.EmitterDTO;

public class SceneActivationEmitterBuilder {

    private static final String SCENE_PARAM_SOURCE_NODE = "source-node-id";
    private static final String SCENE_PARAM_SCENE_ID = "scene-id";

    public SceneActivationEmitter buildEmitter(JWaveZAgent agent, ThingBuilderContext context, EmitterDTO emitterDTO) {
        SceneActivationEmitter emitter = SceneActivationEmitter.builder()
            .id(emitterDTO.getId())
            .metadata(EmitterMetadata.builder()
                .id(emitterDTO.getId())
                .name(emitterDTO.getName())
                .description(emitterDTO.getName())
                .emittedSignalType(SignalValueType.Toggle)
                .build())
            .sceneId((byte) emitterDTO.getIntParameter(SCENE_PARAM_SCENE_ID))
            .sourceNodeId((byte) emitterDTO.getIntParameter(SCENE_PARAM_SOURCE_NODE))
            .signalConsumer(context.getSignalConsumer())
            .build()
            .init();
        agent.getSceneActivationHandler().registerEmitter(emitter);
        return emitter;
    }
}
