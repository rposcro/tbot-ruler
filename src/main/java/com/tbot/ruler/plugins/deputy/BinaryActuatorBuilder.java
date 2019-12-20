package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.rest.RestPatchCommand;
import com.tbot.ruler.rest.RestService;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicActuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;

import java.util.function.Consumer;

public class BinaryActuatorBuilder {

    private static final String PARAM_PIN = "pin";
    private static final String PATH_BINOUT_TEMPLATE = "binary-outputs/%s";

    public Actuator buildActuator(ThingDTO thingDTO, ActuatorDTO actuatorDTO, ThingBuilderContext builderContext) {
        return BasicActuator.builder()
            .id(actuatorDTO.getId())
            .name(actuatorDTO.getName())
            .description(actuatorDTO.getDescription())
            .messageCollectorConsumer(binOutActuatorCollector(thingDTO, actuatorDTO, builderContext))
            .build();
    }

    private Consumer<Message> binOutActuatorCollector(ThingDTO thingDTO, ActuatorDTO actuatorDTO, ThingBuilderContext builderContext) {
        return BinaryActuatorConsumer.builder()
            .actuatorId(actuatorDTO.getId())
            .patchCommand(restPatchCommand(thingDTO, actuatorDTO, builderContext.getServices().getRestService()))
            .getCommand(restGetCommand(thingDTO, actuatorDTO, builderContext.getServices().getRestService()))
            .messagePublisher(builderContext.getMessagePublisher())
            .build();

    }

    private RestPatchCommand restPatchCommand(ThingDTO thingDTO, ActuatorDTO actuatorDTO, RestService restService) {
        return restService.builderForPatch()
                .host(thingDTO.getConfig().get(DeputyBuilder.PARAM_HOST))
                .port(thingDTO.getConfig().get(DeputyBuilder.PARAM_PORT))
                .path(buildBinOutPath(thingDTO, actuatorDTO))
                .build();
    }

    private RestGetCommand restGetCommand(ThingDTO thingDTO, ActuatorDTO actuatorDTO, RestService restService) {
        return restService.builderForGet()
                .host(thingDTO.getConfig().get(DeputyBuilder.PARAM_HOST))
                .port(thingDTO.getConfig().get(DeputyBuilder.PARAM_PORT))
                .path(buildBinOutPath(thingDTO, actuatorDTO))
                .build();
    }

    private String buildBinOutPath(ThingDTO thingDTO, ActuatorDTO actuatorDTO) {
        String basePath = thingDTO.getConfig().get(DeputyBuilder.PARAM_PATH);
        basePath = (basePath == null) ? "" : basePath.trim();

        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }

        return String.format(basePath + PATH_BINOUT_TEMPLATE, actuatorDTO.getConfig().get(PARAM_PIN));
    }
}
