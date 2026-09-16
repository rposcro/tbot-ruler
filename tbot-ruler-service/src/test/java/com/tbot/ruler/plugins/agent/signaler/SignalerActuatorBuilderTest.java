package com.tbot.ruler.plugins.agent.signaler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryClaim;
import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.broker.payload.Trigger;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingAgent;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SignalerActuatorBuilderTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MessagePublisher messagePublisher;

    private RulerThingContext rulerThingContext;
    private SignalerActuatorBuilder signalerActuatorBuilder;

    @BeforeEach
    public void setUp() {
        rulerThingContext = RulerThingContext.builder()
            .thingUuid("thing-uuid")
            .thingName("thing-name")
            .messagePublisher(messagePublisher)
            .rulerThingAgent(new RulerThingAgent())
            .subjectStateService(new SubjectStateService())
            .build();
        signalerActuatorBuilder = new SignalerActuatorBuilder();
    }

    @ParameterizedTest
    @MethodSource("payloadTypeArguments")
    public void testMessagePayloadType(String payloadType, String payloadValue, Class<?> expectedPayloadType)
    throws Exception {
        ActuatorEntity entity = mockActuatorEntity(payloadType, payloadValue);
        Actuator actuator = signalerActuatorBuilder.buildActuator(entity, rulerThingContext);

        actuator.acceptMessage(Message.builder().senderId("sender-id").payload(Trigger.TRIGGER).build());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(messagePublisher, Mockito.times(1)).publishMessage(messageCaptor.capture());

        assertEquals(expectedPayloadType, messageCaptor.getValue().getPayload().getClass());
    }

    private ActuatorEntity mockActuatorEntity(String payloadType, String payloadValue) throws Exception {
        String configurationJson = String.format("{ \"signalType\": \"%s\", \"signalValue\": %s }", payloadType, payloadValue);

        return ActuatorEntity.builder()
            .actuatorUuid("actuator-id")
            .name("actuator-name")
            .configuration(objectMapper.readTree(configurationJson))
            .build();
    }

    public static Stream<Arguments> payloadTypeArguments() {
        return Stream.of(
                Arguments.of("BinaryClaim", "{ \"on\": true }", BinaryClaim.class),
                Arguments.of("OnOffState", "{ \"on\": false }", BinaryClaim.class),
                Arguments.of("RgbwColor", "{ \"red\": 0, \"green\": 0, \"blue\": 0, \"white\": 0 }", RGBWColor.class)
        );
    }
}
