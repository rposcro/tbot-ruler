package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.ThingPluginBuilder;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@Slf4j
public class CronBuilder implements ThingPluginBuilder {

    private static final String EMITTER_REF_SCHEDULER = "scheduler";
    private static final String PARAM_TIME_ZONE = "timeZone";

    private CronSchedulerEmitterBuilder schedulerEmitterBuilder = new CronSchedulerEmitterBuilder();

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) {
        ThingDTO thingDTO = builderContext.getThingDTO();
        log.debug("Building Cron: " + thingDTO.getName());

        List<Emitter > emitters = buildEmitters(builderContext);

        return BasicThing.builder()
            .id(thingDTO.getId())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .emitters(emitters)
            .build();
    }

    private List<Emitter> buildEmitters(ThingBuilderContext builderContext) {
        List<EmitterDTO> emitterDTOs = builderContext.getThingDTO().getEmitters();
        TimeZone timeZone = determineTimeZone(builderContext.getThingDTO());

        if (emitterDTOs != null) {
            List<Emitter> emitters = new ArrayList<>(emitterDTOs.size());
            emitterDTOs.forEach(emitterDTO -> {
                if (EMITTER_REF_SCHEDULER.equals(emitterDTO.getRef())) {
                    emitters.add(schedulerEmitterBuilder.buildEmitter(builderContext, emitterDTO, timeZone));
                }
            });
            return emitters;
        }
        return Collections.emptyList();
    }
    
    private TimeZone determineTimeZone(ThingDTO thingDTO) {
        String tzString = thingDTO.getConfig().get(PARAM_TIME_ZONE);
        TimeZone timeZone = tzString == null ? TimeZone.getDefault() : TimeZone.getTimeZone(tzString);
        return timeZone;
    }
}
