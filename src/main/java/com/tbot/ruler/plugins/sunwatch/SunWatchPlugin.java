package com.tbot.ruler.plugins.sunwatch;

import com.luckycatlabs.sunrisesunset.dto.Location;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.PluginsUtil;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.BasicThing;
import com.tbot.ruler.subjects.Thing;
import lombok.Builder;

import java.time.ZoneId;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SunWatchPlugin extends AbstractSubject implements Plugin {

    private final RulerPluginContext rulerPluginContext;
    private final Map<String, SunWatchActuatorBuilder> buildersMap;

    @Builder
    public SunWatchPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
        this.rulerPluginContext = rulerPluginContext;
        this.buildersMap = PluginsUtil.instantiateActuatorsBuilders(SunWatchActuatorBuilder.class, "com.tbot.ruler.plugins.sunwatch").stream()
                .collect(Collectors.toMap(SunWatchActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Thing startUpThing(ThingEntity thingEntity) {
        SunWatchThingConfiguration configuration = parseConfiguration(thingEntity.getConfiguration(), SunWatchThingConfiguration.class);
        SunLocale sunLocale = sunLocale(configuration);

        return BasicThing.builder()
                .uuid(thingEntity.getThingUuid())
                .name(thingEntity.getName())
                .description(thingEntity.getDescription())
                .actuators(thingEntity.getActuators().stream()
                        .map(entity -> buildActuator(entity, sunLocale))
                        .collect(Collectors.toList()))
                .build();
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, SunLocale sunLocale) {
        SunWatchActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, rulerPluginContext, sunLocale);
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
