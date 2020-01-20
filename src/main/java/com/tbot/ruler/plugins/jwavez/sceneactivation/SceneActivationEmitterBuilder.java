package com.tbot.ruler.plugins.jwavez.sceneactivation;

import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;

public class SceneActivationEmitterBuilder {

    private static final String SCENE_PARAM_SOURCE_NODE = "source-node-id";
    private static final String SCENE_PARAM_SCENE_ID = "scene-id";

    public SceneActivationEmitter buildEmitter(SceneActivationHandler handler, ThingBuilderContext context, EmitterDTO emitterDTO) {
        SceneActivationEmitter emitter = SceneActivationEmitter.builder()
            .id(emitterDTO.getId())
            .name(emitterDTO.getName())
            .description(emitterDTO.getDescription())
            .messagePublisher(context.getMessagePublisher())
            .sceneId((byte) emitterDTO.getIntParameter(SCENE_PARAM_SCENE_ID))
            .sourceNodeId((byte) emitterDTO.getIntParameter(SCENE_PARAM_SOURCE_NODE))
            .build()
            .init();
        handler.registerEmitter(emitter);
        return emitter;
    }
}
