package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostThingConfiguration;
import com.tbot.ruler.plugins.ghost.GhostThingContext;
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
    public Actuator buildActuator(GhostThingContext ghostThingContext, ActuatorEntity actuatorEntity, GhostThingConfiguration thingConfiguration) {
        RulerPluginContext rulerPluginContext = ghostThingContext.getRulerPluginContext();
        SingleIntervalConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SingleIntervalConfiguration.class);
        SingleIntervalAgent actuatorAgent = SingleIntervalAgent.builder()
                .actuatorUuid(actuatorEntity.getActuatorUuid())
                .defaultState(configuration.isActivatedByDefault())
                .subjectStateService(rulerPluginContext.getSubjectStateService())
                .build();
        Runnable emissionTask = SingleIntervalEmissionTask.builder()
                .ghostThingAgent(ghostThingContext.getGhostThingAgent())
                .singleIntervalAgent(actuatorAgent)
                .configuration(configuration)
                .actuatorUuid(actuatorEntity.getActuatorUuid())
                .messagePublisher(rulerPluginContext.getMessagePublisher())
                .zoneId(ZoneId.of(thingConfiguration.getTimeZone()))
                .build();

        return SingleIntervalActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .singleIntervalAgent(actuatorAgent)
                .asynchronousTask(Task.startUpTask(emissionTask))
                .asynchronousTask(Task.triggerableTask(emissionTask, configuration.getEmissionIntervalMinutes() * 60_000))
                .build();
    }
}
