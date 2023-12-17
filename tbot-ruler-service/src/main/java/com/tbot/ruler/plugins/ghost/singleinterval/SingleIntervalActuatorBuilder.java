package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import java.time.ZoneId;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class SingleIntervalActuatorBuilder extends GhostActuatorBuilder {

    private static final String REFERENCE = "single-interval";

    public SingleIntervalActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext thingContext, GhostPluginContext ghostPluginContext) {
        SingleIntervalConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SingleIntervalConfiguration.class);

        SingleIntervalAgent actuatorAgent = SingleIntervalAgent.builder()
                .actuatorUuid(actuatorEntity.getActuatorUuid())
                .defaultState(configuration.isActivatedByDefault())
                .subjectStateService(thingContext.getSubjectStateService())
                .build();
        Job emissionJob = SingleIntervalEmissionJob.builder()
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
                .jobBundle(JobBundle.periodicalJobBundle(emissionJob, configuration.getEmissionIntervalMinutes() * 60_000))
                .build();
    }
}
