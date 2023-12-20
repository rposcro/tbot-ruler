package com.tbot.ruler.plugins.deputy.healthcheck;

import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.deputy.DeputyActuatorBuilder;
import com.tbot.ruler.plugins.deputy.DeputyPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.BasicActuator;

public class HealthCheckActuatorBuilder extends DeputyActuatorBuilder {

    private static final long DEFAULT_FREQUENCY = 15_000;

    public HealthCheckActuatorBuilder() {
        super("health-check");
    }

    public Actuator buildActuator(ActuatorEntity actuatorEntity, DeputyPluginContext deputyPluginContext) {
        return BasicActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .jobBundle(JobBundle.periodicalJobBundle(healthCheckJob(actuatorEntity, deputyPluginContext), DEFAULT_FREQUENCY))
                .build();
    }

    private HealthCheckJob healthCheckJob(ActuatorEntity actuatorEntity, DeputyPluginContext deputyPluginContext) {
        return HealthCheckJob.builder()
                .actuatorUuid(actuatorEntity.getActuatorUuid())
                .deputyServiceApi(deputyPluginContext.getDeputyServiceApi())
                .messagePublisher(deputyPluginContext.getRulerThingContext().getMessagePublisher())
                .build();
    }
}
