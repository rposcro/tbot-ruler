package com.tbot.ruler.appliances;

import com.tbot.ruler.broker.model.MessagePublicationReport;
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
    private String uuid;

    @NonNull
    private ApplianceStatePersistenceService persistenceService;

    @Override
    public void acceptPublicationReport(MessagePublicationReport report) {
        log.debug("Publication report sender: {}, no receivers: {}, success: {}, failure: {}, part failure: {}",
                uuid, report.noReceiversFound(), report.publicationSuccessful(), report.publicationFailed(), report.publicationPartiallyFailed());
    }
}
