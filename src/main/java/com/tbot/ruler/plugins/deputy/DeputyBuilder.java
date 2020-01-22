package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.ThingPluginBuilder;
import com.tbot.ruler.things.builder.dto.EmitterDTO;

import com.tbot.ruler.things.builder.dto.ThingDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeputyBuilder implements ThingPluginBuilder {

    static final String PARAM_HOST = "host";
    static final String PARAM_PORT = "port";
    static final String PARAM_PATH = "path";

    private static final String ACTUATOR_REF_BINOUT = "binary-output";
    private static final String EMITTER_REF_HEALTHCHECK = "health-check";

    private HealthCheckEmitterBuilder healthCheckEmitterBuilder = new HealthCheckEmitterBuilder();
    private BinaryActuatorBuilder binOutActuatorBuilder = new BinaryActuatorBuilder();

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) {
        ThingDTO thingDTO = builderContext.getThingDTO();
        log.debug("Building Deputy: " + thingDTO.getName());

        List<Emitter> emitters = buildEmitters(builderContext);
        List<Actuator> actuators = buildActuators(builderContext);

        return BasicThing.builder()
            .id(thingDTO.getId())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .emitters(emitters)
            .actuators(actuators)
            .build();
    }

    private List<Emitter> buildEmitters(ThingBuilderContext builderContext) {
        ThingDTO thingDTO = builderContext.getThingDTO();
        List<EmitterDTO> emitterDTOs = thingDTO.getEmitters();

        if (emitterDTOs != null) {
            List<Emitter> emitters = new ArrayList<>(emitterDTOs.size());
            emitterDTOs.forEach(emitterDTO -> {
                if (EMITTER_REF_HEALTHCHECK.equals(emitterDTO.getRef())) {
                    emitters.add(healthCheckEmitterBuilder.buildEmitter(builderContext, emitterDTO));
                }
            });
            return emitters;
        }
        return Collections.emptyList();
    }
    
    private List<Actuator> buildActuators(ThingBuilderContext builderContext) {
        ThingDTO thingDTO = builderContext.getThingDTO();
        List<ActuatorDTO> actuatorDTOs = thingDTO.getActuators();

        if (actuatorDTOs != null) {
            List<Actuator> actuators = new ArrayList<>(actuatorDTOs.size());
            actuatorDTOs.forEach(actuatorDTO -> {
                if (ACTUATOR_REF_BINOUT.equals(actuatorDTO.getRef())) {
                    actuators.add(binOutActuatorBuilder.buildActuator(thingDTO, actuatorDTO, builderContext));
                }
            });
            return actuators;
        }
        return Collections.emptyList();
    }
}
