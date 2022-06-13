package com.tbot.ruler.plugins.jwavez.basicset;

import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZAgent;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;

public class BasicSetActuatorBuilder implements ActuatorBuilder {

    private static final String REFERENCE = "basic-set";
    private static final String BASIC_PARAM_SOURCE_NODE = "sourceNodeId";
    private static final String BASIC_PARAM_SOURCE_ENDPOINT = "sourceEndPointId";
    private static final String BASIC_PARAM_VALUE_MODE = "valueMode";
    private static final String BASIC_PARAM_TURN_ON_VALUE = "turnOnValue";
    private static final String BASIC_PARAM_TURN_OFF_VALUE = "turnOffValue";

    private BasicSetHandler basicSetHandler = new BasicSetHandler();

    @Override
    public CommandType getSupportedCommandType() {
        return BasicCommandType.BASIC_SET;
    }

    @Override
    public SupportedCommandHandler<BasicSet> getSupportedCommandHandler() {
        return basicSetHandler;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Actuator buildActuator(JWaveZAgent agent, ThingBuilderContext builderContext, ActuatorDTO actuatorDTO) {
        BasicSetActuator actuator = BasicSetActuator.builder()
            .id(actuatorDTO.getId())
            .name(actuatorDTO.getName())
            .description(actuatorDTO.getDescription())
            .messageConsumer(messageConsumer())
            .emissionProducer(emissionProducer(actuatorDTO, builderContext))
            .build();
        basicSetHandler.registerEmissionProducer(actuator.getEmissionProducer());
        return actuator;
    }

    private BasicSetMessageConsumer messageConsumer() {
        return new BasicSetMessageConsumer();
    }

    private BasicSetEmissionProducer emissionProducer(ActuatorDTO actuatorDTO, ThingBuilderContext builderContext) {
        return BasicSetEmissionProducer.builder()
            .actuatorId(actuatorDTO.getId())
            .messagePublisher(builderContext.getMessagePublisher())
            .sourceNodeId((byte) actuatorDTO.getIntParameter(BASIC_PARAM_SOURCE_NODE))
            .sourceEndPointId((byte) actuatorDTO.getIntParameter(BASIC_PARAM_SOURCE_ENDPOINT, 0xff))
            .valueMode(valueMode(actuatorDTO))
            .turnOnValue((byte) actuatorDTO.getIntParameter(BASIC_PARAM_TURN_ON_VALUE, 255))
            .turnOffValue((byte) actuatorDTO.getIntParameter(BASIC_PARAM_TURN_OFF_VALUE, 0))
            .build();
    }

    private BasicSetValueMode valueMode(ActuatorDTO actuatorDTO) {
        return BasicSetValueMode.of(actuatorDTO.getStringParameter(BASIC_PARAM_VALUE_MODE, "toggle"));
    }
}
