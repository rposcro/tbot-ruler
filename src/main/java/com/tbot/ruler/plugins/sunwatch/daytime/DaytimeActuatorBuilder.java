package com.tbot.ruler.plugins.sunwatch.daytime;

import com.tbot.ruler.plugins.sunwatch.AbstractActuatorBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicActuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

public class DaytimeActuatorBuilder extends AbstractActuatorBuilder {

    private static final String REFERENCE = "daytime";

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Actuator buildActuator(ThingBuilderContext builderContext, SunLocale eventLocale) throws PluginException {
        EmitterDTO emitterDTO = findEmitterDTO(REFERENCE, builderContext);
        DaytimeActuatorConfiguration emitterConfiguration = parseEmitterConfiguration(emitterDTO, DaytimeActuatorConfiguration.class);
        SunCalculator sunCalculator = sunCalculator(emitterConfiguration, eventLocale);
        DaytimeEmissionTrigger emissionTrigger = emissionTrigger(emitterConfiguration, sunCalculator);
        DaytimeEmissionTask emissionTask = emissionTask(emitterDTO, builderContext, sunCalculator, emitterConfiguration);

        return BasicActuator.builder()
                .id(emitterDTO.getId())
                .name(emitterDTO.getName())
                .description(emitterDTO.getDescription())
                .startUpTask(emissionTask)
                .triggerableTask(emissionTask)
                .taskTrigger(emissionTrigger)
                .build();
    }

    private DaytimeEmissionTask emissionTask(
            EmitterDTO emitterDTO,
            ThingBuilderContext builderContext,
            SunCalculator sunCalculator,
            DaytimeActuatorConfiguration emitterConfiguration) {
        return DaytimeEmissionTask.builder()
                .emitterId(emitterDTO.getId())
                .messagePublisher(builderContext.getMessagePublisher())
                .dayTimeMessage(emitterMessage(emitterDTO, emitterConfiguration.getDayTimeSignal()))
                .nightTimeMessage(emitterMessage(emitterDTO, emitterConfiguration.getNightTimeSignal()))
                .sunCalculator(sunCalculator)
                .build();
    }

    private DaytimeEmissionTrigger emissionTrigger(DaytimeActuatorConfiguration configuration, SunCalculator sunCalculator) {
        return DaytimeEmissionTrigger.builder()
                .sunCalculator(sunCalculator)
                .emissionIntervalMinutes(configuration.getEmissionInterval())
                .build();
    }

    private SunCalculator sunCalculator(DaytimeActuatorConfiguration configuration, SunLocale eventLocale) {
        return SunCalculator.builder()
                .eventLocale(eventLocale)
                .sunriseShiftMinutes(configuration.getSunriseShift())
                .sunsetShiftMinutes(configuration.getSunsetShift())
                .build();
    }
}
