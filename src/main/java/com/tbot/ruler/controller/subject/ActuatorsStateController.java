package com.tbot.ruler.controller.subject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.broker.SynchronousMessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.subject.payload.ActuatorStatePayloadType;
import com.tbot.ruler.controller.subject.payload.ActuatorStateUpdateRequest;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.service.things.ActuatorsService;
import com.tbot.ruler.subjects.ActuatorState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/actuators")
public class ActuatorsStateController extends AbstractController {

    private final ActuatorsService actuatorsService;
    private final SynchronousMessagePublisher messagePublisher;
    private final ObjectMapper objectMapper;

    @Autowired
    public ActuatorsStateController(
            ActuatorsService actuatorsService,
            SynchronousMessagePublisher messagePublisher,
            ObjectMapper objectMapper) {
        this.actuatorsService = actuatorsService;
        this.messagePublisher = messagePublisher;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{actuatorUuid}/state")
    public ResponseEntity<ActuatorState> getActuatorState(@PathVariable("actuatorUuid") String actuatorUuid) {
        log.debug("Requested actuator state for uuid {}", actuatorUuid);
        return ok(actuatorsService.getActuatorState(actuatorUuid));
    }

    @PutMapping("/{actuatorUuid}/state")
    public ResponseEntity<MessagePublicationReport> updateActuatorState(
            @PathVariable("actuatorUuid") String actuatorUuid,
            @RequestBody ActuatorStateUpdateRequest stateUpdateRequest) {
        log.debug("Requested actuator state update {}", stateUpdateRequest);
        Message message = toMessage(actuatorUuid, stateUpdateRequest);
        MessagePublicationReport report = messagePublisher.publishAndWaitForReport(message);
        return ok(report);
    }

    @PutMapping("/state")
    public ResponseEntity<MessagePublicationReport> broadcastStateUpdate(
            @RequestBody ActuatorStateUpdateRequest stateUpdateRequest) {
        log.debug("Requested actuator state update broadcast {}", stateUpdateRequest);
        Message message = toMessage(null, stateUpdateRequest);
        MessagePublicationReport report = messagePublisher.publishAndWaitForReport(message);
        return ok(report);
    }

    private Message toMessage(String actuatorUuid, ActuatorStateUpdateRequest stateUpdateRequest) {
        ActuatorStatePayloadType payloadType = ActuatorStatePayloadType.fromString(stateUpdateRequest.getPayloadType())
                .orElseThrow(() -> new ServiceRequestException("Unknown payload type " + stateUpdateRequest.getPayloadType()));

        try {
            Object payload = switch (payloadType) {
                case Object -> stateUpdateRequest.getPayload();
                case OnOff -> objectMapper.readerFor(OnOffState.class).readValue(stateUpdateRequest.getPayload());
                case Rgbw -> objectMapper.readerFor(RGBWColor.class).readValue(stateUpdateRequest.getPayload());
            };
            return Message.builder()
                    .senderId(stateUpdateRequest.getWidgetUuid())
                    .receiverId(actuatorUuid)
                    .payload(payload)
                    .build();
        } catch(IOException e) {
            throw new ServiceRequestException(String.format("Invalid message payload type %s for content %s", payloadType, stateUpdateRequest.getPayload()));
        }
    }
}
