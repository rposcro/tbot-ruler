package com.tbot.ruler.appliances;

import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePayload;
import com.tbot.ruler.service.ApplianceStatePersistenceService;

import java.util.Optional;

public class RGBWAppliance extends AbstractAppliance<RGBWColor> {

    private Optional<RGBWColor> colorState;

    public RGBWAppliance(String id, ApplianceStatePersistenceService persistenceService) {
        super(id, persistenceService);
        colorState = persistenceService.retrieve(this.getUuid());
    }

    @Override
    public void acceptMessage(Message message) {
        setState(message.getPayloadAs(RGBWColor.class));
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload messagePayload) {
        return Optional.of(Message.builder()
            .senderId(getUuid())
            .payload(messagePayload.getPayload())
            .build());
    }

    @Override
    public void acceptPublicationReport(MessagePublicationReport publicationReport) {
        super.acceptPublicationReport(publicationReport);
        if (publicationReport.publicationSuccessful() || publicationReport.noReceiversFound()) {
            setState(publicationReport.getOriginalMessage().getPayloadAs(RGBWColor.class));
            getPersistenceService().persist(this.getUuid(), colorState.get());
        }
    }

    @Override
    public Optional<RGBWColor> getState() {
        return this.colorState;
    }

    private void setState(RGBWColor rgbw) {
        this.colorState = Optional.of(rgbw);
    }
}
