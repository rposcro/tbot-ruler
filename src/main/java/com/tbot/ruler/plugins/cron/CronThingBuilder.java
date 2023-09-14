package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.ThingPluginBuilder;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@Slf4j
public class CronThingBuilder implements ThingPluginBuilder {

    private static final String ACTUATOR_REF_SCHEDULER = "scheduler";
    private static final String PARAM_TIME_ZONE = "timeZone";

    private CronSchedulerActuatorBuilder schedulerActuatorBuilder = new CronSchedulerActuatorBuilder();

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) {
        ThingDTO thingDTO = builderContext.getThingDTO();
        log.debug("Building Cron: " + thingDTO.getName());

        List<Actuator> actuators = buildActuators(builderContext);

        return BasicThing.builder()
            .id(thingDTO.getId())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .actuators(actuators)
            .build();
    }

    private List<Actuator> buildActuators(ThingBuilderContext builderContext) {
        List<ActuatorDTO> actuatorDTOS = builderContext.getThingDTO().getActuators();
        TimeZone timeZone = determineTimeZone(builderContext.getThingDTO());

        if (actuatorDTOS != null) {
            List<Actuator> actuators = new ArrayList<>(actuatorDTOS.size());
            actuatorDTOS.forEach(dto -> {
                if (ACTUATOR_REF_SCHEDULER.equals(dto.getRef())) {
                    actuators.add(schedulerActuatorBuilder.buildEmitter(builderContext, dto, timeZone));
                }
            });
            return actuators;
        }
        return Collections.emptyList();
    }
    
    private TimeZone determineTimeZone(ThingDTO thingDTO) {
        String tzString = thingDTO.getStringParameter(PARAM_TIME_ZONE);
        TimeZone timeZone = tzString == null ? TimeZone.getDefault() : TimeZone.getTimeZone(tzString);
        return timeZone;
    }
}
