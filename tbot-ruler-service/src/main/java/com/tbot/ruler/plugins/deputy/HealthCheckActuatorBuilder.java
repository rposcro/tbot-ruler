package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.BasicActuator;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import com.tbot.ruler.task.RegularEmissionTrigger;
import com.tbot.ruler.task.SubjectTask;

class HealthCheckActuatorBuilder {

    private static final String PARAM_FREQUENCY = "frequency";
    private static final String DEFAULT_FREQUENCY = "15";

    Actuator buildActuator(ActuatorDTO actuatorDTO, RulerThingContext thingContext) {
        return BasicActuator.builder()
                .uuid(actuatorDTO.getUuid())
                .name(actuatorDTO.getName())
                .description(actuatorDTO.getDescription())
                .asynchronousSubjectTask(SubjectTask.triggerableTask(
                        emissionTask(thingContext, actuatorDTO),
                        emissionTrigger(actuatorDTO)))
                .build();
    }

    private HealthCheckEmissionTask emissionTask(RulerThingContext thingContext, ActuatorDTO actuatorDTO) {
        return HealthCheckEmissionTask.builder()
            .actuatorId(actuatorDTO.getUuid())
            .healthCheckCommand(null)
            .messagePublisher(thingContext.getMessagePublisher())
            .build();
    }

    private RegularEmissionTrigger emissionTrigger(ActuatorDTO actuatorDTO) {
        return new RegularEmissionTrigger(
                1000 * Long.parseLong(actuatorDTO.getStringParameter(PARAM_FREQUENCY, DEFAULT_FREQUENCY)));
    }

    private RestGetCommand restGetCommand(RulerPluginContext builderContext) {
        return null;
//        ThingDTO thingDTO = builderContext.getThingDTO();
//        return builderContext.getServices().getRestService().builderForGet()
//                .host(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_HOST))
//                .port(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_PORT))
//                .path(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_PATH))
//                .build();
    }
}
