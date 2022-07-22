package com.tbot.ruler.plugins.sunwatch;

import java.io.IOException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.ThingPluginBuilder;
import com.tbot.ruler.things.builder.dto.EmitterDTO;

import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.util.PackageScanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SunWatchThingBuilder implements ThingPluginBuilder {

    private Map<String, AbstractEmitterBuilder> emitterBuilderMap;

    public SunWatchThingBuilder() {
        emitterBuilderMap = findEmittersBuilders();
    }

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) throws PluginException {
        ThingDTO thingDTO = builderContext.getThingDTO();
        SunWatchThingConfiguration thingConfiguration = buildThingConfiguration(builderContext);
        SunEventLocale sunEventLocale = sunEventLocale(thingConfiguration);
        log.debug("Building Sun Clock: {}, with locale: {}", thingDTO.getName(), sunEventLocale);

        List<Emitter> emitters = buildEmitters(builderContext, sunEventLocale);

        return BasicThing.builder()
            .id(thingDTO.getId())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .emitters(emitters)
            .build();
    }

    private Map<String, AbstractEmitterBuilder> findEmittersBuilders() {
        Map<String, AbstractEmitterBuilder> buildersMap = new HashMap<>();
        PackageScanner packageScanner = new PackageScanner();
        Set<Class<? extends AbstractEmitterBuilder>> buildersClasses = packageScanner.findAllClassesOfType(AbstractEmitterBuilder.class, "com.tbot.ruler.plugins.sunwatch");
        Set<? extends AbstractEmitterBuilder> builders = packageScanner.instantiateAll(buildersClasses);
        builders.stream().forEach(builder -> buildersMap.put(builder.getReference(), builder));
        return buildersMap;
    }

    private SunWatchThingConfiguration buildThingConfiguration(ThingBuilderContext builderContext) throws PluginException {
        try {
            return new ObjectMapper().readerFor(SunWatchThingConfiguration.class).readValue(builderContext.getThingDTO().getConfigurationNode());
        } catch(IOException e) {
            throw new PluginException("Could not parse SunWatch thing's configuration!", e);
        }
    }

    private List<Emitter> buildEmitters(ThingBuilderContext builderContext, SunEventLocale sunEventLocale)
    throws PluginException {
        List<Emitter> emitters = new LinkedList<>();
        for (EmitterDTO emitterDTO : builderContext.getThingDTO().getEmitters()) {
            emitters.add(buildEmitter(builderContext, sunEventLocale, emitterDTO));
        }
        return emitters;
    }

    private Emitter buildEmitter(ThingBuilderContext builderContext, SunEventLocale sunEventLocale, EmitterDTO emitterDTO)
    throws PluginException {
        AbstractEmitterBuilder emitterBuilder = emitterBuilderMap.get(emitterDTO.getRef());
        if (emitterBuilder == null) {
            throw new PluginException("Unknown emitter reference " + emitterDTO.getRef() + ", skipping this DTO");
        }
        return emitterBuilder.buildEmitter(builderContext, sunEventLocale);
    }

    private SunEventLocale sunEventLocale(SunWatchThingConfiguration configuration) {
        return SunEventLocale.builder()
                .location(thingLocation(configuration))
                .zoneId(thingZoneId(configuration))
                .build();
    }

    private Location thingLocation(SunWatchThingConfiguration configuration) {
        Location location = new Location(
                configuration.getLatitude(),
                configuration.getLongitude());
        return location;
    }
    
    private ZoneId thingZoneId(SunWatchThingConfiguration configuration) {
        return ZoneId.of(configuration.getTimeZone());
    }
}
