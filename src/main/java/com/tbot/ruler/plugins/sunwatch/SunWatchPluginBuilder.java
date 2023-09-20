package com.tbot.ruler.plugins.sunwatch;

import com.luckycatlabs.sunrisesunset.dto.Location;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.BasicPlugin;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.exceptions.PluginException;

import java.time.ZoneId;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.findActuatorsBuilders;
import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SunWatchPluginBuilder implements PluginBuilder {

    private final Map<String, SunWatchActuatorBuilder> buildersMap;

    public SunWatchPluginBuilder() {
        buildersMap = findActuatorsBuilders(SunWatchActuatorBuilder.class, "com.tbot.ruler.plugins.sunwatch").stream()
                .collect(Collectors.toMap(SunWatchActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Plugin buildPlugin(PluginBuilderContext builderContext) {
        return BasicPlugin.builder()
                .uuid(builderContext.getPluginEntity().getPluginUuid())
                .name(builderContext.getPluginEntity().getName())
                .things(builderContext.getPluginEntity().getThings().stream()
                        .map(entity -> buildThing(entity, builderContext))
                        .collect(Collectors.toList()))
                .build();
    }

    private Thing buildThing(ThingEntity thingEntity, PluginBuilderContext pluginBuilderContext) {
        SunWatchThingConfiguration configuration = parseConfiguration(thingEntity.getConfiguration(), SunWatchThingConfiguration.class);
        SunLocale sunLocale = sunLocale(configuration);

        return BasicThing.builder()
                .uuid(thingEntity.getThingUuid())
                .name(thingEntity.getName())
                .description(thingEntity.getDescription())
                .actuators(thingEntity.getActuators().stream()
                        .map(entity -> buildActuator(entity, pluginBuilderContext, sunLocale))
                        .collect(Collectors.toList()))
                .build();
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, PluginBuilderContext pluginBuilderContext, SunLocale sunLocale) {
        SunWatchActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, pluginBuilderContext, sunLocale);
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
