package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.things.EmissionThread;
import com.tbot.ruler.things.ThingBuilderContext;
import com.tbot.ruler.things.dto.EmitterDTO;
import lombok.Builder;

@Builder
public class DaytimeEmitterBuilder extends AbstractEmitterBuilder {

    private static final String PARAM_EMITTER_SHIFT_SUNRISE = "sunriseShift";
    private static final String PARAM_EMITTER_SHIFT_SUNSET = "sunsetShift";
    private static final String PARAM_EMITTER_SIGNAL_SUNRISE = "sunriseSignal";
    private static final String PARAM_EMITTER_SIGNAL_SUNSET = "sunsetSignal";

    private ThingBuilderContext builderContext;
    private SunEventLocale eventLocale;

    public DaytimeEmitter buildEmitter(EmitterDTO emitterDTO) {
        DaytimeTrigger trigger = emissionTrigger(emitterDTO);
        DaytimeEmitter emitter = emitter(emitterDTO, trigger);
        registerEmitterThread(trigger, emitter);
        return emitter;
    }

    private DaytimeEmitter emitter(EmitterDTO emitterDTO, DaytimeTrigger daytimeTrigger) {
        EmitterSignal sunriseSignal = emitterSignal(emitterDTO, PARAM_EMITTER_SIGNAL_SUNRISE);
        EmitterSignal sunsetSignal = emitterSignal(emitterDTO, PARAM_EMITTER_SIGNAL_SUNSET);
        return DaytimeEmitter.builder()
            .daytimeTrigger(daytimeTrigger)
            .id(emitterDTO.getId())
            .metadata(emitterMetadata(emitterDTO))
            .sunriseSignal(sunriseSignal)
            .sunsetSignal(sunsetSignal)
            .signalConsumer(builderContext.getSignalConsumer())
            .build();
    }

    private void registerEmitterThread(DaytimeTrigger trigger, DaytimeEmitter emitter) {
        EmissionThread thread = EmissionThread.ofRunnable(emitter);
        builderContext.getServices().getRegistrationService().registerPeriodicEmissionThread(thread, trigger);
    }

    private DaytimeTrigger emissionTrigger(EmitterDTO emitterDTO) {
        return DaytimeTrigger.builder()
            .sunCalculator(new SunCalculator(eventLocale))
            .plusSunriseMinutes(plusMinutes(emitterDTO, PARAM_EMITTER_SHIFT_SUNRISE))
            .plusSunsetMinutes(plusMinutes(emitterDTO, PARAM_EMITTER_SHIFT_SUNSET))
            .zoneId(eventLocale.getZoneId())
            .build();
    }
}
