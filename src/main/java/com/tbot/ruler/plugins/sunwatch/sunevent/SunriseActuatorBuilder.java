package com.tbot.ruler.plugins.sunwatch.sunevent;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.plugins.sunwatch.AbstractActuatorBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicActuator;
import com.tbot.ruler.things.thread.TaskTrigger;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SunriseActuatorBuilder extends AbstractActuatorBuilder {

    private static final String REFERENCE = "sunrise";

    public SunriseActuatorBuilder() {
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
            .taskTrigger(emissionTrigger)
            .triggerableTask(emissionTask)
            .build();
    }

    private Runnable emissionTask(ActuatorEntity actuatorEntity, PluginBuilderContext builderContext, SunEventActuatorConfiguration configuration) {
        Message message = emitterMessage(actuatorEntity, configuration.getSignal());
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

    private SunEventTimer sunEvent(long sunriseShift, SunLocale eventLocale) {
        SunCalculator sunCalculator = SunCalculator.builder()
                .eventLocale(eventLocale)
                .sunriseShiftMinutes(sunriseShift)
                .sunsetShiftMinutes(0)
                .build();
        return sunCalculator::sunriseForDate;
    }
}
