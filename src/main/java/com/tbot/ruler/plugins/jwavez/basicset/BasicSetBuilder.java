package com.tbot.ruler.plugins.jwavez.basicset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

import java.io.IOException;

public class BasicSetBuilder implements ActuatorBuilder {

    private static final String REFERENCE = "basic-set";

    private final JWaveZThingContext thingContext;
    private final BasicSetCommandListener basicSetCommandHandler;

    public BasicSetBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
        basicSetCommandHandler = new BasicSetCommandListener(thingContext.getJwzApplicationSupport().supportedCommandParser());
    }

    @Override
    public CommandType getSupportedCommandType() {
        return BasicCommandType.BASIC_SET;
    }

    @Override
    public JWaveZCommandListener<BasicSet> getSupportedCommandHandler() {
        return basicSetCommandHandler;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Actuator buildActuator(ActuatorDTO actuatorDTO) throws PluginException {
        try {
            BasicSetConfiguration configuration = new ObjectMapper().readerFor(BasicSetConfiguration.class).readValue(actuatorDTO.getConfigurationNode());
            BasicSetActuator emitter = BasicSetActuator.builder()
                    .id(actuatorDTO.getUuid())
                    .name(actuatorDTO.getName())
                    .description(actuatorDTO.getDescription())
                    .configuration(configuration)
                    .messagePublisher(thingContext.getMessagePublisher())
                    .build();
            basicSetCommandHandler.registerEmitter(emitter);
            return emitter;
        } catch (IOException e) {
            throw new PluginException("Could not parse actuator's configuration!", e);
        }
    }
}
