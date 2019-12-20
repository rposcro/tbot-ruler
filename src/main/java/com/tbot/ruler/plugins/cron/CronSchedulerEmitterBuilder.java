package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.HeartBeatPayload;
import com.tbot.ruler.things.BasicEmitter;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.ItemId;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;

import java.util.TimeZone;
import java.util.function.Consumer;

public class CronSchedulerEmitterBuilder {

    private static final String PARAM_SCHEDULE_PATTERN = "schedulePattern";

    public Emitter buildEmitter(ThingBuilderContext builderContext, EmitterDTO emitterDTO, TimeZone timeZone) {
        return BasicEmitter.builder()
            .emitterId(emitterDTO.getId())
            .name(emitterDTO.getName())
            .description(emitterDTO.getDescription())
            .emissionTask(emissionTask(emitterDTO, builderContext.getMessagePublisher()))
            .emissionTrigger(emissionTrigger(emitterDTO, timeZone))
            .build();
    }

    private Runnable emissionTask(EmitterDTO emitterDTO, Consumer<Message> messageConsumer) {
        return () -> messageConsumer.accept(messageToSend(emitterDTO.getId(), new HeartBeatPayload()));
    }

    private CronEmissionTrigger emissionTrigger(EmitterDTO emitterDTO, TimeZone timeZone) {
        String pattern = emitterDTO.getConfig().get(PARAM_SCHEDULE_PATTERN);
        return new CronEmissionTrigger(pattern, timeZone);
    }

    private Message messageToSend(ItemId emitterId, MessagePayload messagePayload) {
        return Message.builder()
            .senderId(emitterId)
            .payload(messagePayload)
            .build();
    }
}
