package com.tbot.ruler.appliances;

import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.broker.model.MessageDeliveryReport;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePayload;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import lombok.Getter;

import java.util.Optional;

import static com.tbot.ruler.broker.payload.OnOffState.STATE_OFF;

public class OnOffAppliance extends AbstractAppliance<OnOffState> {

    @Getter
    private Optional<OnOffState> state;

    public OnOffAppliance(String id, ApplianceStatePersistenceService persistenceService) {
        super(id, persistenceService);
        state = persistenceService.retrieve(this.getUuid());
    }

    @Override
    public void acceptMessage(Message message) {
        setState(message.getPayload());
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload messagePayload) {
        return Optional.of(Message.builder()
            .senderId(getUuid())
            .payload(OnOffState.of(determineValue(messagePayload.getPayload())))
            .build());
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
        super.acceptDeliveryReport(deliveryReport);
        if (deliveryReport.deliverySuccessful() || deliveryReport.noReceiversFound()) {
            setState(deliveryReport.getOriginalMessage().getPayload());
            getPersistenceService().persist(this.getUuid(), state.get());
        }
    }

    private void setState(Object payload) {
        this.state = Optional.of(OnOffState.of(determineValue(payload)));
    }

    private boolean determineValue(Object payload) {
        if (payload instanceof BinaryStateClaim) {
            BinaryStateClaim claim = (BinaryStateClaim) payload;
            if (claim.isToggle()) {
                return !this.state.orElse(STATE_OFF).isOn();
            } else {
                return claim.isSetOn();
            }
        } else if (payload instanceof OnOffState) {
            return ((OnOffState) payload).isOn();
        } else {
            throw new MessageUnsupportedException("Unsupported message payload of class " + payload.getClass());
        }
    }
}
