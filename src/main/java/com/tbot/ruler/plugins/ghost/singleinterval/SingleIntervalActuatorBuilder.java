package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostThingConfiguration;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.thread.RegularEmissionTrigger;

import java.time.ZoneId;
import java.util.Optional;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SingleIntervalActuatorBuilder extends GhostActuatorBuilder {

    private static final String REFERENCE = "single-interval";

    public SingleIntervalActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, MessagePublisher messagePublisher, GhostThingConfiguration thingConfiguration) {
        SingleIntervalConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SingleIntervalConfiguration.class);
        SingleIntervalStateAgent stateAgent = new SingleIntervalStateAgent();
        Optional<Runnable> emissionTask = Optional.of(SingleIntervalEmissionTask.builder()
                .configuration(configuration)
                .emitterId(actuatorEntity.getActuatorUuid())
                .messagePublisher(messagePublisher)
                .zoneId(ZoneId.of(thingConfiguration.getTimeZone()))
                .stateAgent(stateAgent)
                .build());

        return SingleIntervalActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .singleIntervalStateAgent(stateAgent)
                .startUpTask(emissionTask)
                .triggerableTask(emissionTask)
                .taskTrigger(Optional.of(new RegularEmissionTrigger(configuration.getEmissionIntervalMinutes() * 60_000)))
                .build();
    }
}
