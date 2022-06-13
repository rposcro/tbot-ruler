package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import com.tbot.ruler.things.*;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.ThingPluginBuilder;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.util.PackageScanner;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class JWaveZThingBuilder implements ThingPluginBuilder {

    private Map<String, ActuatorBuilder> actuatorBuilderMap;
    private Map<String, EmitterBuilder> emitterBuilderMap;
    private Map<String, CollectorBuilder> collectorBuilderMap;

    public JWaveZThingBuilder() {
        this.actuatorBuilderMap = findActuatorsBuilders();
        this.emitterBuilderMap = findEmittersBuilders();
        this.collectorBuilderMap = findCollectorsBuilders();
    }

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) throws PluginException {
        JWaveZAgent agent = agent(builderContext, commandHandlerMap());
        ThingDTO thingDTO = builderContext.getThingDTO();

        return BasicThing.builder()
            .id(thingDTO.getId())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .emitters(buildEmitters(builderContext, agent))
            .actuators(buildActuators(builderContext, agent))
            .collectors(buildCollectors(builderContext, agent))
            .startUpTask(() -> agent.connect())
            .build();
    }

    private Map<CommandType, SupportedCommandHandler<?>> commandHandlerMap() {
        Map<CommandType, SupportedCommandHandler<?>> handlersMap = new HashMap<>();
        actuatorBuilderMap.values().stream()
                .forEach(builder -> handlersMap.put(builder.getSupportedCommandType(), builder.getSupportedCommandHandler()));
        emitterBuilderMap.values().stream()
                .forEach(builder -> handlersMap.put(builder.getSupportedCommandType(), builder.getSupportedCommandHandler()));
        return handlersMap;
    }

    private Map<String, ActuatorBuilder> findActuatorsBuilders() {
        Map<String, ActuatorBuilder> buildersMap = new HashMap<>();
        PackageScanner packageScanner = new PackageScanner();
        Set<Class<? extends ActuatorBuilder>> buildersClasses = packageScanner.findAllClassesOfType(ActuatorBuilder.class, "com.tbot.ruler.plugins.jwavez");
        Set<? extends ActuatorBuilder> builders = packageScanner.instantiateAll(buildersClasses);
        builders.stream().forEach(builder -> buildersMap.put(builder.getReference(), builder));
        return buildersMap;
    }

    private Map<String, EmitterBuilder> findEmittersBuilders() {
        Map<String, EmitterBuilder> buildersMap = new HashMap<>();
        PackageScanner packageScanner = new PackageScanner();
        Set<Class<? extends EmitterBuilder>> buildersClasses = packageScanner.findAllClassesOfType(EmitterBuilder.class, "com.tbot.ruler.plugins.jwavez");
        Set<? extends EmitterBuilder> builders = packageScanner.instantiateAll(buildersClasses);
        builders.stream().forEach(builder -> buildersMap.put(builder.getReference(), builder));
        return buildersMap;
    }

    private Map<String, CollectorBuilder> findCollectorsBuilders() {
        Map<String, CollectorBuilder> buildersMap = new HashMap<>();
        PackageScanner packageScanner = new PackageScanner();
        Set<Class<? extends CollectorBuilder>> buildersClasses = packageScanner.findAllClassesOfType(CollectorBuilder.class, "com.tbot.ruler.plugins.jwavez");
        Set<? extends CollectorBuilder> builders = packageScanner.instantiateAll(buildersClasses);
        builders.stream().forEach(builder -> buildersMap.put(builder.getReference(), builder));
        return buildersMap;
    }

    private JWaveZAgent agent(ThingBuilderContext builderContext, Map<CommandType, SupportedCommandHandler<?>> commandHandlerMap) {
        return new JWaveZAgent(builderContext, commandHandlerMap);
    }

    private List<Actuator> buildActuators(ThingBuilderContext builderContext, JWaveZAgent agent) throws PluginException {
        List<Actuator> actuators = new LinkedList<>();
        for (ActuatorDTO actuatorDTO : builderContext.getThingDTO().getActuators()) {
            actuators.add(buildActuator(agent, builderContext, actuatorDTO));
        }
        return actuators;
    }

    private Actuator buildActuator(JWaveZAgent agent, ThingBuilderContext context, ActuatorDTO actuatorDTO) throws PluginException {
        ActuatorBuilder actuatorBuilder = actuatorBuilderMap.get(actuatorDTO.getRef());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorDTO.getRef() + ", skipping this DTO");
        }
        return actuatorBuilder.buildActuator(agent, context, actuatorDTO);
    }

    private List<Emitter> buildEmitters(ThingBuilderContext builderContext, JWaveZAgent agent) throws PluginException {
        List<Emitter> emitters = new LinkedList<>();
        for (EmitterDTO emitterDTO : builderContext.getThingDTO().getEmitters()) {
            emitters.add(buildEmitter(agent, builderContext, emitterDTO));
        }
        return emitters;
    }

    private Emitter buildEmitter(JWaveZAgent agent, ThingBuilderContext context, EmitterDTO emitterDTO) throws PluginException {
        EmitterBuilder emitterBuilder = emitterBuilderMap.get(emitterDTO.getRef());
        if (emitterBuilder == null) {
            throw new PluginException("Unknown emitter reference " + emitterDTO.getRef() + ", skipping this DTO");
        }
        return emitterBuilder.buildEmitter(agent, context, emitterDTO);
    }

    private List<Collector> buildCollectors(ThingBuilderContext builderContext, JWaveZAgent agent) throws PluginException {
        List<Collector> collectors = new LinkedList<>();
        for (CollectorDTO collectorDTO : builderContext.getThingDTO().getCollectors()) {
            collectors.add(buildCollector(agent, builderContext, collectorDTO));
        }
        return collectors;
    }

    private Collector buildCollector(JWaveZAgent agent, ThingBuilderContext context, CollectorDTO collectorDTO) throws PluginException {
        CollectorBuilder collectorBuilder = collectorBuilderMap.get(collectorDTO.getRef());
        if (collectorBuilder == null) {
            throw new PluginException("Unknown collector reference " + collectorDTO.getRef() + ", skipping this DTO");
        }
        return collectorBuilder.buildCollector(agent, context, collectorDTO);
    }
}
