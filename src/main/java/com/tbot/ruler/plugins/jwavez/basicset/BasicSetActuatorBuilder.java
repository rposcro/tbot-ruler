package com.tbot.ruler.plugins.jwavez.basicset;

import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;

public class BasicSetActuatorBuilder {

    private static final String BASIC_PARAM_SOURCE_NODE = "source-node-id";
    private static final String BASIC_PARAM_VALUE_MODE = "value-mode";
    private static final String BASIC_PARAM_TURN_ON_VALUE = "turn-on-value";
    private static final String BASIC_PARAM_TURN_OFF_VALUE = "turn-off-value";

    public BasicSetActuator buildActuator(BasicSetHandler handler, ThingBuilderContext builderContext, ActuatorDTO actuatorDTO) {
        BasicSetActuator actuator = BasicSetActuator.builder()
            .id(actuatorDTO.getId())
            .name(actuatorDTO.getName())
            .description(actuatorDTO.getDescription())
            .messageConsumer(messageConsumer())
            .emissionProducer(emissionProducer(actuatorDTO, builderContext))
            .build();
        handler.registerEmissionProducer(actuator.getEmissionProducer());
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
            .valueMode(valueMode(actuatorDTO))
            .turnOnValue((byte) actuatorDTO.getIntParameter(BASIC_PARAM_TURN_ON_VALUE, 255))
            .turnOffValue((byte) actuatorDTO.getIntParameter(BASIC_PARAM_TURN_OFF_VALUE, 0))
            .build();
    }

    private BasicSetValueMode valueMode(ActuatorDTO actuatorDTO) {
        return BasicSetValueMode.of(actuatorDTO.getStringParameter(BASIC_PARAM_VALUE_MODE, "toggle"));
    }
}
