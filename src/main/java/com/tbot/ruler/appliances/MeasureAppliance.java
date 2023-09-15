package com.tbot.ruler.appliances;

import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.MessagePayload;
import com.tbot.ruler.model.Measure;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class MeasureAppliance extends AbstractAppliance<Measure> {

    private Optional<Measure> measureState;

    public MeasureAppliance(String id, ApplianceStatePersistenceService persistenceService) {
        super(id, persistenceService);
        this.measureState = persistenceService.retrieve(this.getUuid());
    }

    @Override
    public void acceptMessage(Message message) {
        setState(message.getPayloadAs(Measure.class));
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload payload) {
        throw new MessageUnsupportedException("Direct messages unsupported by appliance " + this.getClass());
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
        super.acceptDeliveryReport(deliveryReport);
        if (deliveryReport.deliverySuccessful() || deliveryReport.noReceiversFound()) {
            setState(deliveryReport.getOriginalMessage().getPayloadAs(Measure.class));
            getPersistenceService().persist(this.getUuid(), measureState.get());
        }
    }

    @Override
    public Optional<Measure> getState() {
        return this.measureState;
    }

    private void setState(Measure measure) {
        this.measureState = Optional.of(measure);

        if (log.isDebugEnabled()) {
            log.debug("MeasureAppliance {} received measure {}", getUuid(), measure);
        }
    }
}
