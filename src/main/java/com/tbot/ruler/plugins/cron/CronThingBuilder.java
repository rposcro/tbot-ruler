package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.persistance.json.dto.ThingDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@Slf4j
public class CronThingBuilder {

    private static final String ACTUATOR_REF_SCHEDULER = "scheduler";
    private static final String PARAM_TIME_ZONE = "timeZone";

    private CronSchedulerActuatorBuilder schedulerActuatorBuilder = new CronSchedulerActuatorBuilder();

    public Thing buildThing(PluginBuilderContext builderContext) {
        // TODO: Rewrite the code
        return null;
        //        ThingDTO thingDTO = builderContext.getThingDTO();
//        log.debug("Building Cron: " + thingDTO.getName());
//
//        List<Actuator> actuators = buildActuators(builderContext);
//
//        return BasicThing.builder()
//            .uuid(thingDTO.getUuid())
//            .name(thingDTO.getName())
//            .description(thingDTO.getDescription())
//            .actuators(actuators)
//            .build();
    }

    private List<Actuator> buildActuators(PluginBuilderContext builderContext) {
        //        List<ActuatorDTO> actuatorDTOS = builderContext.getThingDTO().getActuators();
//        TimeZone timeZone = determineTimeZone(builderContext.getThingDTO());
//
//        if (actuatorDTOS != null) {
//            List<Actuator> actuators = new ArrayList<>(actuatorDTOS.size());
//            actuatorDTOS.forEach(dto -> {
//                if (ACTUATOR_REF_SCHEDULER.equals(dto.getRef())) {
//                    actuators.add(schedulerActuatorBuilder.buildEmitter(builderContext, dto, timeZone));
//                }
//            });
//            return actuators;
//        }
        return Collections.emptyList();
    }
    
    private TimeZone determineTimeZone(ThingDTO thingDTO) {
        String tzString = thingDTO.getStringParameter(PARAM_TIME_ZONE);
        TimeZone timeZone = tzString == null ? TimeZone.getDefault() : TimeZone.getTimeZone(tzString);
        return timeZone;
    }
}
