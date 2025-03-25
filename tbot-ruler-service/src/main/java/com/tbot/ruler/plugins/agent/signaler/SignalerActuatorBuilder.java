package com.tbot.ruler.plugins.agent.signaler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.broker.payload.BinaryClaim;
import com.tbot.ruler.broker.payload.BinaryState;
import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.agent.AgentActuatorBuilder;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import java.io.IOException;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class SignalerActuatorBuilder extends AgentActuatorBuilder {

    private static final String REFERENCE = "signaler";

    public SignalerActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext thingContext) {
        SignalerActuatorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SignalerActuatorConfiguration.class);
        Object signalPayload = parseSignalPayload(configuration);
        return SignalerActuator.builder()
                .actuatorEntity(actuatorEntity)
                .signalPayload(signalPayload)
                .thingContext(thingContext)
                .build();
    }

    private Object parseSignalPayload(SignalerActuatorConfiguration configuration) {
        try {
            return switch (configuration.getSignalType()) {
                case "BinaryClaim" -> parseValue(BinaryClaim.class, configuration.getSignalValue());
                case "OnOffState" -> parseValue(BinaryClaim.class, configuration.getSignalValue());
                case "RgbwColor" -> parseValue(RGBWColor.class, configuration.getSignalValue());
                default -> throw new PluginException("Unsupported signal type " + configuration.getSignalType());
            };
        } catch(IOException e) {
            throw new PluginException("Failed to parse signal value!", e);
        }
    }

    private Object parseValue(Class<?> valueClass, JsonNode valueNode) throws IOException {
        return new ObjectMapper().readerFor(valueClass).readValue(valueNode);
    }
}
