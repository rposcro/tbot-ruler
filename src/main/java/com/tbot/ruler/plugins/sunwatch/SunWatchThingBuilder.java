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
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.ThingPluginBuilder;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;

import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.util.PackageScanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SunWatchThingBuilder implements ThingPluginBuilder {

    private Map<String, _AbstractActuatorBuilder> emitterBuilderMap;

    public SunWatchThingBuilder() {
        emitterBuilderMap = findActuatorsBuilders();
    }

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) throws PluginException {
        ThingDTO thingDTO = builderContext.getThingDTO();
        SunWatchThingConfiguration thingConfiguration = buildThingConfiguration(builderContext);
        SunLocale sunEventLocale = sunLocale(thingConfiguration);
        log.debug("Building Sun Clock: {}, with locale: {}", thingDTO.getName(), sunEventLocale);

        List<Actuator> actuators = buildActuators(builderContext, sunEventLocale);

        return BasicThing.builder()
            .uuid(thingDTO.getUuid())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .actuators(actuators)
            .build();
    }

    private Map<String, _AbstractActuatorBuilder> findActuatorsBuilders() {
        Map<String, _AbstractActuatorBuilder> buildersMap = new HashMap<>();
        PackageScanner packageScanner = new PackageScanner();
        Set<Class<? extends _AbstractActuatorBuilder>> buildersClasses = packageScanner.findAllClassesOfType(_AbstractActuatorBuilder.class, "com.tbot.ruler.plugins.sunwatch");
        Set<? extends _AbstractActuatorBuilder> builders = packageScanner.instantiateAll(buildersClasses);
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

    private List<Actuator> buildActuators(ThingBuilderContext builderContext, SunLocale sunEventLocale)
    throws PluginException {
        List<Actuator> actuators = new LinkedList<>();
        for (ActuatorDTO actuatorDTO : builderContext.getThingDTO().getActuators()) {
            actuators.add(buildActuator(builderContext, sunEventLocale, actuatorDTO));
        }
        return actuators;
    }

    private Actuator buildActuator(ThingBuilderContext builderContext, SunLocale sunEventLocale, ActuatorDTO actuatorDTO)
    throws PluginException {
        _AbstractActuatorBuilder actuatorBuilder = emitterBuilderMap.get(actuatorDTO.getRef());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorDTO.getRef() + ", skipping this DTO");
        }
        return actuatorBuilder.buildActuator(builderContext, sunEventLocale);
    }

    private SunLocale sunLocale(SunWatchThingConfiguration configuration) {
        return SunLocale.builder()
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
