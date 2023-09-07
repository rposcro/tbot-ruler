package com.tbot.ruler.plugins.jwavez.switchbinary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.plugins.jwavez.CollectorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class SwitchBinaryCollectorBuilder implements CollectorBuilder {

    private final static String REFERENCE = "switch-binary";

    private final JWaveZThingContext thingContext;

    public SwitchBinaryCollectorBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public SwitchBinaryCollector buildCollector(CollectorDTO collectorDTO) throws PluginException {
        try {
            SwitchBinaryConfiguration configuration = new ObjectMapper().readerFor(SwitchBinaryConfiguration.class).readValue(collectorDTO.getConfigurationNode());

            return SwitchBinaryCollector.builder()
                    .id(collectorDTO.getId())
                    .name(collectorDTO.getName())
                    .description(collectorDTO.getDescription())
                    .commandSender(thingContext.getJwzCommandSender())
                    .configuration(configuration)
                    .applicationSupport(thingContext.getJwzApplicationSupport())
                    .build();
        } catch(IOException e) {
            throw new PluginException("Could not parse collector's configuration!", e);
        }
    }
}
