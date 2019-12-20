package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.things.BasicEmitter;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.thread.RegularEmissionTrigger;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;

class HealthCheckEmitterBuilder {

    private static final String PARAM_FREQUENCY = "frequency";
    private static final String DEFAULT_FREQUENCY = "15";

    Emitter buildEmitter(ThingBuilderContext builderContext, EmitterDTO emitterDTO) {
        return BasicEmitter.builder()
            .id(emitterDTO.getId())
            .name(emitterDTO.getName())
            .description(emitterDTO.getDescription())
            .emissionTask(emissionTask(builderContext, emitterDTO))
            .emissionTrigger(emissionTrigger(emitterDTO))
            .build();
    }

    private HealthCheckEmissionTask emissionTask(ThingBuilderContext builderContext, EmitterDTO emitterDTO) {
        return HealthCheckEmissionTask.builder()
            .emitterId(emitterDTO.getId())
            .healthCheckCommand(restGetCommand(builderContext))
            .messageConsumer(builderContext.getMessagePublisher())
            .build();
    }

    private RegularEmissionTrigger emissionTrigger(EmitterDTO emitterDTO) {
        return new RegularEmissionTrigger(
                1000 * Long.parseLong(emitterDTO.getConfig().getOrDefault(PARAM_FREQUENCY, DEFAULT_FREQUENCY)));
    }

    private RestGetCommand restGetCommand(ThingBuilderContext builderContext) {
        ThingDTO thingDTO = builderContext.getThingDTO();
        return builderContext.getServices().getRestService().builderForGet()
                .host(thingDTO.getConfig().get(DeputyBuilder.PARAM_HOST))
                .port(thingDTO.getConfig().get(DeputyBuilder.PARAM_PORT))
                .path(thingDTO.getConfig().get(DeputyBuilder.PARAM_PATH))
                .build();
    }
}
