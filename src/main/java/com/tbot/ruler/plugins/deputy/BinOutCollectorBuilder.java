package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.rest.RestPatchCommand;
import com.tbot.ruler.things.BasicCollector;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.service.ServiceProvider;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;

public class BinOutCollectorBuilder {

    private static final String PARAM_PIN = "pin";
    private static final String PATH_BINOUT_TEMPLATE = "binary-outputs/%s";

    public Collector buildCollector(ThingDTO thingDTO, CollectorDTO collectorDTO, ServiceProvider serviceProvider) {
        return BasicCollector.builder()
            .id(collectorDTO.getId())
            .name(collectorDTO.getName())
            .description(collectorDTO.getDescription())
            .messageCollectorConsumer(BinOutCollectorConsumer.builder()
                .patchCommand(buildRestPatchCommand(thingDTO, collectorDTO, serviceProvider))
                .build())
            .build();
    }

    private RestPatchCommand buildRestPatchCommand(ThingDTO thingDTO, CollectorDTO collectorDTO, ServiceProvider serviceProvider) {
        return serviceProvider.getRestService().builderForPatch()
                .host(thingDTO.getConfig().get(DeputyBuilder.PARAM_HOST))
                .port(thingDTO.getConfig().get(DeputyBuilder.PARAM_PORT))
                .path(buildBinOutPath(thingDTO, collectorDTO))
                .build();
    }

    private String buildBinOutPath(ThingDTO thingDTO, CollectorDTO collectorDTO) {
        String basePath = thingDTO.getConfig().getOrDefault(DeputyBuilder.PARAM_PATH, "").trim();

        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }

        return String.format(basePath + PATH_BINOUT_TEMPLATE, collectorDTO.getConfig().get(PARAM_PIN));
    }
}
