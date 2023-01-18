package com.tbot.ruler.appliances;

import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAppliance<T> implements Appliance<T> {

    @NonNull
    private String id;

    @NonNull
    private ApplianceStatePersistenceService persistenceService;

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport report) {
        log.debug("Delivery report sender: {}, empty: {}, success: {}, failure: {}, part failure: {}",
            id, report.noReceiversFound(), report.deliverySuccessful(), report.deliveryFailed(), report.deliveryPartiallyFailed());
    }
}
