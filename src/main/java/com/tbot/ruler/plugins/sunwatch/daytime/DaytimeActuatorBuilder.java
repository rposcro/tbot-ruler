package com.tbot.ruler.plugins.sunwatch.daytime;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.plugins.sunwatch.SunWatchActuatorBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicActuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class DaytimeActuatorBuilder extends SunWatchActuatorBuilder {

    private static final String REFERENCE = "daytime";

    public DaytimeActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, PluginBuilderContext builderContext, SunLocale eventLocale) {
        DaytimeActuatorConfiguration emitterConfiguration = parseConfiguration(actuatorEntity.getConfiguration(), DaytimeActuatorConfiguration.class);
        SunCalculator sunCalculator = sunCalculator(emitterConfiguration, eventLocale);
        DaytimeEmissionTrigger emissionTrigger = emissionTrigger(emitterConfiguration, sunCalculator);
        DaytimeEmissionTask emissionTask = emissionTask(actuatorEntity, builderContext, sunCalculator, emitterConfiguration);

        return BasicActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .startUpTask(emissionTask)
                .triggerableTask(emissionTask)
                .taskTrigger(emissionTrigger)
                .build();
    }

    private DaytimeEmissionTask emissionTask(
            ActuatorEntity actuatorEntity,
            PluginBuilderContext pluginBuilderContext,
            SunCalculator sunCalculator,
            DaytimeActuatorConfiguration emitterConfiguration) {
        return DaytimeEmissionTask.builder()
                .emitterId(actuatorEntity.getActuatorUuid())
                .messagePublisher(pluginBuilderContext.getMessagePublisher())
                .dayTimeMessage(emitterMessage(actuatorEntity, emitterConfiguration.getDayTimeSignal()))
                .nightTimeMessage(emitterMessage(actuatorEntity, emitterConfiguration.getNightTimeSignal()))
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
