package com.tbot.ruler.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.tbot.ruler.broker.SynchronousMessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.controller.payload.BrokerMessageRequest;
import com.tbot.ruler.exceptions.ServiceRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BrokerMessageControllerTest {

    @Mock
    private SynchronousMessagePublisher messagePublisher;

    private BrokerMessageController controller;

    @BeforeEach
    public void setUp() {
        this.controller = new BrokerMessageController(messagePublisher, new ObjectMapper());
    }

    @Test
    public void sendsObjectMessage() {
        final JsonNode payload = new TextNode("a payload");
        final BrokerMessageRequest request = mockMessageRequest("Object", payload);
        final ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        controller.sendMessage(request);

        verify(messagePublisher, times(1)).publishAndWaitForReport(messageCaptor.capture());
        assertEquals(request.getWidgetUuid(), messageCaptor.getValue().getSenderId());
        assertEquals(request.getReceiverUuid(), messageCaptor.getValue().getReceiverId());
        assertEquals(payload, messageCaptor.getValue().getPayload());
    }

    @Test
    public void sendsOnOffMessage() {
        final JsonNode payload = new ObjectMapper().createObjectNode().put("on", "true");
        final BrokerMessageRequest request = mockMessageRequest("OnOff", payload);
        final ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        controller.sendMessage(request);

        verify(messagePublisher, times(1)).publishAndWaitForReport(messageCaptor.capture());
        assertEquals(request.getWidgetUuid(), messageCaptor.getValue().getSenderId());
        assertEquals(request.getReceiverUuid(), messageCaptor.getValue().getReceiverId());
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
        final BrokerMessageRequest request = mockMessageRequest("Rgbw", payload);
        final ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        controller.sendMessage(request);

        verify(messagePublisher, times(1)).publishAndWaitForReport(messageCaptor.capture());
        assertEquals(request.getWidgetUuid(), messageCaptor.getValue().getSenderId());
        assertEquals(request.getReceiverUuid(), messageCaptor.getValue().getReceiverId());
        assertTrue(messageCaptor.getValue().isPayloadAs(RGBWColor.class));

        assertEquals(100, messageCaptor.getValue().getPayloadAs(RGBWColor.class).getRed());
        assertEquals(101, messageCaptor.getValue().getPayloadAs(RGBWColor.class).getGreen());
        assertEquals(102, messageCaptor.getValue().getPayloadAs(RGBWColor.class).getBlue());
        assertEquals(103, messageCaptor.getValue().getPayloadAs(RGBWColor.class).getWhite());
    }

    @Test
    public void failsOnUnknownPayloadType() {
        final BrokerMessageRequest request = mockMessageRequest("Fake", null);
        assertThrows(ServiceRequestException.class, () -> controller.sendMessage(request));
    }

    private BrokerMessageRequest mockMessageRequest(String payloadType, JsonNode payload) {
        return BrokerMessageRequest.builder()
                .widgetUuid("widget-uuid")
                .receiverUuid("receiver-uuid")
                .payloadType(payloadType)
                .payload(payload)
                .build();
    }
}
