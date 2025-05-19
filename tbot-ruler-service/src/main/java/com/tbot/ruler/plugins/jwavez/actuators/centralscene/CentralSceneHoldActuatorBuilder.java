package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class CentralSceneHoldActuatorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "central-scene-hold";

    public CentralSceneHoldActuatorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public CentralSceneHoldActuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        CentralSceneHoldConfiguration configuration =
            parseConfiguration(actuatorEntity.getConfiguration(), CentralSceneHoldConfiguration.class);
        CentralSceneHoldActuator actuator = CentralSceneHoldActuator.builder()
            .uuid(actuatorEntity.getActuatorUuid())
            .name(actuatorEntity.getName())
            .description(actuatorEntity.getDescription())
            .messagePublisher(rulerThingContext.getMessagePublisher())
            .stateService(rulerThingContext.getSubjectStateService())
            .minMillisecondsOfHold(configuration.getMinMillisecondsOfHold())
            .maxMillisecondsOfHold(configuration.getMaxMillisecondsOfHold())
            .mode(configuration.getMode())
            .build();
        pluginContext.getCommandRouteRegistry().registerListener(
                CentralSceneHoldCommandListener.builder()
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
