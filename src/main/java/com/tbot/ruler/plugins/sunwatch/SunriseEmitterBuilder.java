package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.things.BasicEmitter;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.things.thread.TaskTrigger;

public class SunriseEmitterBuilder extends AbstractEmitterBuilder {

    private static final String REFERENCE = "sunrise";

    private ThingBuilderContext builderContext;
    private SunEventLocale eventLocale;

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Emitter buildEmitter(ThingBuilderContext builderContext, SunEventLocale eventLocale) throws PluginException {
        EmitterDTO emitterDTO = findEmitterDTO(REFERENCE, builderContext);
        SunEventEmitterConfiguration emitterConfiguration = parseEmitterConfiguration(emitterDTO, SunEventEmitterConfiguration.class);
        TaskTrigger emissionTrigger = emissionTrigger(emitterConfiguration);
        Runnable emissionTask = emissionTask(emitterDTO, emitterConfiguration);

        return BasicEmitter.builder()
            .id(emitterDTO.getId())
            .name(emitterDTO.getName())
            .description(emitterDTO.getDescription())
            .taskTrigger(emissionTrigger)
            .triggerableTask(emissionTask)
            .build();
    }

    private Runnable emissionTask(EmitterDTO emitterDTO, SunEventEmitterConfiguration emitterConfiguration) {
        Message message = emitterMessage(emitterDTO, emitterConfiguration.getSignal());
        return () -> builderContext.getMessagePublisher().publishMessage(message);
    }

    private TaskTrigger emissionTrigger(SunEventEmitterConfiguration emitterConfiguration) {
        SunEventTimer eventTimer = sunEvent(emitterConfiguration.getShift());
        return SunEventTrigger.builder()
            .timer(eventTimer)
            .zoneId(eventLocale.getZoneId())
            .plusMinutes(emitterConfiguration.getShift())
            .build();
    }

    private SunEventTimer sunEvent(long sunriseShift) {
        SunCalculator sunCalculator = SunCalculator.builder()
                .eventLocale(eventLocale)
                .sunriseShiftMinutes(sunriseShift)
                .sunsetShiftMinutes(0)
                .build();
        return sunCalculator::sunriseForDate;
    }
}
