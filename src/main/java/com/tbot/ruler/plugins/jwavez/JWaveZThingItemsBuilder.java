package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.util.PackageScanner;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JWaveZThingItemsBuilder {

    private final PackageScanner packageScanner = new PackageScanner();
    @Getter
    private final Map<String, ActuatorBuilder> actuatorBuilderMap;
    @Getter
    private final Map<String, EmitterBuilder> emitterBuilderMap;
    @Getter
    private final Map<String, CollectorBuilder> collectorBuilderMap;

    public JWaveZThingItemsBuilder(JWaveZThingContext thingContext) {
        actuatorBuilderMap = findActuatorsBuilders(thingContext);
        emitterBuilderMap = findEmittersBuilders(thingContext);
        collectorBuilderMap = findCollectorsBuilders(thingContext);
    }

    private Map<String, ActuatorBuilder> findActuatorsBuilders(JWaveZThingContext thingContext) {
        Map<String, ActuatorBuilder> buildersMap = new HashMap<>();
        Set<Class<? extends ActuatorBuilder>> buildersClasses = packageScanner.findAllClassesOfType(ActuatorBuilder.class, "com.tbot.ruler.plugins.jwavez");
        Set<? extends ActuatorBuilder> builders = packageScanner.instantiateAll(buildersClasses, thingContext);
        builders.stream().forEach(builder -> buildersMap.put(builder.getReference(), builder));
        return buildersMap;
    }

    private Map<String, EmitterBuilder> findEmittersBuilders(JWaveZThingContext thingContext) {
        Map<String, EmitterBuilder> buildersMap = new HashMap<>();
        Set<Class<? extends EmitterBuilder>> buildersClasses = packageScanner.findAllClassesOfType(EmitterBuilder.class, "com.tbot.ruler.plugins.jwavez");
        Set<? extends EmitterBuilder> builders = packageScanner.instantiateAll(buildersClasses, thingContext);
        builders.stream().forEach(builder -> buildersMap.put(builder.getReference(), builder));
        return buildersMap;
    }

    private Map<String, CollectorBuilder> findCollectorsBuilders(JWaveZThingContext thingContext) {
        Map<String, CollectorBuilder> buildersMap = new HashMap<>();
        Set<Class<? extends CollectorBuilder>> buildersClasses = packageScanner.findAllClassesOfType(CollectorBuilder.class, "com.tbot.ruler.plugins.jwavez");
        Set<? extends CollectorBuilder> builders = packageScanner.instantiateAll(buildersClasses, thingContext);
        builders.stream().forEach(builder -> buildersMap.put(builder.getReference(), builder));
        return buildersMap;
    }

    public List<Emitter> buildEmitters(ThingBuilderContext builderContext) throws PluginException {
        List<Emitter> emitters = new LinkedList<>();
        for (EmitterDTO emitterDTO : builderContext.getThingDTO().getEmitters()) {
            emitters.add(buildEmitter(emitterDTO));
        }
        return emitters;
    }

    public List<Collector> buildCollectors(ThingBuilderContext builderContext) throws PluginException {
        List<Collector> collectors = new LinkedList<>();
        for (CollectorDTO collectorDTO : builderContext.getThingDTO().getCollectors()) {
            collectors.add(buildCollector(collectorDTO));
        }
        return collectors;
    }

    public List<Actuator> buildActuators(ThingBuilderContext builderContext) throws PluginException {
        List<Actuator> actuators = new LinkedList<>();
        for (ActuatorDTO actuatorDTO : builderContext.getThingDTO().getActuators()) {
            actuators.add(buildActuator(actuatorDTO));
        }
        return actuators;
    }

    private Emitter buildEmitter(EmitterDTO emitterDTO) throws PluginException {
        EmitterBuilder emitterBuilder = emitterBuilderMap.get(emitterDTO.getRef());
        if (emitterBuilder == null) {
            throw new PluginException("Unknown emitter reference " + emitterDTO.getRef() + ", skipping this DTO");
        }
        return emitterBuilder.buildEmitter(emitterDTO);
    }

    private Collector buildCollector(CollectorDTO collectorDTO) throws PluginException {
        CollectorBuilder collectorBuilder = collectorBuilderMap.get(collectorDTO.getRef());
        if (collectorBuilder == null) {
            throw new PluginException("Unknown collector reference " + collectorDTO.getRef() + ", skipping this DTO");
        }
        return collectorBuilder.buildCollector(collectorDTO);
    }

    private Actuator buildActuator(ActuatorDTO actuatorDTO) throws PluginException {
        ActuatorBuilder actuatorBuilder = actuatorBuilderMap.get(actuatorDTO.getRef());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorDTO.getRef() + ", skipping this DTO");
        }
        return actuatorBuilder.buildActuator(actuatorDTO);
    }
}
