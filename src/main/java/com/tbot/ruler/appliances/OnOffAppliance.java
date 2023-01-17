package com.tbot.ruler.appliances;

import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessagePayload;
import com.tbot.ruler.messages.payloads.BooleanTogglePayload;
import com.tbot.ruler.messages.payloads.BooleanUpdatePayload;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import lombok.Getter;

import java.util.Optional;

import static com.tbot.ruler.model.OnOffState.STATE_OFF;

public class OnOffAppliance extends AbstractAppliance<OnOffState> {

    @Getter
    private Optional<OnOffState> state;

    public OnOffAppliance(String id, ApplianceStatePersistenceService persistenceService) {
        super(id, persistenceService);
        state = persistenceService.retrieve(this.getId());
    }

    @Override
    public void acceptMessage(Message message) {
        setState(message.getPayload());
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload payload) {
        return Optional.of(Message.builder()
            .senderId(getId())
            .payload(BooleanUpdatePayload.of(determineValue(payload)))
            .build());
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
        super.acceptDeliveryReport(deliveryReport);
        if (deliveryReport.deliverySuccessful() || deliveryReport.noReceiversFound()) {
            setState(deliveryReport.getOriginalMessage().getPayload());
            getPersistenceService().persist(this.getId(), state);
        }
    }

    private void setState(MessagePayload messagePayload) {
        this.state = Optional.of(OnOffState.of(determineValue(messagePayload)));
    }

    private boolean determineValue(MessagePayload messagePayload) {
        if (messagePayload instanceof BooleanTogglePayload) {
            return !this.state.orElse(STATE_OFF).isOn();
        } else if (messagePayload instanceof BooleanUpdatePayload) {
            return ((BooleanUpdatePayload) messagePayload).isState();
        } else {
            throw new MessageUnsupportedException("Unsupported message payload of class " + messagePayload.getClass());
        }
    }
}
