package com.tbot.ruler.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.tbot.ruler.broker.SynchronousMessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.controller.subject.payload.ActuatorStateUpdateRequest;
import com.tbot.ruler.controller.subject.ActuatorsStateController;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.service.things.ActuatorsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ActuatorsStateControllerTest {

    @Mock
    private ActuatorsService actuatorsService;

    @Mock
    private SynchronousMessagePublisher messagePublisher;

    private ActuatorsStateController controller;

    @BeforeEach
    public void setUp() {
        this.controller = new ActuatorsStateController(actuatorsService, messagePublisher, new ObjectMapper());
    }

    @Test
    public void sendsObjectMessage() {
        final JsonNode payload = new TextNode("a payload");
        final String actuatorUuid = "actuator-uuid";
        final ActuatorStateUpdateRequest request = mockMessageRequest("Object", payload);
        final ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        controller.updateActuatorState(actuatorUuid, request);

        verify(messagePublisher, times(1)).publishAndWaitForReport(messageCaptor.capture());
        assertEquals(request.getWidgetUuid(), messageCaptor.getValue().getSenderId());
        assertEquals(actuatorUuid, messageCaptor.getValue().getReceiverId());
        assertEquals(payload, messageCaptor.getValue().getPayload());
    }

    @Test
    public void sendsOnOffMessage() {
        final JsonNode payload = new ObjectMapper().createObjectNode().put("on", "true");
        final String actuatorUuid = "actuator-uuid";
        final ActuatorStateUpdateRequest request = mockMessageRequest("OnOff", payload);
        final ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        controller.updateActuatorState(actuatorUuid, request);

        verify(messagePublisher, times(1)).publishAndWaitForReport(messageCaptor.capture());
        assertEquals(request.getWidgetUuid(), messageCaptor.getValue().getSenderId());
        assertEquals(actuatorUuid, messageCaptor.getValue().getReceiverId());
        assertTrue(messageCaptor.getValue().isPayloadAs(OnOffState.class));
        assertTrue(messageCaptor.getValue().getPayloadAs(OnOffState.class).isOn());
    }

    @Test
    public void sendsRgbwMessage() {
        final JsonNode payload = new ObjectMapper().createObjectNode()
                .put("red", "100")
                .put("green", "101")
                .put("blue", "102")
                .put("white", "103");
        final String actuatorUuid = "actuator-uuid";
        final ActuatorStateUpdateRequest request = mockMessageRequest("Rgbw", payload);
        final ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        controller.updateActuatorState("actuator-uuid", request);

        verify(messagePublisher, times(1)).publishAndWaitForReport(messageCaptor.capture());
        assertEquals(request.getWidgetUuid(), messageCaptor.getValue().getSenderId());
        assertEquals(actuatorUuid, messageCaptor.getValue().getReceiverId());
        assertTrue(messageCaptor.getValue().isPayloadAs(RGBWColor.class));

        assertEquals(100, messageCaptor.getValue().getPayloadAs(RGBWColor.class).getRed());
        assertEquals(101, messageCaptor.getValue().getPayloadAs(RGBWColor.class).getGreen());
        assertEquals(102, messageCaptor.getValue().getPayloadAs(RGBWColor.class).getBlue());
        assertEquals(103, messageCaptor.getValue().getPayloadAs(RGBWColor.class).getWhite());
    }

    @Test
    public void failsOnUnknownPayloadType() {
        final ActuatorStateUpdateRequest request = mockMessageRequest("Fake", null);
        assertThrows(ServiceRequestException.class, () -> controller.updateActuatorState("actuator-uuid", request));
    }

    private ActuatorStateUpdateRequest mockMessageRequest(String payloadType, JsonNode payload) {
        return ActuatorStateUpdateRequest.builder()
                .widgetUuid("widget-uuid")
                .payloadType(payloadType)
                .payload(payload)
                .build();
    }
}
