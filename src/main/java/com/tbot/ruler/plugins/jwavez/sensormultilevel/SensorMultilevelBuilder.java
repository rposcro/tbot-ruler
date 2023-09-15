package com.tbot.ruler.plugins.jwavez.sensormultilevel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class SensorMultilevelBuilder implements ActuatorBuilder {

    private static final String REFERENCE = "sensor-multilevel";

    private JWaveZThingContext thingContext;
    private final SensorMultilevelListener sensorMultilevelHandler;

    public SensorMultilevelBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
        this.sensorMultilevelHandler = new SensorMultilevelListener(
                thingContext.getJwzApplicationSupport().supportedCommandParser());
    }

    @Override
    public CommandType getSupportedCommandType() {
        return SensorMultilevelCommandType.SENSOR_MULTILEVEL_REPORT;
    }

    @Override
    public JWaveZCommandListener<SensorMultilevelReport> getSupportedCommandHandler() {
        return sensorMultilevelHandler;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public SensorMultilevelActuator buildActuator(ActuatorDTO actuatorDTO)
    throws PluginException {
        try {
            SensorMultilevelConfiguration configuration = new ObjectMapper()
                    .readerFor(SensorMultilevelConfiguration.class)
                    .readValue(actuatorDTO.getConfigurationNode());
            SensorMultilevelActuator emitter = SensorMultilevelActuator.builder()
                    .uuid(actuatorDTO.getUuid())
                    .name(actuatorDTO.getName())
                    .description(actuatorDTO.getDescription())
                    .messagePublisher(thingContext.getMessagePublisher())
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
            throw new PluginException("Could not parse actuator's configuration!", e);
        }
    }
}
