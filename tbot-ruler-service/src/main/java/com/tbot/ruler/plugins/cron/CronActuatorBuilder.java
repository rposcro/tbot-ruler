package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.jobs.JobTrigger;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.BasicReceiverActuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import java.time.Clock;
import java.time.ZoneId;
import java.util.TimeZone;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class CronActuatorBuilder {

    public Actuator buildActuator(RulerThingContext thingContext, ActuatorEntity actuatorEntity) {
        CronActuatorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), CronActuatorConfiguration.class);

        return BasicReceiverActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .jobBundle(JobBundle.triggerableJobBundle(
                        emissionJob(actuatorEntity, configuration, thingContext.getMessagePublisher()),
                        emissionTrigger(configuration)))
                .build();
    }

    private Job emissionJob(ActuatorEntity actuatorEntity, CronActuatorConfiguration configuration, MessagePublisher messagePublisher) {
        ZoneId zoneId = ZoneId.of(configuration.getTimeZone() == null ? TimeZone.getDefault().getID() : configuration.getTimeZone());
        return CronEmissionJob.builder()
            .actuatorUuid(actuatorEntity.getActuatorUuid())
            .messagePublisher(messagePublisher)
            .switchOnSchedule(configuration.getSwitchOnSchedule())
            .switchOffSchedule(configuration.getSwitchOffSchedule())
            .defaultState("on".equalsIgnoreCase(configuration.getDefaultState()))
            .clock(Clock.system(zoneId))
            .build();
    }

    private JobTrigger emissionTrigger(CronActuatorConfiguration actuatorConfiguration) {
        return JobTrigger.periodicalTrigger(actuatorConfiguration.getEmissionInterval() * 60_000);
    }
}
