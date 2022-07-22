package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.things.BasicEmitter;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

public class DaytimeEmitterBuilder extends AbstractEmitterBuilder {

    private static final String REFERENCE = "daytime";

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Emitter buildEmitter(ThingBuilderContext builderContext, SunEventLocale eventLocale) throws PluginException {
        EmitterDTO emitterDTO = findEmitterDTO(REFERENCE, builderContext);
        DaytimeEmitterConfiguration emitterConfiguration = parseEmitterConfiguration(emitterDTO, DaytimeEmitterConfiguration.class);
        DaytimeEmissionTrigger emissionTrigger = emissionTrigger(emitterConfiguration, eventLocale);
        DaytimeEmissionTask emissionTask = emissionTask(emitterDTO, builderContext, emissionTrigger, emitterConfiguration);

        return BasicEmitter.builder()
            .id(emitterDTO.getId())
            .name(emitterDTO.getName())
            .description(emitterDTO.getDescription())
            .triggerableTask(emissionTask)
            .taskTrigger(emissionTrigger)
            .reportListener(emissionTask::acceptDeliveryReport)
            .build();
    }

    private DaytimeEmissionTask emissionTask(
            EmitterDTO emitterDTO,
            ThingBuilderContext builderContext,
            DaytimeEmissionTrigger trigger,
            DaytimeEmitterConfiguration emitterConfiguration) {
        return DaytimeEmissionTask.builder()
            .emitterId(emitterDTO.getId())
            .daytimeTrigger(trigger)
            .messagePublisher(builderContext.getMessagePublisher())
            .sunriseMessage(emitterMessage(emitterDTO, emitterConfiguration.getSunriseSignal()))
            .sunsetMessage(emitterMessage(emitterDTO, emitterConfiguration.getSunsetSignal()))
            .build();
    }

    private DaytimeEmissionTrigger emissionTrigger(DaytimeEmitterConfiguration emitterConfiguration, SunEventLocale eventLocale) {
        return DaytimeEmissionTrigger.builder()
            .sunCalculator(new SunCalculator(eventLocale))
            .plusSunriseMinutes(emitterConfiguration.getSunriseShift())
            .plusSunsetMinutes(emitterConfiguration.getSunsetShift())
            .zoneId(eventLocale.getZoneId())
            .build();
    }
}
