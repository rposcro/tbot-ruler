package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.payload.Notification;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.BasicActuator;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.task.Task;

import java.util.TimeZone;

public class CronSchedulerActuatorBuilder {

    private static final String PARAM_SCHEDULE_PATTERN = "schedulePattern";

    public Actuator buildEmitter(RulerPluginContext builderContext, ActuatorDTO actuatorDTO, TimeZone timeZone) {
        return BasicActuator.builder()
                .uuid(actuatorDTO.getUuid())
                .name(actuatorDTO.getName())
                .description(actuatorDTO.getDescription())
                .asynchronousTask(Task.triggerableTask(
                        emissionTask(actuatorDTO, builderContext.getMessagePublisher()),
                        emissionTrigger(actuatorDTO, timeZone)))
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
