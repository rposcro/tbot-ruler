package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.payload.Notification;
import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.BasicActuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Getter;

import java.util.TimeZone;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class CronActuatorBuilder {

    public Actuator buildActuator(RulerThingContext thingContext, ActuatorEntity actuatorEntity) {
        CronActuatorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), CronActuatorConfiguration.class);
        TimeZone timeZone = TimeZone.getTimeZone(configuration.getTimeZone());

        return BasicActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .jobBundle(JobBundle.triggerableJobBundle(
                        emissionJob(actuatorEntity, thingContext.getMessagePublisher()),
                        emissionTrigger(configuration.getSchedulePattern(), timeZone)))
                .build();
    }

    private Job emissionJob(ActuatorEntity actuatorEntity, MessagePublisher messagePublisher) {
        return new Job() {
            @Getter
            private final String name = CronActuatorBuilder.class.getSimpleName() + "-Job@" + actuatorEntity.getActuatorUuid();

            @Override
            public void doJob() {
                messagePublisher.publishMessage(messageToSend(actuatorEntity.getActuatorUuid(), Notification.HEARTBEAT));
            }
        };
    }

    private CronEmissionTrigger emissionTrigger(String cronPattern, TimeZone timeZone) {
        return new CronEmissionTrigger(cronPattern, timeZone);
    }

    private Message messageToSend(String emitterId, Object messagePayload) {
        return Message.builder()
            .senderId(emitterId)
            .payload(messagePayload)
            .build();
    }
}
