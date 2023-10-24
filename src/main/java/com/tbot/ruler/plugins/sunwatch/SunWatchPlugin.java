package com.tbot.ruler.plugins.sunwatch;

import com.luckycatlabs.sunrisesunset.dto.Location;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.PluginsUtil;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;

import java.time.ZoneId;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SunWatchPlugin extends AbstractSubject implements Plugin {

    private final RulerPluginContext rulerPluginContext;
    private final SunWatchPluginConfiguration pluginConfiguration;
    private final SunLocale sunLocale;
    private final Map<String, SunWatchActuatorBuilder> buildersMap;

    @Builder
    public SunWatchPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
        this.rulerPluginContext = rulerPluginContext;
        this.pluginConfiguration = parseConfiguration(rulerPluginContext.getPluginConfiguration(), SunWatchPluginConfiguration.class);
        this.sunLocale = sunLocale(pluginConfiguration);
        this.buildersMap = PluginsUtil.instantiateActuatorsBuilders(SunWatchActuatorBuilder.class, "com.tbot.ruler.plugins.sunwatch").stream()
                .collect(Collectors.toMap(SunWatchActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        return buildActuator(actuatorEntity, sunLocale);
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, SunLocale sunLocale) {
        SunWatchActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, rulerPluginContext, sunLocale);
    }

    private SunLocale sunLocale(SunWatchPluginConfiguration configuration) {
        return SunLocale.builder()
                .location(thingLocation(configuration))
                .zoneId(thingZoneId(configuration))
                .build();
    }

    private Location thingLocation(SunWatchPluginConfiguration configuration) {
        Location location = new Location(
                configuration.getLatitude(),
                configuration.getLongitude());
        return location;
    }

    private ZoneId thingZoneId(SunWatchPluginConfiguration configuration) {
        return ZoneId.of(configuration.getTimeZone());
    }
}
