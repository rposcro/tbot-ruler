package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import com.tbot.ruler.task.SubjectTask;

import java.time.ZoneId;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SingleIntervalActuatorBuilder extends GhostActuatorBuilder {

    private static final String REFERENCE = "single-interval";

    public SingleIntervalActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext thingContext, GhostPluginContext ghostPluginContext) {
        SingleIntervalConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SingleIntervalConfiguration.class);
        RulerPluginContext rulerPluginContext = ghostPluginContext.getRulerPluginContext();

        SingleIntervalAgent actuatorAgent = SingleIntervalAgent.builder()
                .actuatorUuid(actuatorEntity.getActuatorUuid())
                .defaultState(configuration.isActivatedByDefault())
                .subjectStateService(thingContext.getSubjectStateService())
                .build();
        Runnable emissionTask = SingleIntervalEmissionTask.builder()
                .singleIntervalAgent(actuatorAgent)
                .configuration(configuration)
                .actuatorUuid(actuatorEntity.getActuatorUuid())
                .messagePublisher(thingContext.getMessagePublisher())
                .zoneId(ZoneId.of(ghostPluginContext.getGhostPluginConfiguration().getTimeZone()))
                .build();

        return SingleIntervalActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .singleIntervalAgent(actuatorAgent)
                .asynchronousSubjectTask(SubjectTask.startUpTask(emissionTask))
                .asynchronousSubjectTask(SubjectTask.triggerableTask(emissionTask, configuration.getEmissionIntervalMinutes() * 60_000))
                .build();
    }
}
