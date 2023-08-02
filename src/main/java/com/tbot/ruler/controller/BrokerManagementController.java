package com.tbot.ruler.controller;

import com.tbot.ruler.messages.MessagePublicationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/broker")
public class BrokerManagementController {

    @Autowired
    private MessagePublicationManager messagePublicationManager;

    @PatchMapping(value = "/senders/{senderId}/suspension/(state}")
    public ResponseEntity changeSenderSuspension(
            @PathVariable("senderId") String senderId,
            @PathVariable("state") boolean suspensionOn) {
        if (suspensionOn) {
            messagePublicationManager.suspendMessagesFrom(senderId);
        } else {
            messagePublicationManager.unsuspendMessagesFrom(senderId);
        }

        return ResponseEntity.ok().build();
    }
}
