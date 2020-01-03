package com.tbot.ruler.service;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.broker.MessageQueue;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.message.payloads.RGBWUpdatePayload;
import com.tbot.ruler.things.ApplianceId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AppliancesStateService {

    @Autowired
    private AppliancesService appliancesService;

    @Autowired
    private MessageQueue messageQueue;

    public void updateApplianceState(ApplianceId applianceId, BooleanUpdatePayload stateUpdate) {
        Appliance appliance = appliancesService.applianceById(applianceId);
        Optional<Message> optionalForwardMessage = appliance.acceptDirectPayload(stateUpdate);
        optionalForwardMessage.ifPresent(message -> messageQueue.publish(message));
    }

    public void updateApplianceState(ApplianceId applianceId, RGBWUpdatePayload stateUpdate) {
        Appliance appliance = appliancesService.applianceById(applianceId);
        Optional<Message> optionalForwardMessage = appliance.acceptDirectPayload(stateUpdate);
        optionalForwardMessage.ifPresent(message -> messageQueue.publish(message));
    }
}
