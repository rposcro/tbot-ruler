package com.tbot.ruler.plugins.sunwatch;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.luckycatlabs.sunrisesunset.dto.Location;
import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.ThingBuilderContext;
import com.tbot.ruler.things.ThingMetadata;
import com.tbot.ruler.things.ThingPluginBuilder;
import com.tbot.ruler.things.dto.EmitterDTO;

import com.tbot.ruler.things.dto.ThingDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SunWatchBuilder implements ThingPluginBuilder {

    private static final String PARAM_LATITUDE = "latitude";
    private static final String PARAM_LONGITUDE = "longitude";
    private static final String PARAM_TIMEZONE = "timezone";

    static final String EMITTER_REF_SUNRISE = "sunrise";
    static final String EMITTER_REF_SUNSET = "sunset";
    static final String EMITTER_REF_DAYTIME = "daytime";

    private SunEventEmitterBuilder sunEventEmitterBuilder;
    private DaytimeEmitterBuilder daytimeEmitterBuilder;

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) {
        ThingDTO thingDTO = builderContext.getThingDTO();
        SunEventLocale sunEventLocale = sunEventLocale(thingDTO);
        log.debug("Building Sun Clock: {}, with locale: {}", thingDTO.getName(), sunEventLocale);

        sunEventEmitterBuilder = SunEventEmitterBuilder.builder()
            .builderContext(builderContext)
            .eventLocale(sunEventLocale)
            .build();

        daytimeEmitterBuilder = DaytimeEmitterBuilder.builder()
            .builderContext(builderContext)
            .eventLocale(sunEventLocale)
            .build();

        List<Emitter> emitters = buildEmitters(thingDTO.getEmitters());

        return BasicThing.builder()
                .metadata(ThingMetadata.fromThingMetadata(thingDTO))
                .emitters(emitters)
                .build();
    }

    private List<Emitter> buildEmitters(List<EmitterDTO> emitterDTOList) {
        if (emitterDTOList != null) {
            List<Emitter> emitters = new ArrayList<>(emitterDTOList.size());
            emitterDTOList.forEach(emitterDTO -> {
                if (EMITTER_REF_SUNRISE.equals(emitterDTO.getRef()) || EMITTER_REF_SUNSET.equals(emitterDTO.getRef())) {
                    emitters.add(sunEventEmitterBuilder.buildEmitter(emitterDTO));
                }
                else if (EMITTER_REF_DAYTIME.equals(emitterDTO.getRef())) {
                    emitters.add(daytimeEmitterBuilder.buildEmitter(emitterDTO));
                }
                else {
                    log.error("Unrecognized emitter reference: {}", emitterDTO.getRef());
                    throw new IllegalArgumentException("Unrecognized emitter reference: " + emitterDTO.getRef());
                }
            });
            return emitters;
        }
        return Collections.emptyList();
    }

    private SunEventLocale sunEventLocale(ThingDTO thingDTO) {
        return SunEventLocale.builder()
                .location(thingLocation(thingDTO))
                .zoneId(thingZoneId(thingDTO))
                .build();
    }

    private Location thingLocation(ThingDTO thingDTO) {
        Location location = new Location(
                thingDTO.getStringParameter(PARAM_LATITUDE),
                thingDTO.getStringParameter(PARAM_LONGITUDE));
        return location;
    }
    
    private ZoneId thingZoneId(ThingDTO thingDTO) {
        return ZoneId.of(thingDTO.getStringParameter(PARAM_TIMEZONE));
    }

}
