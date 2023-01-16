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
        SunCalculator sunCalculator = sunCalculator(emitterConfiguration, eventLocale);
        DaytimeEmissionTrigger emissionTrigger = emissionTrigger(emitterConfiguration, eventLocale, sunCalculator);
        DaytimeEmissionTask emissionTask = emissionTask(emitterDTO, builderContext, sunCalculator, emitterConfiguration);

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
            SunCalculator sunCalculator,
            DaytimeEmitterConfiguration emitterConfiguration) {
        return DaytimeEmissionTask.builder()
                .emitterId(emitterDTO.getId())
                .messagePublisher(builderContext.getMessagePublisher())
                .dayTimeMessage(emitterMessage(emitterDTO, emitterConfiguration.getDayTimeSignal()))
                .nightTimeMessage(emitterMessage(emitterDTO, emitterConfiguration.getNightTimeSignal()))
                .sunCalculator(sunCalculator)
                .build();
    }

    private DaytimeEmissionTrigger emissionTrigger(DaytimeEmitterConfiguration configuration, SunEventLocale eventLocale, SunCalculator sunCalculator) {
        return DaytimeEmissionTrigger.builder()
                .sunCalculator(sunCalculator)
                .emissionIntervalMinutes(configuration.getEmissionInterval())
                .zoneId(eventLocale.getZoneId())
                .build();
    }

    private SunCalculator sunCalculator(DaytimeEmitterConfiguration configuration, SunEventLocale eventLocale) {
        return SunCalculator.builder()
                .eventLocale(eventLocale)
                .sunriseShiftMinutes(configuration.getSunriseShift())
                .sunsetShiftMinutes(configuration.getSunsetShift())
                .build();
    }
}
