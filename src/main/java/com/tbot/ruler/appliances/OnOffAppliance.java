package com.tbot.ruler.appliances;

import com.tbot.ruler.appliances.state.OnOffState;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.BooleanTogglePayload;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.service.PersistenceService;
import com.tbot.ruler.things.ApplianceId;
import lombok.Getter;

import java.util.Optional;

import static com.tbot.ruler.appliances.state.OnOffState.STATE_OFF;

public class OnOffAppliance extends AbstractAppliance<OnOffState> {

    private final static String PERSIST_KEY = "state";

    public OnOffAppliance(ApplianceId id, PersistenceService persistenceService) {
        super(id, persistenceService);
        persistenceService.retrieve(this.getId(), PERSIST_KEY).ifPresent(
            encState -> this.state = Optional.of(OnOffState.of(Boolean.parseBoolean(encState)))
        );
    }

    @Getter
    private Optional<OnOffState> state = Optional.empty();

    @Override
    public void acceptMessage(Message message) {
        setState(message.getPayload());
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload payload) {
        return Optional.of(Message.builder()
            .senderId(getId())
            .payload(BooleanUpdatePayload.of(state.get().isOn()))
            .build());
    }

    private void setState(MessagePayload messagePayload) {
        OnOffState newValue;

        if (messagePayload instanceof BooleanTogglePayload) {
            newValue = this.state.orElse(STATE_OFF).invert();
        } else if (messagePayload instanceof BooleanUpdatePayload) {
            newValue = OnOffState.of(((BooleanUpdatePayload) messagePayload).isState());
        } else {
            throw new MessageUnsupportedException("Unsupported message payload of class " + messagePayload.getClass());
        }

        this.state = Optional.of(newValue);
    }

    @Override
    public void acceptDeliveryReport(DeliveryReport deliveryReport) {
        super.acceptDeliveryReport(deliveryReport);
        if (deliveryReport.deliverySuccessful() || deliveryReport.noReceiversFound()) {
            getPersistenceService().persist(this.getId(), PERSIST_KEY, Boolean.toString(state.get().isOn()));
            setState(deliveryReport.getOriginalMessage().getPayload());
        }
    }
}
