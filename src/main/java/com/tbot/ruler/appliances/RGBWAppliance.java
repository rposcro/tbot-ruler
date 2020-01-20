package com.tbot.ruler.appliances;

import com.tbot.ruler.appliances.state.RGBWState;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.RGBWUpdatePayload;
import com.tbot.ruler.service.PersistenceService;
import com.tbot.ruler.things.ApplianceId;
import lombok.Getter;

import java.util.Optional;

public class RGBWAppliance extends AbstractAppliance<RGBWState> {

    private final static String CODING_SEPARATOR = "-";
    private final static String PERSIST_KEY = "state";

    public RGBWAppliance(ApplianceId id, PersistenceService persistenceService) {
        super(id, persistenceService);
        persistenceService.retrieve(this.getId(), PERSIST_KEY).ifPresent(
            encState -> this.colorState = Optional.of(fromString(encState))
        );
    }

    @Getter
    private Optional<RGBWState> colorState = Optional.empty();

    @Override
    public void acceptMessage(Message message) {
        processPayload(message.getPayload());
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload payload) {
        return Optional.of(Message.builder()
            .senderId(getId())
            .payload(processPayload(payload))
            .build());
    }

    @Override
    public Optional<RGBWState> getState() {
        return this.colorState;
    }

    @Override
    public void acceptDeliveryReport(DeliveryReport deliveryReport) {
        super.acceptDeliveryReport(deliveryReport);
        if (deliveryReport.deliverySuccessful() || deliveryReport.noReceiversFound()) {
            getPersistenceService().persist(this.getId(), PERSIST_KEY, toString(colorState.get()));
        }
    }

    private MessagePayload processPayload(MessagePayload messagePayload) {
        if (messagePayload instanceof RGBWUpdatePayload) {
            return processColor((RGBWUpdatePayload) messagePayload);
        } else {
            throw new MessageUnsupportedException("Unsupported message payload of class " + messagePayload.getClass());
        }
    }

    private MessagePayload processColor(RGBWUpdatePayload rgbw) {
        RGBWState newState = RGBWState.of(rgbw.getRed(), rgbw.getGreen(), rgbw.getBlue(), rgbw.getWhite());
        this.colorState = Optional.of(newState);
        return rgbw;
    }

    private String toString(RGBWState state) {
        return String.join(CODING_SEPARATOR, "" + state.getRed(), "" + state.getGreen(), "" + state.getBlue(), "" + state.getWhite());
    }

    private RGBWState fromString(String encState) {
        String[] components = encState.split(CODING_SEPARATOR);
        return RGBWState.of(
            Integer.parseInt(components[0]),
            Integer.parseInt(components[1]),
            Integer.parseInt(components[2]),
            Integer.parseInt(components[3])
        );
    }
}
