package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostThingConfiguration;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.task.Task;

import java.time.ZoneId;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SingleIntervalActuatorBuilder extends GhostActuatorBuilder {

    private static final String REFERENCE = "single-interval";

    public SingleIntervalActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, MessagePublisher messagePublisher, GhostThingConfiguration thingConfiguration) {
        SingleIntervalConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SingleIntervalConfiguration.class);
        SingleIntervalActuatorState actuatorState = new SingleIntervalActuatorState(actuatorEntity.getActuatorUuid(), false);
        Runnable emissionTask = SingleIntervalEmissionTask.builder()
                .configuration(configuration)
                .emitterId(actuatorEntity.getActuatorUuid())
                .messagePublisher(messagePublisher)
                .zoneId(ZoneId.of(thingConfiguration.getTimeZone()))
                .stateAgent(actuatorState)
                .build();

        return SingleIntervalActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .singleIntervalActuatorState(actuatorState)
                .asynchronousTask(Task.startUpTask(emissionTask))
                .asynchronousTask(Task.triggerableTask(emissionTask, configuration.getEmissionIntervalMinutes() * 60_000))
                .build();
    }
}
