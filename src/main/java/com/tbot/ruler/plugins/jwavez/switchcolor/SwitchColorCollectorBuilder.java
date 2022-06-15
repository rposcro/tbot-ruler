package com.tbot.ruler.plugins.jwavez.switchcolor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.plugins.jwavez.CollectorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZAgent;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class SwitchColorCollectorBuilder implements CollectorBuilder {

    private static final String REFERENCE = "switch-color";

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Collector buildCollector(JWaveZAgent agent, ThingBuilderContext builderContext, CollectorDTO collectorDTO) throws PluginException {
        try {
            SwitchColorCollectorConfiguration configuration = new ObjectMapper().readerFor(SwitchColorCollectorConfiguration.class)
                    .readValue(collectorDTO.getConfigurationNode());
            return SwitchColorCollector.builder()
                    .id(collectorDTO.getId())
                    .name(collectorDTO.getName())
                    .description(collectorDTO.getDescription())
                    .commandSender(agent.getCommandSender())
                    .configuration(configuration)
                    .build();
        } catch (IOException e) {
            throw new PluginException("Could not parse collector's configuration!", e);
        }
    }
}
