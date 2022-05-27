package com.tbot.ruler.plugins.jwavez.basicset;

import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;

public class BasicSetActuatorBuilder {

    private static final String BASIC_PARAM_SOURCE_NODE = "sourceNodeId";
    private static final String BASIC_PARAM_SOURCE_ENDPOINT = "sourceEndPointId";
    private static final String BASIC_PARAM_VALUE_MODE = "valueMode";
    private static final String BASIC_PARAM_TURN_ON_VALUE = "turnOnValue";
    private static final String BASIC_PARAM_TURN_OFF_VALUE = "turnOffValue";

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
