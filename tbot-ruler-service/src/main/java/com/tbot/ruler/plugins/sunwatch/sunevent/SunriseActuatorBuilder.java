package com.tbot.ruler.plugins.sunwatch.sunevent;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.sunwatch.SunWatchActuatorBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.BasicActuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import com.tbot.ruler.task.SubjectTask;
import com.tbot.ruler.task.TaskTrigger;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SunriseActuatorBuilder extends SunWatchActuatorBuilder {

    private static final String REFERENCE = "sunrise";

    public SunriseActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext thingContext, SunLocale eventLocale) {
        SunEventActuatorConfiguration configuration = parseConfiguration(
                actuatorEntity.getConfiguration(), SunEventActuatorConfiguration.class);
        TaskTrigger emissionTrigger = emissionTrigger(configuration, eventLocale);
        Runnable emissionTask = emissionTask(actuatorEntity, thingContext, configuration);

        return BasicActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .asynchronousSubjectTask(SubjectTask.triggerableTask(emissionTask, emissionTrigger))
                .build();
    }

    private Runnable emissionTask(ActuatorEntity actuatorEntity, RulerThingContext thingContext, SunEventActuatorConfiguration configuration) {
        Message message = emitterMessage(actuatorEntity, configuration.getSignal());
        return () -> thingContext.getMessagePublisher().publishMessage(message);
    }

    private TaskTrigger emissionTrigger(SunEventActuatorConfiguration configuration, SunLocale eventLocale) {
        SunEventTimer eventTimer = sunEvent(configuration.getShift(), eventLocale);
        return SunEventTrigger.builder()
            .timer(eventTimer)
            .zoneId(eventLocale.getZoneId())
            .plusMinutes(configuration.getShift())
            .build();
    }

    private SunEventTimer sunEvent(long sunriseShift, SunLocale eventLocale) {
        SunCalculator sunCalculator = SunCalculator.builder()
                .eventLocale(eventLocale)
                .sunriseShiftMinutes(sunriseShift)
                .sunsetShiftMinutes(0)
                .build();
        return sunCalculator::sunriseForDate;
    }
}
