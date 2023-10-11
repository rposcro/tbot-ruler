package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.BasicActuator;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.task.RegularEmissionTrigger;
import com.tbot.ruler.task.Task;

class HealthCheckActuatorBuilder {

    private static final String PARAM_FREQUENCY = "frequency";
    private static final String DEFAULT_FREQUENCY = "15";

    Actuator buildActuator(ActuatorDTO actuatorDTO, PluginBuilderContext builderContext) {
        return BasicActuator.builder()
                .uuid(actuatorDTO.getUuid())
                .name(actuatorDTO.getName())
                .description(actuatorDTO.getDescription())
                .asynchronousTask(Task.triggerableTask(
                        emissionTask(builderContext, actuatorDTO),
                        emissionTrigger(actuatorDTO)))
                .build();
    }

    private HealthCheckEmissionTask emissionTask(PluginBuilderContext builderContext, ActuatorDTO actuatorDTO) {
        return HealthCheckEmissionTask.builder()
            .actuatorId(actuatorDTO.getUuid())
            .healthCheckCommand(restGetCommand(builderContext))
            .messagePublisher(builderContext.getMessagePublisher())
            .build();
    }

    private RegularEmissionTrigger emissionTrigger(ActuatorDTO actuatorDTO) {
        return new RegularEmissionTrigger(
                1000 * Long.parseLong(actuatorDTO.getStringParameter(PARAM_FREQUENCY, DEFAULT_FREQUENCY)));
    }

    private RestGetCommand restGetCommand(PluginBuilderContext builderContext) {
        return null;
//        ThingDTO thingDTO = builderContext.getThingDTO();
//        return builderContext.getServices().getRestService().builderForGet()
//                .host(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_HOST))
//                .port(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_PORT))
//                .path(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_PATH))
//                .build();
    }
}
