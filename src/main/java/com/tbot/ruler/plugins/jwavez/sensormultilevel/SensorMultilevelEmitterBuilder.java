package com.tbot.ruler.plugins.jwavez.sensormultilevel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.tbot.ruler.plugins.jwavez.EmitterBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZAgent;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandHandler;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class SensorMultilevelEmitterBuilder implements EmitterBuilder {

    private static final String REFERENCE = "sensor-multilevel";

    private SensorMultilevelHandler sensorMultilevelHandler = new SensorMultilevelHandler();

    @Override
    public CommandType getSupportedCommandType() {
        return SensorMultilevelCommandType.SENSOR_MULTILEVEL_REPORT;
    }

    @Override
    public JWaveZCommandHandler<SensorMultilevelReport> getSupportedCommandHandler() {
        return sensorMultilevelHandler;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public SensorMultilevelEmitter buildEmitter(JWaveZAgent jWaveZAgent, ThingBuilderContext context, EmitterDTO emitterDTO)
    throws PluginException {
        try {
            SensorMultilevelConfiguration configuration = new ObjectMapper()
                    .readerFor(SensorMultilevelConfiguration.class)
                    .readValue(emitterDTO.getConfigurationNode());
            SensorMultilevelEmitter emitter = SensorMultilevelEmitter.builder()
                    .id(emitterDTO.getId())
                    .name(emitterDTO.getName())
                    .description(emitterDTO.getDescription())
                    .messagePublisher(context.getMessagePublisher())
                    .build();

            if (configuration.isMultiChannelOn()) {
                sensorMultilevelHandler.registerEmitter(
                        (byte) configuration.getSourceNodeId(),
                        (byte) configuration.getSourceEndPointId(),
                        emitter
                );
            } else {
                sensorMultilevelHandler.registerEmitter(
                        (byte) configuration.getSourceNodeId(),
                        emitter
                );
            }

            return emitter;
        } catch (IOException e) {
            throw new PluginException("Could not parse emitter's configuration!", e);
        }
    }
}
