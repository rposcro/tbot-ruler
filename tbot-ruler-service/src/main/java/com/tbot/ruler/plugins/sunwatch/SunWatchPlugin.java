package com.tbot.ruler.plugins.sunwatch;

import com.luckycatlabs.sunrisesunset.dto.Location;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;
import com.tbot.ruler.subjects.plugin.PluginsUtil;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;

import java.time.ZoneId;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class SunWatchPlugin extends AbstractSubject implements Plugin {

    private final SunWatchPluginConfiguration pluginConfiguration;
    private final SunLocale sunLocale;
    private final Map<String, SunWatchActuatorBuilder> buildersMap;

    @Builder
    public SunWatchPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
        this.pluginConfiguration = parseConfiguration(rulerPluginContext.getPluginConfiguration(), SunWatchPluginConfiguration.class);
        this.sunLocale = sunLocale(pluginConfiguration);
        this.buildersMap = PluginsUtil.instantiateActuatorsBuilders(SunWatchActuatorBuilder.class, "com.tbot.ruler.plugins.sunwatch").stream()
                .collect(Collectors.toMap(SunWatchActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext thingContext) {
        return buildActuator(actuatorEntity, thingContext, sunLocale);
    }

    @Override
    public void stopActuator(Actuator actuator, String reference) {
        SunWatchActuatorBuilder builder = buildersMap.get(reference);
        if (builder == null) {
            throw new PluginException("Unknown builder reference " + reference);
        }
        builder.destroyActuator(actuator);
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext thingContext, SunLocale sunLocale) {
        SunWatchActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, thingContext, sunLocale);
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
