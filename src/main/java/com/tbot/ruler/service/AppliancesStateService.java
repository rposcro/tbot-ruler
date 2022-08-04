package com.tbot.ruler.service;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.broker.MessageQueue;
import com.tbot.ruler.exceptions.ServiceException;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.exceptions.ServiceUnavailableException;
import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.message.payloads.RGBWUpdatePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AppliancesStateService {

    @Autowired
    private DeliveryReportListenerService listenerService;

    @Autowired
    private AppliancesService appliancesService;

    @Autowired
    private MessageQueue messageQueue;

    public DeliveryReport updateApplianceState(String applianceId, BooleanUpdatePayload stateUpdate) {
        Appliance appliance = appliancesService.applianceById(applianceId).orElseThrow(
            () -> new ServiceRequestException("No appliance id " + applianceId + " found")
        );
        Optional<Message> optionalForwardMessage = appliance.acceptDirectPayload(stateUpdate);
        return publishMessage(optionalForwardMessage);
    }

    public DeliveryReport updateApplianceState(String applianceId, RGBWUpdatePayload stateUpdate) {
        Appliance appliance = appliancesService.applianceById(applianceId).orElseThrow(
            () -> new ServiceRequestException("No appliance id " + applianceId + " found")
        );
        Optional<Message> optionalForwardMessage = appliance.acceptDirectPayload(stateUpdate);
        return publishMessage(optionalForwardMessage);
    }

    private DeliveryReport publishMessage(Optional<Message> optionalForwardMessage) {
        DeliveryReport report = optionalForwardMessage
            .map(message -> listenerService.deliverAndWaitForReport(message.getId(), () -> messageQueue.publish(message), 3000))
            .orElseThrow(() -> new ServiceException("Unexpected missing delivery report without exception!"));
        if (!report.deliverySuccessful()) {
            throw new ServiceUnavailableException("Some or all items are unavailable for messaging", report.getFailedReceivers());
        }
        return report;
    }
}
