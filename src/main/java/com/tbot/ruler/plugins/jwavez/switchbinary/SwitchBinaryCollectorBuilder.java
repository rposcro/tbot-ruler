package com.tbot.ruler.plugins.jwavez.switchbinary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.plugins.jwavez.CollectorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZAgent;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class SwitchBinaryCollectorBuilder implements CollectorBuilder {

    private final static String REFERENCE = "switch-binary";

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public SwitchBinaryCollector buildCollector(JWaveZAgent agent, ThingBuilderContext builderContext, CollectorDTO collectorDTO) throws PluginException {
        try {
            SwitchBinaryConfiguration configuration = new ObjectMapper().readerFor(SwitchBinaryConfiguration.class).readValue(collectorDTO.getConfigurationNode());

            return SwitchBinaryCollector.builder()
                    .id(collectorDTO.getId())
                    .name(collectorDTO.getName())
                    .description(collectorDTO.getDescription())
                    .commandSender(agent.getCommandSender())
                    .configuration(configuration)
                    .build();
        } catch(IOException e) {
            throw new PluginException("Could not parse collector's configuration!", e);
        }
    }
}
