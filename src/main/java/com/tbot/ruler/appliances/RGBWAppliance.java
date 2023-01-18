package com.tbot.ruler.appliances;

import com.tbot.ruler.model.RGBWColor;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessagePayload;
import com.tbot.ruler.messages.payloads.RGBWUpdatePayload;
import com.tbot.ruler.service.ApplianceStatePersistenceService;

import java.util.Optional;

public class RGBWAppliance extends AbstractAppliance<RGBWColor> {

    private Optional<RGBWColor> colorState;

    public RGBWAppliance(String id, ApplianceStatePersistenceService persistenceService) {
        super(id, persistenceService);
        colorState = persistenceService.retrieve(this.getId());
    }

    @Override
    public void acceptMessage(Message message) {
        setState(message.getPayloadObject());
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload payload) {
        RGBWUpdatePayload forwardPayload = asPayloadType(payload);
        return Optional.of(Message.builder()
            .senderId(getId())
            .payload(forwardPayload)
            .build());
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
        super.acceptDeliveryReport(deliveryReport);
        if (deliveryReport.deliverySuccessful() || deliveryReport.noReceiversFound()) {
            setState(deliveryReport.getOriginalMessage().getPayloadObject());
            getPersistenceService().persist(this.getId(), colorState.get());
        }
    }

    @Override
    public Optional<RGBWColor> getState() {
        return this.colorState;
    }

    private MessagePayload setState(RGBWUpdatePayload rgbw) {
        RGBWColor newState = RGBWColor.of(rgbw.getRed(), rgbw.getGreen(), rgbw.getBlue(), rgbw.getWhite());
        this.colorState = Optional.of(newState);
        return rgbw;
    }
}
