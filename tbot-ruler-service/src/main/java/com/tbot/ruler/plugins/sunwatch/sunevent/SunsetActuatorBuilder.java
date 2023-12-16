package com.tbot.ruler.plugins.sunwatch.sunevent;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.jobs.JobTrigger;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.sunwatch.SunWatchActuatorBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.BasicActuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SunsetActuatorBuilder extends SunWatchActuatorBuilder {

    private static final String REFERENCE = "sunset";

    public SunsetActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext thingContext, SunLocale eventLocale) {
        SunEventActuatorConfiguration configuration = parseConfiguration(
                actuatorEntity.getConfiguration(), SunEventActuatorConfiguration.class);
        JobTrigger emissionJobTrigger = emissionJobTrigger(configuration, eventLocale);
        Job emissionJob = emissionJob(actuatorEntity, thingContext, configuration);

        return BasicActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .jobBundle(JobBundle.triggerableJobBundle(emissionJob, emissionJobTrigger))
                .build();
    }

    private Job emissionJob(ActuatorEntity actuatorEntity, RulerThingContext thingContext, SunEventActuatorConfiguration emitterConfiguration) {
        Message message = emitterMessage(actuatorEntity, emitterConfiguration.getSignal());
        return Job.namedJob(
                "SunWatch-Sunset-Job@" + actuatorEntity.getActuatorUuid(),
                () -> thingContext.getMessagePublisher().publishMessage(message));
    }

    private JobTrigger emissionJobTrigger(SunEventActuatorConfiguration configuration, SunLocale eventLocale) {
        SunEventTimer eventTimer = sunEvent(configuration.getShift(), eventLocale);
        return SunEventJobTrigger.builder()
            .timer(eventTimer)
            .zoneId(eventLocale.getZoneId())
            .plusMinutes(configuration.getShift())
            .build();
    }

    private SunEventTimer sunEvent(long shiftMinutes, SunLocale eventLocale) {
        SunCalculator sunCalculator = SunCalculator.builder()
                .eventLocale(eventLocale)
                .sunriseShiftMinutes(0)
                .sunsetShiftMinutes(shiftMinutes)
                .build();
        return sunCalculator::sunsetForDate;
    }
}
