package com.tbot.ruler.controller;

import com.tbot.ruler.broker.MessagePublicationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(value = "/broker")
public class BrokerManagementController {

    @Autowired
    private MessagePublicationManager messagePublicationManager;

    @GetMapping(value = "/suspensions/senders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<String>> getSuspendedSenders() {
        return ResponseEntity.ok(messagePublicationManager.getSuspendedSenders());
    }

    @PatchMapping(value = "/suspensions/senders/{senderId}/{state}")
    public ResponseEntity changeSenderSuspension(
            @PathVariable("senderId") String senderId,
            @PathVariable("state") String suspensionOn) {
        log.debug("Requested suspension state {} to sender {}", suspensionOn, senderId);
        if (Boolean.parseBoolean(suspensionOn)) {
            messagePublicationManager.suspendMessagesFrom(senderId);
        } else {
            messagePublicationManager.unsuspendMessagesFrom(senderId);
        }

        return ResponseEntity.ok().build();
    }
}
