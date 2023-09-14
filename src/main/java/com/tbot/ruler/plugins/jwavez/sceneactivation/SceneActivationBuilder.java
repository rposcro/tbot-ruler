package com.tbot.ruler.plugins.jwavez.sceneactivation;

import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.SceneActivationCommandType;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;

public class SceneActivationBuilder implements ActuatorBuilder {

    private static final String REFERENCE = "scene-activation";
    private static final String SCENE_PARAM_SOURCE_NODE = "source-node-id";
    private static final String SCENE_PARAM_SCENE_ID = "scene-id";

    private final JWaveZThingContext thingContext;
    private final SceneActivationListener sceneActivationHandler = new SceneActivationListener();

    public SceneActivationBuilder(JWaveZThingContext thingContext) {
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
    public SceneActivationActuator buildActuator(ActuatorDTO actuatorDTO) {
        SceneActivationActuator actuator = SceneActivationActuator.builder()
            .id(actuatorDTO.getId())
            .name(actuatorDTO.getName())
            .description(actuatorDTO.getDescription())
            .messagePublisher(thingContext.getMessagePublisher())
            .sceneId((byte) actuatorDTO.getIntParameter(SCENE_PARAM_SCENE_ID))
            .sourceNodeId((byte) actuatorDTO.getIntParameter(SCENE_PARAM_SOURCE_NODE))
            .build()
            .init();
        sceneActivationHandler.registerActuator(actuator);
        return actuator;
    }
}
