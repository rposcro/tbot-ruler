package com.tbot.ruler.service;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.exceptions.ServiceException;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.exceptions.ServiceUnavailableException;
import com.tbot.ruler.messages.SynchronousMessageSender;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessagePayload;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.model.RGBWColor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AppliancesStateService {

    @Autowired
    private SynchronousMessageSender messageSender;

    @Autowired
    private AppliancesService appliancesService;

    public MessageDeliveryReport updateApplianceState(String applianceId, OnOffState onOffState) {
        Appliance appliance = appliancesService.applianceById(applianceId).orElseThrow(
            () -> new ServiceRequestException("No appliance id " + applianceId + " found")
        );
        Optional<Message> optionalForwardMessage = appliance.acceptDirectPayload(new MessagePayload(onOffState));
        return publishMessage(optionalForwardMessage);
    }

    public MessageDeliveryReport updateApplianceState(String applianceId, RGBWColor stateUpdate) {
        Appliance appliance = appliancesService.applianceById(applianceId).orElseThrow(
            () -> new ServiceRequestException("No appliance id " + applianceId + " found")
        );
        Optional<Message> optionalForwardMessage = appliance.acceptDirectPayload(
                new MessagePayload(stateUpdate));
        return publishMessage(optionalForwardMessage);
    }

    private MessageDeliveryReport publishMessage(Optional<Message> optionalForwardMessage) {
        MessageDeliveryReport report = optionalForwardMessage
            .map(message -> messageSender.sendAndWaitForReport(message, 3000))
            .orElseThrow(() -> new ServiceException("Unexpected missing delivery report without exception!"));
        if (!report.deliverySuccessful()) {
            throw new ServiceUnavailableException("Some or all items are unavailable for messaging", report.getFailedReceivers());
        }
        return report;
    }
}
