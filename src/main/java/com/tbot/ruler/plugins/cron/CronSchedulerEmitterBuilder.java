package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.signals.SignalValue;
import com.tbot.ruler.things.EmissionThread;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.EmitterMetadata;
import com.tbot.ruler.things.ThingBuilderContext;
import com.tbot.ruler.things.dto.EmitterDTO;
import com.tbot.ruler.signals.SignalValueType;

import java.util.TimeZone;

public class CronSchedulerEmitterBuilder {

    private static final String PARAM_SIGNAL_TYPE = "signalType";
    private static final String PARAM_SIGNAL_VALUE = "signalValue";
    private static final String PARAM_SCHEDULE_PATTERN = "schedulePattern";

    public CronSchedulerEmitter buildEmitter(ThingBuilderContext builderContext, EmitterDTO emitterDTO, TimeZone timeZone) {
        EmitterId emitterId = emitterDTO.getId();
        EmitterSignal signal = parseSignal(emitterDTO, emitterId);
        CronSchedulerEmitter emitter = CronSchedulerEmitter.builder()
            .id(emitterId)
            .metadata(buildMetadata(emitterDTO, signal.getSignalValue().getSignalValueType(), emitterId))
            .build();
        registerEmitterThread(builderContext, emitterDTO, timeZone, signal);
        return emitter;
    }

    private EmitterMetadata buildMetadata(EmitterDTO emitterDTO, SignalValueType signalType, EmitterId emitterId) {
        return EmitterMetadata.builder()
                .id(emitterId)
                .name(emitterDTO.getName())
                .description(emitterDTO.getName())
                .emittedSignalType(signalType)
                .build();
    }

    private EmitterSignal parseSignal(EmitterDTO emitterDTO, EmitterId emitterId) {
        return new EmitterSignal(
                SignalValue.parse(
                    emitterDTO.getStringParameter(PARAM_SIGNAL_TYPE),
                    emitterDTO.getStringParameter(PARAM_SIGNAL_VALUE)),
                emitterId);
    }

    private void registerEmitterThread(
            ThingBuilderContext builderContext,
            EmitterDTO emitterDTO,
            TimeZone timeZone,
            EmitterSignal signal) {
        builderContext.getServices().getRegistrationService().registerPeriodicEmissionThread(
                EmissionThread.ofSignal(builderContext.getSignalConsumer(), signal),
                emissionTrigger(emitterDTO, timeZone));
    }

    private CronEmissionTrigger emissionTrigger(EmitterDTO emitterDTO, TimeZone timeZone) {
        String pattern = emitterDTO.getConfig().get(PARAM_SCHEDULE_PATTERN);
        return new CronEmissionTrigger(pattern, timeZone);
    }
}
