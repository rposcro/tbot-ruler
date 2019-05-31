package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.things.EmissionThread;
import com.tbot.ruler.things.EmissionTrigger;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.ThingBuilderContext;
import com.tbot.ruler.things.dto.EmitterDTO;
import lombok.Builder;

@Builder
public class SunEventEmitterBuilder extends AbstractEmitterBuilder {

    private static final String PARAM_EMITTER_SHIFT = "shift";
    private static final String PARAM_EMITTER_SIGNAL = "signal";

    private ThingBuilderContext builderContext;
    private SunEventLocale eventLocale;

    public Emitter buildEmitter(EmitterDTO emitterDTO) {
        registerEmitterThread(emitterDTO);
        return SunEventEmitter.builder()
            .id(emitterDTO.getId())
            .metadata(emitterMetadata(emitterDTO))
            .build();
    }

    private void registerEmitterThread(EmitterDTO emitterDTO) {
        EmitterSignal signal = emitterSignal(emitterDTO, PARAM_EMITTER_SIGNAL);
        EmissionTrigger trigger = emissionTrigger(emitterDTO);
        EmissionThread thread = EmissionThread.ofSignal(builderContext.getSignalConsumer(), signal);
        builderContext.getServices().getRegistrationService().registerPeriodicEmissionThread(thread, trigger);
    }

    private EmissionTrigger emissionTrigger(EmitterDTO emitterDTO) {
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
