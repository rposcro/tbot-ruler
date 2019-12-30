package com.tbot.ruler.service;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.appliances.state.OnOffState;
import com.tbot.ruler.broker.MessageQueue;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
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

    public void updateApplianceState(ApplianceId applianceId, OnOffState state) {
        Appliance appliance = appliancesService.applianceById(applianceId);
        Optional<Message> optionalForwardMessage = appliance.acceptDirectPayload(BooleanUpdatePayload.of(state.isOn()));
        optionalForwardMessage.ifPresent(message -> messageQueue.publish(message));
    }
}
