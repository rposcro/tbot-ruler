package com.tbot.ruler.plugins.jwavez.actuators.sceneactivation;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class SceneActivationBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "scene-activation";

    public SceneActivationBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public SceneActivationActuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        SceneActivationConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SceneActivationConfiguration.class);
        SceneActivationActuator actuator = SceneActivationActuator.builder()
            .uuid(actuatorEntity.getActuatorUuid())
            .name(actuatorEntity.getName())
            .description(actuatorEntity.getDescription())
            .messagePublisher(rulerThingContext.getMessagePublisher())
            .sceneId((byte) configuration.getSceneId())
            .sourceNodeId((byte) configuration.getNodeId())
            .build()
            .init();
        pluginContext.getCommandRouteRegistry().registerListener(
                SceneActivationCommandListener.builder()
                        .actuator(actuator)
                        .sourceNodeId(configuration.getNodeId())
                        .sceneId(configuration.getSceneId())
                        .build());
        return actuator;
    }

    @Override
    public void destroyActuator(Actuator actuator) {
        pluginContext.getCommandRouteRegistry().unregisterListener(actuator.getUuid());
    }
}
