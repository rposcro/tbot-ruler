package com.tbot.ruler.appliances;

import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.BooleanTogglePayload;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.service.PersistenceService;
import lombok.Getter;

import java.util.Optional;

import static com.tbot.ruler.model.OnOffState.STATE_OFF;

public class OnOffAppliance extends AbstractAppliance<OnOffState> {

    private final static String PERSIST_KEY = "state";

    public OnOffAppliance(String id, PersistenceService persistenceService) {
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
            .payload(BooleanUpdatePayload.of(determineValue(payload)))
            .build());
    }

    @Override
    public void acceptDeliveryReport(DeliveryReport deliveryReport) {
        super.acceptDeliveryReport(deliveryReport);
        if (deliveryReport.deliverySuccessful() || deliveryReport.noReceiversFound()) {
            setState(deliveryReport.getOriginalMessage().getPayload());
            getPersistenceService().persist(this.getId(), PERSIST_KEY, Boolean.toString(state.get().isOn()));
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
