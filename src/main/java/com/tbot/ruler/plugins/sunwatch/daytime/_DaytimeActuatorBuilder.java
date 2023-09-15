package com.tbot.ruler.plugins.sunwatch.daytime;

import com.tbot.ruler.plugins.sunwatch._AbstractActuatorBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicActuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

public class _DaytimeActuatorBuilder extends _AbstractActuatorBuilder {

    private static final String REFERENCE = "daytime";

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Actuator buildActuator(ThingBuilderContext builderContext, SunLocale eventLocale) throws PluginException {
        ActuatorDTO actuatorDTO = findActuatorDTO(REFERENCE, builderContext);
        DaytimeActuatorConfiguration emitterConfiguration = parseActuatorConfiguration(actuatorDTO, DaytimeActuatorConfiguration.class);
        SunCalculator sunCalculator = sunCalculator(emitterConfiguration, eventLocale);
        DaytimeEmissionTrigger emissionTrigger = emissionTrigger(emitterConfiguration, sunCalculator);
        DaytimeEmissionTask emissionTask = emissionTask(actuatorDTO, builderContext, sunCalculator, emitterConfiguration);

        return BasicActuator.builder()
                .uuid(actuatorDTO.getUuid())
                .name(actuatorDTO.getName())
                .description(actuatorDTO.getDescription())
                .startUpTask(emissionTask)
                .triggerableTask(emissionTask)
                .taskTrigger(emissionTrigger)
                .build();
    }

    private DaytimeEmissionTask emissionTask(
            ActuatorDTO actuatorDTO,
            ThingBuilderContext builderContext,
            SunCalculator sunCalculator,
            DaytimeActuatorConfiguration emitterConfiguration) {
        return DaytimeEmissionTask.builder()
                .emitterId(actuatorDTO.getUuid())
                .messagePublisher(builderContext.getMessagePublisher())
                .dayTimeMessage(emitterMessage(actuatorDTO, emitterConfiguration.getDayTimeSignal()))
                .nightTimeMessage(emitterMessage(actuatorDTO, emitterConfiguration.getNightTimeSignal()))
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
