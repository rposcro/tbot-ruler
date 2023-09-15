package com.tbot.ruler.plugins.ghost;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.messages.MessagePublisher;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class GhostThingBuilder implements ThingPluginBuilder {

    private Map<String, _GhostActuatorBuilder> actuatorBuilderMap;

    public GhostThingBuilder() {
        actuatorBuilderMap = findActuatorsBuilders();
    }

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) throws PluginException {
        ThingDTO thingDTO = builderContext.getThingDTO();
        GhostThingConfiguration thingConfiguration = buildThingConfiguration(builderContext);
        log.debug("Building Ghost thing: {}", thingDTO.getName());

        List<Actuator> actuators = buildActuators(builderContext, thingConfiguration);

        return BasicThing.builder()
            .uuid(thingDTO.getUuid())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .actuators(actuators)
            .build();
    }

    private Map<String, _GhostActuatorBuilder> findActuatorsBuilders() {
        Map<String, _GhostActuatorBuilder> buildersMap = new HashMap<>();
        PackageScanner packageScanner = new PackageScanner();
        Set<Class<? extends _GhostActuatorBuilder>> buildersClasses = packageScanner.findAllClassesOfType(_GhostActuatorBuilder.class, "com.tbot.ruler.plugins.ghost");
        Set<? extends _GhostActuatorBuilder> builders = packageScanner.instantiateAll(buildersClasses);
        builders.stream().forEach(builder -> buildersMap.put(builder.getReference(), builder));
        return buildersMap;
    }

    private GhostThingConfiguration buildThingConfiguration(ThingBuilderContext builderContext) throws PluginException {
        try {
            return new ObjectMapper().readerFor(GhostThingConfiguration.class).readValue(builderContext.getThingDTO().getConfigurationNode());
        } catch(IOException e) {
            throw new PluginException("Could not parse Ghost thing's configuration!", e);
        }
    }

    private List<Actuator> buildActuators(ThingBuilderContext builderContext, GhostThingConfiguration thingConfiguration)
    throws PluginException {
        List<Actuator> actuators = new LinkedList<>();
        for (ActuatorDTO actuatorDTO : builderContext.getThingDTO().getActuators()) {
            actuators.add(buildActuator(actuatorDTO, builderContext.getMessagePublisher(), thingConfiguration));
        }
        return actuators;
    }

    private Actuator buildActuator(ActuatorDTO actuatorDTO, MessagePublisher messagePublisher, GhostThingConfiguration thingConfiguration)
    throws PluginException {
        _GhostActuatorBuilder actuatorBuilder = actuatorBuilderMap.get(actuatorDTO.getRef());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorDTO.getRef() + ", skipping this DTO");
        }
        return actuatorBuilder.buildActuator(actuatorDTO, messagePublisher, thingConfiguration);
    }
}
