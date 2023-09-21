package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.rest.RestPatchCommand;
import com.tbot.ruler.rest.RestService;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.persistance.json.dto.ThingDTO;

public class BinaryActuatorBuilder {

    private static final String PARAM_PIN = "pin";
    private static final String PATH_BINOUT_TEMPLATE = "binary-outputs/%s";

    public Actuator buildActuator(ThingDTO thingDTO, ActuatorDTO actuatorDTO, PluginBuilderContext builderContext) {
        return null;
//        return BinaryActuator.builder()
//            .id(actuatorDTO)
//            .binaryChannel(binChannel(thingDTO, actuatorDTO, builderContext))
//            .messagePublisher(builderContext.getMessagePublisher())
//            .build();
    }

    private BinaryActuatorChannel binChannel(ThingDTO thingDTO, ActuatorDTO actuatorDTO, PluginBuilderContext builderContext) {
        return BinaryActuatorChannel.builder()
            .patchCommand(restPatchCommand(thingDTO, actuatorDTO, builderContext.getServiceProvider().getRestService()))
            .getCommand(restGetCommand(thingDTO, actuatorDTO, builderContext.getServiceProvider().getRestService()))
            .build();
    }

    private RestPatchCommand restPatchCommand(ThingDTO thingDTO, ActuatorDTO actuatorDTO, RestService restService) {
        return restService.builderForPatch()
                .host(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_HOST))
                .port(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_PORT))
                .path(buildBinOutPath(thingDTO, actuatorDTO))
                .build();
    }

    private RestGetCommand restGetCommand(ThingDTO thingDTO, ActuatorDTO actuatorDTO, RestService restService) {
        return restService.builderForGet()
                .host(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_HOST))
                .port(thingDTO.getStringParameter(DeputyThingBuilder.PARAM_PORT))
                .path(buildBinOutPath(thingDTO, actuatorDTO))
                .build();
    }

    private String buildBinOutPath(ThingDTO thingDTO, ActuatorDTO actuatorDTO) {
        String basePath = thingDTO.getStringParameter(DeputyThingBuilder.PARAM_PATH);
        basePath = (basePath == null) ? "" : basePath.trim();

        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }

        return String.format(basePath + PATH_BINOUT_TEMPLATE, actuatorDTO.getStringParameter(PARAM_PIN));
    }
}
