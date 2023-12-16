package com.tbot.ruler.plugins.sunwatch.daytime;

import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.sunwatch.SunWatchActuatorBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.BasicActuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class DaytimeActuatorBuilder extends SunWatchActuatorBuilder {

    private static final String REFERENCE = "daytime";

    public DaytimeActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext thingContext, SunLocale eventLocale) {
        DaytimeActuatorConfiguration actuatorConfiguration = parseConfiguration(actuatorEntity.getConfiguration(), DaytimeActuatorConfiguration.class);
        SunCalculator sunCalculator = sunCalculator(actuatorConfiguration, eventLocale);
        DaytimeEmissionJobTrigger emissionJobTrigger = emissionJobTrigger(actuatorConfiguration, sunCalculator);
        DaytimeEmissionJob emissionJob = emissionJob(actuatorEntity, thingContext, sunCalculator, actuatorConfiguration);

        return BasicActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .jobBundle(JobBundle.triggerableJobBundle(emissionJob, emissionJobTrigger))
                .build();
    }

    private DaytimeEmissionJob emissionJob(
            ActuatorEntity actuatorEntity,
            RulerThingContext thingContext,
            SunCalculator sunCalculator,
            DaytimeActuatorConfiguration emitterConfiguration) {
        return DaytimeEmissionJob.builder()
                .actuatorUuid(actuatorEntity.getActuatorUuid())
                .messagePublisher(thingContext.getMessagePublisher())
                .dayTimeMessage(emitterMessage(actuatorEntity, emitterConfiguration.getDayTimeSignal()))
                .nightTimeMessage(emitterMessage(actuatorEntity, emitterConfiguration.getNightTimeSignal()))
                .sunCalculator(sunCalculator)
                .build();
    }

    private DaytimeEmissionJobTrigger emissionJobTrigger(DaytimeActuatorConfiguration configuration, SunCalculator sunCalculator) {
        return DaytimeEmissionJobTrigger.builder()
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
