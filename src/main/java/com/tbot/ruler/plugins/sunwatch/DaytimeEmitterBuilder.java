package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.things.BasicEmitter;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import lombok.Builder;

@Builder
public class DaytimeEmitterBuilder extends AbstractEmitterBuilder {

    private static final String PARAM_EMITTER_SHIFT_SUNRISE = "sunriseShift";
    private static final String PARAM_EMITTER_SHIFT_SUNSET = "sunsetShift";
    private static final String PARAM_EMITTER_SIGNAL_SUNRISE = "sunriseSignal";
    private static final String PARAM_EMITTER_SIGNAL_SUNSET = "sunsetSignal";

    private ThingBuilderContext builderContext;
    private SunEventLocale eventLocale;

    public Emitter buildEmitter(EmitterDTO emitterDTO) {
        DaytimeEmissionTrigger emissionTrigger = emissionTrigger(emitterDTO);
        return BasicEmitter.builder()
            .id(emitterDTO.getId())
            .name(emitterDTO.getName())
            .description(emitterDTO.getDescription())
            .triggerableTask(emissionTask(emitterDTO, emissionTrigger))
            .taskTrigger(emissionTrigger)
            .build();
    }

    private DaytimeEmissionTask emissionTask(EmitterDTO emitterDTO, DaytimeEmissionTrigger trigger) {
        return DaytimeEmissionTask.builder()
            .emitterId(emitterDTO.getId())
            .daytimeTrigger(trigger)
            .messageConsumer(builderContext.getMessagePublisher())
            .sunriseMessage(emitterMessage(emitterDTO, PARAM_EMITTER_SIGNAL_SUNRISE))
            .sunsetMessage(emitterMessage(emitterDTO, PARAM_EMITTER_SIGNAL_SUNSET))
            .build();
    }

    private DaytimeEmissionTrigger emissionTrigger(EmitterDTO emitterDTO) {
        return DaytimeEmissionTrigger.builder()
            .sunCalculator(new SunCalculator(eventLocale))
            .plusSunriseMinutes(plusMinutes(emitterDTO, PARAM_EMITTER_SHIFT_SUNRISE))
            .plusSunsetMinutes(plusMinutes(emitterDTO, PARAM_EMITTER_SHIFT_SUNSET))
            .zoneId(eventLocale.getZoneId())
            .build();
    }
}
