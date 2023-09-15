package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.util.PackageScanner;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class _JWaveZThingItemsBuilder {

    private final PackageScanner packageScanner = new PackageScanner();
    @Getter
    private final Map<String, _ActuatorBuilder> actuatorBuilderMap;

    public _JWaveZThingItemsBuilder(_JWaveZThingContext thingContext) {
        actuatorBuilderMap = findActuatorsBuilders(thingContext);
    }

    private Map<String, _ActuatorBuilder> findActuatorsBuilders(_JWaveZThingContext thingContext) {
        Map<String, _ActuatorBuilder> buildersMap = new HashMap<>();
        Set<Class<? extends _ActuatorBuilder>> buildersClasses = packageScanner.findAllClassesOfType(_ActuatorBuilder.class, "com.tbot.ruler.plugins.jwavez");
        Set<? extends _ActuatorBuilder> builders = packageScanner.instantiateAll(buildersClasses, thingContext);
        builders.stream().forEach(builder -> buildersMap.put(builder.getReference(), builder));
        return buildersMap;
    }

    public List<Actuator> buildActuators(ThingBuilderContext builderContext) throws PluginException {
        List<Actuator> actuators = new LinkedList<>();
        for (ActuatorDTO actuatorDTO : builderContext.getThingDTO().getActuators()) {
            actuators.add(buildActuator(actuatorDTO));
        }
        return actuators;
    }

    private Actuator buildActuator(ActuatorDTO actuatorDTO) throws PluginException {
        _ActuatorBuilder actuatorBuilder = actuatorBuilderMap.get(actuatorDTO.getRef());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorDTO.getRef() + ", skipping this DTO");
        }
        return actuatorBuilder.buildActuator(actuatorDTO);
    }
}
