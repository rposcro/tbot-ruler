package com.tbot.ruler.plugins.jwavez.sceneactivation;

import com.rposcro.jwavez.core.commands.types.SceneActivationCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SceneActivationBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "scene-activation";

    private final SceneActivationListener listener;

    public SceneActivationBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
        this.listener = new SceneActivationListener();
        pluginContext.getJwzSerialHandler().addCommandListener(SceneActivationCommandType.SCENE_ACTIVATION_SET, listener);
    }

    @Override
    public SceneActivationActuator buildActuator(ActuatorEntity actuatorEntity) {
        SceneActivationConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SceneActivationConfiguration.class);
        SceneActivationActuator actuator = SceneActivationActuator.builder()
            .uuid(actuatorEntity.getActuatorUuid())
            .name(actuatorEntity.getName())
            .description(actuatorEntity.getDescription())
            .messagePublisher(pluginContext.getMessagePublisher())
            .sceneId((byte) configuration.getSceneId())
            .sourceNodeId((byte) configuration.getSourceNodeId())
            .build()
            .init();

        listener.registerActuator(actuator);
        return actuator;
    }
}
