package com.tbot.ruler.controller.broker;

import com.tbot.ruler.service.lifetime.BrokerLifetimeService;
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
public class BrokerLifetimeController {

    @Autowired
    private BrokerLifetimeService brokerLifetimeService;

    @PatchMapping(value = "/delivery/{state}")
    public ResponseEntity changeDeliveryStatus(@PathVariable("state") boolean deliveryOn) {
        log.debug("Requested broker delivery state change to {}", deliveryOn);

        if (deliveryOn) {
            brokerLifetimeService.startBroker();
        } else {
            brokerLifetimeService.stopBroker();
        }

        return ResponseEntity.ok().build();
    }
}
