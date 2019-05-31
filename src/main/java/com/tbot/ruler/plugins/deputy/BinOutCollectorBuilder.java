package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.rest.RestPatchCommand;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.CollectorMetadata;
import com.tbot.ruler.things.service.ServiceProvider;
import com.tbot.ruler.things.dto.CollectorDTO;
import com.tbot.ruler.things.dto.ThingDTO;
import com.tbot.ruler.signals.SignalValueType;

class BinOutCollectorBuilder {

    private static final String PARAM_PIN = "pin";
    private static final String PATH_BINOUT_TEMPLATE = "binary-outputs/%s";

    Collector buildCollector(ThingDTO thingDTO, CollectorDTO collectorDTO, ServiceProvider serviceProvider) {
        CollectorMetadata metadata = buildMetadata(collectorDTO);
        RestPatchCommand command = buildRestPatchCommand(thingDTO, collectorDTO, serviceProvider);
        return BinOutCollector.builder()
                .metadata(metadata)
                .patchCommand(command)
                .build();
    }

    private CollectorMetadata buildMetadata(CollectorDTO collectorDTO) {
        CollectorMetadata metadata = CollectorMetadata.fromCollectorDTO(collectorDTO);
        metadata.setCollectedSignalType(SignalValueType.OnOff);
        return metadata;
    }

    private RestPatchCommand buildRestPatchCommand(ThingDTO thingDTO, CollectorDTO collectorDTO, ServiceProvider serviceProvider) {
        return serviceProvider.getRestService().builderForPatch()
                .host(thingDTO.getConfig().get(DeputyBuilder.PARAM_HOST))
                .port(thingDTO.getConfig().get(DeputyBuilder.PARAM_PORT))
                .path(buildBinOutPath(thingDTO, collectorDTO))
                .build();
    }

    private String buildBinOutPath(ThingDTO thingDTO, CollectorDTO collectorDTO) {
        String basePath = thingDTO.getConfig().get(DeputyBuilder.PARAM_PATH);
        basePath = (basePath == null) ? "" : basePath.trim();

        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }

        return String.format(basePath + PATH_BINOUT_TEMPLATE, collectorDTO.getConfig().get(PARAM_PIN));
    }
}
