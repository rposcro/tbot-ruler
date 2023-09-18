package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.things.Actuator;
import java.util.Collections;
import java.util.List;

import com.tbot.ruler.things.Thing;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeputyThingBuilder {

    static final String PARAM_HOST = "host";
    static final String PARAM_PORT = "port";
    static final String PARAM_PATH = "path";

    private static final String ACTUATOR_REF_BINOUT = "binary-output";
    private static final String ACTUATOR_REF_HEALTHCHECK = "health-check";

    private HealthCheckActuatorBuilder healthCheckActuatorBuilder = new HealthCheckActuatorBuilder();
    private BinaryActuatorBuilder binOutActuatorBuilder = new BinaryActuatorBuilder();

    public Thing buildThing(PluginBuilderContext builderContext) {
        // TODO: rewrite the thing
        return null;
//        ThingDTO thingDTO = builderContext.getThingDTO();
//        log.debug("Building Deputy: " + thingDTO.getName());
//
//        List<Actuator> actuators = buildActuators(builderContext);
//
//        return BasicThing.builder()
//            .uuid(thingDTO.getUuid())
//            .name(thingDTO.getName())
//            .description(thingDTO.getDescription())
//            .actuators(actuators)
//            .build();
    }

    private List<Actuator> buildActuators(PluginBuilderContext builderContext) {
//        ThingDTO thingDTO = builderContext.getThingDTO();
//        List<ActuatorDTO> actuatorDTOs = thingDTO.getActuators();
//
//        if (actuatorDTOs != null) {
//            List<Actuator> actuators = new ArrayList<>(actuatorDTOs.size());
//            actuatorDTOs.forEach(actuatorDTO -> {
//                if (ACTUATOR_REF_BINOUT.equals(actuatorDTO.getRef())) {
//                    actuators.add(binOutActuatorBuilder.buildActuator(thingDTO, actuatorDTO, builderContext));
//                } else if (ACTUATOR_REF_HEALTHCHECK.equals(actuatorDTO.getRef())) {
//                    actuators.add(healthCheckActuatorBuilder.buildActuator(actuatorDTO, builderContext));
//                }
//            });
//            return actuators;
//        }
        return Collections.emptyList();
    }
}
