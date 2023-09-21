package com.tbot.ruler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.broker.SynchronousMessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.controller.payload.BrokerMessagePayloadType;
import com.tbot.ruler.controller.payload.BrokerMessageRequest;
import com.tbot.ruler.exceptions.ServiceRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/broker/messages")
public class BrokerMessageController extends AbstractController {

    private final SynchronousMessagePublisher messagePublisher;
    private final ObjectMapper objectMapper;

    @Autowired
    public BrokerMessageController(SynchronousMessagePublisher messagePublisher, ObjectMapper objectMapper) {
        this.messagePublisher = messagePublisher;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<MessagePublicationReport> sendMessage(@RequestBody BrokerMessageRequest messageRequest) {
        log.debug("Requested sending message {}", messageRequest);
        Message message = toMessage(messageRequest);
        MessagePublicationReport report = messagePublisher.publishAndWaitForReport(message);
        return response(ResponseEntity.ok()).body(report);
    }

    private Message toMessage(BrokerMessageRequest messageRequest) {
        BrokerMessagePayloadType payloadType = BrokerMessagePayloadType.fromString(messageRequest.getPayloadType())
                .orElseThrow(() -> new ServiceRequestException("Unknown payload type " + messageRequest.getPayloadType()));

        try {
            Object payload = switch (payloadType) {
                case Object -> messageRequest.getPayload();
                case OnOff -> objectMapper.readerFor(OnOffState.class).readValue(messageRequest.getPayload());
                case Rgbw -> objectMapper.readerFor(RGBWColor.class).readValue(messageRequest.getPayload());
            };
            return Message.builder()
                    .senderId(messageRequest.getWidgetUuid())
                    .receiverId(messageRequest.getReceiverUuid())
                    .payload(payload)
                    .build();
        } catch(IOException e) {
            throw new ServiceRequestException(String.format("Invalid message payload type %s for content %s", payloadType, messageRequest.getPayload()));
        }
    }
}
