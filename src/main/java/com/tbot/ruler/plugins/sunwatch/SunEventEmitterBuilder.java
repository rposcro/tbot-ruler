package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.things.BasicEmitter;
import com.tbot.ruler.things.thread.TaskTrigger;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import lombok.Builder;

@Builder
public class SunEventEmitterBuilder extends AbstractEmitterBuilder {

    private static final String PARAM_EMITTER_SHIFT = "shift";
    private static final String PARAM_EMITTER_SIGNAL = "signal";

    private ThingBuilderContext builderContext;
    private SunEventLocale eventLocale;

    public Emitter buildEmitter(EmitterDTO emitterDTO) {
        return BasicEmitter.builder()
            .id(emitterDTO.getId())
            .name(emitterDTO.getName())
            .description(emitterDTO.getDescription())
            .taskTrigger(emissionTrigger(emitterDTO))
            .triggerableTask(emissionTask(emitterDTO))
            .build();
    }

    private Runnable emissionTask(EmitterDTO emitterDTO) {
        Message message = Message.builder()
            .senderId(emitterDTO.getId())
            .payload(emitterPayload(emitterDTO, PARAM_EMITTER_SIGNAL))
            .build();
        return () -> builderContext.getMessagePublisher().acceptMessage(message);
    }

    private TaskTrigger emissionTrigger(EmitterDTO emitterDTO) {
        SunEventTimer eventTimer = sunEvent(emitterDTO);
        return SunEventTrigger.builder()
            .timer(eventTimer)
            .zoneId(eventLocale.getZoneId())
            .plusMinutes(plusMinutes(emitterDTO, PARAM_EMITTER_SHIFT))
            .build();
    }

    private SunEventTimer sunEvent(EmitterDTO emitterDTO) {
        SunCalculator sunCalculator = new SunCalculator(eventLocale);
        switch(emitterDTO.getRef()) {
            case SunWatchBuilder.EMITTER_REF_SUNRISE:
                return sunCalculator::sunriseForDate;
            case SunWatchBuilder.EMITTER_REF_SUNSET:
                return sunCalculator::sunsetForDate;
            default:
                throw new IllegalArgumentException("Unrecognized emitter reference: " + emitterDTO.getRef());
        }
    }
}
