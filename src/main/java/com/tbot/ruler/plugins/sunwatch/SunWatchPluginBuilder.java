package com.tbot.ruler.plugins.sunwatch;

import com.luckycatlabs.sunrisesunset.dto.Location;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.plugins.PluginsUtil;
import com.tbot.ruler.subjects.BasicPlugin;
import com.tbot.ruler.subjects.Plugin;
import com.tbot.ruler.plugins.PluginBuilder;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.BasicThing;
import com.tbot.ruler.subjects.Thing;
import com.tbot.ruler.exceptions.PluginException;

import java.time.ZoneId;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.instantiateActuatorsBuilders;
import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SunWatchPluginBuilder implements PluginBuilder {

    private final Map<String, SunWatchActuatorBuilder> buildersMap;
    private final PluginBuilderContext pluginBuilderContext;

    public SunWatchPluginBuilder(PluginBuilderContext pluginBuilderContext) {
        this.pluginBuilderContext = pluginBuilderContext;
        this.buildersMap = PluginsUtil.instantiateActuatorsBuilders(SunWatchActuatorBuilder.class, "com.tbot.ruler.plugins.sunwatch").stream()
                .collect(Collectors.toMap(SunWatchActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Plugin buildPlugin(PluginEntity pluginEntity) {
        return BasicPlugin.builder()
                .uuid(pluginEntity.getPluginUuid())
                .name(pluginEntity.getName())
                .things(pluginEntity.getThings().stream()
                        .map(entity -> buildThing(entity))
                        .collect(Collectors.toList()))
                .build();
    }

    private Thing buildThing(ThingEntity thingEntity) {
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
