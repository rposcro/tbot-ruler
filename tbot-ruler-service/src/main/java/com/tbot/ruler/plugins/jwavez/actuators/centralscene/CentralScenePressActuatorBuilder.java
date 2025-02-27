package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.BasicSenderActuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class CentralScenePressActuatorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "central-scene-press";

    public CentralScenePressActuatorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public BasicSenderActuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        CentralScenePressConfiguration configuration =
            parseConfiguration(actuatorEntity.getConfiguration(), CentralScenePressConfiguration.class);
        Message toggleMessage = Message.builder()
            .senderId(actuatorEntity.getActuatorUuid())
            .payload(BinaryStateClaim.TOGGLE)
            .build();
        BasicSenderActuator actuator = BasicSenderActuator.builder()
            .uuid(actuatorEntity.getActuatorUuid())
            .name(actuatorEntity.getName())
            .description(actuatorEntity.getDescription())
            .messagePublisher(rulerThingContext.getMessagePublisher())
            .messageSupplier(() -> toggleMessage)
            .build();
        pluginContext.getCommandRouteRegistry().registerListener(
                CentralScenePressCommandListener.builder()
                    .actuator(actuator)
                    .sourceNodeId(configuration.getNodeId())
                    .sceneId(configuration.getSceneId())
                    .keyAttribute(CentralSceneKeyAttribute.KEY_PRESSED)
                    .build());
        return actuator;
    }

    @Override
    public void destroyActuator(Actuator actuator) {
        pluginContext.getCommandRouteRegistry().unregisterListener(actuator.getUuid());
    }
}
