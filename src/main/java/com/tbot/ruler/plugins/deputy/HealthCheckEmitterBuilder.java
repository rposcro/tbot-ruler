package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.things.EmissionThread;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.EmitterMetadata;
import com.tbot.ruler.things.RegularEmissionTrigger;
import com.tbot.ruler.things.ThingBuilderContext;
import com.tbot.ruler.things.dto.EmitterDTO;
import com.tbot.ruler.things.dto.ThingDTO;
import com.tbot.ruler.signals.SignalValueType;

class HealthCheckEmitterBuilder {

    private static final String PARAM_FREQUENCY = "frequency";
    private static final String DEFAULT_FREQUENCY = "15";

    HealthCheckEmitter buildEmitter(ThingBuilderContext builderContext, EmitterDTO emitterDTO) {
        HealthCheckEmitter emitter = HealthCheckEmitter.builder()
            .id(emitterDTO.getId())
            .metadata(emitterMetadata(emitterDTO))
            .build();
        registerEmitterThread(builderContext, emitterDTO);
        return emitter;
    }

    private void registerEmitterThread(ThingBuilderContext builderContext, EmitterDTO emitterDTO) {
        builderContext.getServices().getRegistrationService().registerPeriodicEmissionThread(
                emissionThread(builderContext, emitterDTO.getId()),
                emissionTrigger(emitterDTO)
        );
    }

    private EmissionThread emissionThread(ThingBuilderContext builderContext, EmitterId emitterId) {
        return HealthCheckEmissionThread.builder()
                .emitterId(emitterId)
                .healthCheckCommand(restGetCommand(builderContext))
                .signalConsumer(builderContext.getSignalConsumer())
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

    private EmitterMetadata emitterMetadata(EmitterDTO emitterDTO) {
        return EmitterMetadata.builder()
                .id(emitterDTO.getId())
                .name(emitterDTO.getName())
                .description(emitterDTO.getName())
                .emittedSignalType(SignalValueType.Boolean)
                .build();
    }
}
