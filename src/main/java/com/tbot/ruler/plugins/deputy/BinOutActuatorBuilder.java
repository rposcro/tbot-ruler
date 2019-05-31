package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.rest.RestPatchCommand;
import com.tbot.ruler.signals.SignalValueType;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.ActuatorMetadata;
import com.tbot.ruler.things.dto.ActuatorDTO;
import com.tbot.ruler.things.dto.ThingDTO;
import com.tbot.ruler.things.service.ServiceProvider;

class BinOutActuatorBuilder {

    private static final String PARAM_PIN = "pin";
    private static final String PATH_BINOUT_TEMPLATE = "binary-outputs/%s";

    Actuator buildActuator(ThingDTO thingDTO, ActuatorDTO actuatorDTO, ServiceProvider serviceProvider) {
        ActuatorMetadata metadata = buildMetadata(actuatorDTO);
        RestPatchCommand command = buildRestPatchCommand(thingDTO, actuatorDTO, serviceProvider);
        return BinOutActuator.builder()
                .metadata(metadata)
                .patchCommand(command)
                .build();
    }

    private ActuatorMetadata buildMetadata(ActuatorDTO actuatorDTO) {
        ActuatorMetadata metadata = ActuatorMetadata.fromActuatorDTO(actuatorDTO);
        metadata.setHandledSignalType(SignalValueType.OnOff);
        return metadata;
    }

    private RestPatchCommand buildRestPatchCommand(ThingDTO thingDTO, ActuatorDTO actuatorDTO, ServiceProvider serviceProvider) {
        return serviceProvider.getRestService().builderForPatch()
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
