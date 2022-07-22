package com.tbot.ruler.appliances;

import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.service.PersistenceService;
import com.tbot.ruler.things.ApplianceId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAppliance<T> implements Appliance<T> {

    @NonNull private ApplianceId id;
    @NonNull private PersistenceService persistenceService;

    @Override
    public void acceptDeliveryReport(DeliveryReport report) {
        log.debug("Delivery report sender: {}, empty: {}, success: {}, failure: {}, part failure: {}",
            id.getValue(), report.noReceiversFound(), report.deliverySuccessful(), report.deliveryFailed(), report.deliveryPartiallyFailed());
    }
}
