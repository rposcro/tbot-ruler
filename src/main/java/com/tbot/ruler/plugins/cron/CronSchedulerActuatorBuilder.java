package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.model.Notification;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicActuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;

import java.util.TimeZone;

public class CronSchedulerActuatorBuilder {

    private static final String PARAM_SCHEDULE_PATTERN = "schedulePattern";

    public Actuator buildEmitter(ThingBuilderContext builderContext, ActuatorDTO actuatorDTO, TimeZone timeZone) {
        return BasicActuator.builder()
            .uuid(actuatorDTO.getUuid())
            .name(actuatorDTO.getName())
            .description(actuatorDTO.getDescription())
            .triggerableTask(emissionTask(actuatorDTO, builderContext.getMessagePublisher()))
            .taskTrigger(emissionTrigger(actuatorDTO, timeZone))
            .build();
    }

    private Runnable emissionTask(ActuatorDTO actuatorDTO, MessagePublisher messagePublisher) {
        return () -> messagePublisher.publishMessage(messageToSend(actuatorDTO.getUuid(), Notification.HEARTBEAT));
    }

    private CronEmissionTrigger emissionTrigger(ActuatorDTO actuatorDTO, TimeZone timeZone) {
        String pattern = actuatorDTO.getStringParameter(PARAM_SCHEDULE_PATTERN);
        return new CronEmissionTrigger(pattern, timeZone);
    }

    private Message messageToSend(String emitterId, Object messagePayload) {
        return Message.builder()
            .senderId(emitterId)
            .payload(messagePayload)
            .build();
    }
}
