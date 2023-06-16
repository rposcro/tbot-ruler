package com.tbot.ruler.plugins.jwavez.sceneactivation;

import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.SceneActivationCommandType;
import com.tbot.ruler.plugins.jwavez.EmitterBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;

public class SceneActivationEmitterBuilder implements EmitterBuilder {

    private static final String REFERENCE = "scene-activation";
    private static final String SCENE_PARAM_SOURCE_NODE = "source-node-id";
    private static final String SCENE_PARAM_SCENE_ID = "scene-id";

    private final JWaveZThingContext thingContext;
    private final SceneActivationListener sceneActivationHandler = new SceneActivationListener();

    public SceneActivationEmitterBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
    }

    @Override
    public CommandType getSupportedCommandType() {
        return SceneActivationCommandType.SCENE_ACTIVATION_SET;
    }

    @Override
    public JWaveZCommandListener<SceneActivationSet> getSupportedCommandHandler() {
        return sceneActivationHandler;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public SceneActivationEmitter buildEmitter(EmitterDTO emitterDTO) {
        SceneActivationEmitter emitter = SceneActivationEmitter.builder()
            .id(emitterDTO.getId())
            .name(emitterDTO.getName())
            .description(emitterDTO.getDescription())
            .messagePublisher(thingContext.getMessagePublisher())
            .sceneId((byte) emitterDTO.getIntParameter(SCENE_PARAM_SCENE_ID))
            .sourceNodeId((byte) emitterDTO.getIntParameter(SCENE_PARAM_SOURCE_NODE))
            .build()
            .init();
        sceneActivationHandler.registerEmitter(emitter);
        return emitter;
    }
}
