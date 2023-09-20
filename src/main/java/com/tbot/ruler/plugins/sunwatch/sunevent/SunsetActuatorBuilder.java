package com.tbot.ruler.plugins.sunwatch.sunevent;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.plugins.sunwatch.SunWatchActuatorBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicActuator;
import com.tbot.ruler.task.Task;
import com.tbot.ruler.task.TaskTrigger;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SunsetActuatorBuilder extends SunWatchActuatorBuilder {

    private static final String REFERENCE = "sunset";

    public SunsetActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, PluginBuilderContext builderContext, SunLocale eventLocale) {
        SunEventActuatorConfiguration configuration = parseConfiguration(
                actuatorEntity.getConfiguration(), SunEventActuatorConfiguration.class);
        TaskTrigger emissionTrigger = emissionTrigger(configuration, eventLocale);
        Runnable emissionTask = emissionTask(actuatorEntity, builderContext, configuration);

        return BasicActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .asynchronousTask(Task.triggerableTask(emissionTask, emissionTrigger))
                .build();
    }

    private Runnable emissionTask(ActuatorEntity actuatorEntity, PluginBuilderContext builderContext, SunEventActuatorConfiguration emitterConfiguration) {
        Message message = emitterMessage(actuatorEntity, emitterConfiguration.getSignal());
        return () -> builderContext.getMessagePublisher().publishMessage(message);
    }

    private TaskTrigger emissionTrigger(SunEventActuatorConfiguration configuration, SunLocale eventLocale) {
        SunEventTimer eventTimer = sunEvent(configuration.getShift(), eventLocale);
        return SunEventTrigger.builder()
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
