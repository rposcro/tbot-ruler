package com.tbot.ruler.plugins.sunwatch.sunevent;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.plugins.sunwatch.AbstractEmitterBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.things.BasicEmitter;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.things.thread.TaskTrigger;

public class SunsetEmitterBuilder extends AbstractEmitterBuilder {

    private static final String REFERENCE = "sunset";

    private ThingBuilderContext builderContext;
    private SunLocale eventLocale;

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Emitter buildEmitter(ThingBuilderContext builderContext, SunLocale eventLocale) throws PluginException {
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

    private SunEventTimer sunEvent(long shiftMinutes) {
        SunCalculator sunCalculator = SunCalculator.builder()
                .eventLocale(eventLocale)
                .sunriseShiftMinutes(0)
                .sunsetShiftMinutes(shiftMinutes)
                .build();
        return sunCalculator::sunsetForDate;
    }
}
