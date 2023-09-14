package com.tbot.ruler.plugins.sunwatch.sunevent;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.plugins.sunwatch.AbstractActuatorBuilder;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import com.tbot.ruler.plugins.sunwatch.SunLocale;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicActuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.things.thread.TaskTrigger;

public class SunriseActuatorBuilder extends AbstractActuatorBuilder {

    private static final String REFERENCE = "sunrise";

    private ThingBuilderContext builderContext;
    private SunLocale eventLocale;

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Actuator buildActuator(ThingBuilderContext builderContext, SunLocale eventLocale) throws PluginException {
        ActuatorDTO actuatorDTO = findActuatorDTO(REFERENCE, builderContext);
        SunEventActuatorConfiguration emitterConfiguration = parseActuatorConfiguration(actuatorDTO, SunEventActuatorConfiguration.class);
        TaskTrigger emissionTrigger = emissionTrigger(emitterConfiguration);
        Runnable emissionTask = emissionTask(actuatorDTO, emitterConfiguration);

        return BasicActuator.builder()
            .id(actuatorDTO.getId())
            .name(actuatorDTO.getName())
            .description(actuatorDTO.getDescription())
            .taskTrigger(emissionTrigger)
            .triggerableTask(emissionTask)
            .build();
    }

    private Runnable emissionTask(ActuatorDTO actuatorDTO, SunEventActuatorConfiguration emitterConfiguration) {
        Message message = emitterMessage(actuatorDTO, emitterConfiguration.getSignal());
        return () -> builderContext.getMessagePublisher().publishMessage(message);
    }

    private TaskTrigger emissionTrigger(SunEventActuatorConfiguration emitterConfiguration) {
        SunEventTimer eventTimer = sunEvent(emitterConfiguration.getShift());
        return SunEventTrigger.builder()
            .timer(eventTimer)
            .zoneId(eventLocale.getZoneId())
            .plusMinutes(emitterConfiguration.getShift())
            .build();
    }

    private SunEventTimer sunEvent(long sunriseShift) {
        SunCalculator sunCalculator = SunCalculator.builder()
                .eventLocale(eventLocale)
                .sunriseShiftMinutes(sunriseShift)
                .sunsetShiftMinutes(0)
                .build();
        return sunCalculator::sunriseForDate;
    }
}
