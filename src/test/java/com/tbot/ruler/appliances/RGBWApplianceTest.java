package com.tbot.ruler.appliances;

import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.MessagePayload;
import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RGBWApplianceTest extends AbstractApplianceTest {

    private final static String APPLIANCE_ID = "appliance-id";

    @Mock
    private ApplianceStatePersistenceService persistenceService;

    private RGBWAppliance appliance;

    @BeforeEach
    public void setUp() {
        this.appliance = new RGBWAppliance(APPLIANCE_ID, "", persistenceService);
    }

    @Test
    public void initialApplianceStateIsCorrect() {
        assertEquals(APPLIANCE_ID, appliance.getUuid());
        assertFalse(appliance.getState().isPresent());
    }

    @Test
    public void setsStateFromMessage() {
        RGBWColor payload = RGBWColor.of(12, 120, 50, 99);
        Message message = mockMessage(payload);

        appliance.acceptMessage(message);

        assertEquals(APPLIANCE_ID, appliance.getUuid());
        assertTrue(appliance.getState().isPresent());
        assertEquals(payload, appliance.getState().get());
    }

    @Test
    public void failsToSetStateFromIncorrectMessagePayload() {
        Message message = mockMessage(new Object());

        assertThrows(MessageUnsupportedException.class, () -> appliance.acceptMessage(message));
    }

    @Test
    public void setsStateFromDirectMessage() {
        RGBWColor payload = RGBWColor.of(12, 120, 50, 99);
        MessagePayload messagePayload = new MessagePayload(payload);

        Optional<Message> forwardMessage = appliance.acceptDirectPayload(messagePayload);

        assertFalse(appliance.getState().isPresent());
        assertTrue(forwardMessage.isPresent());
        assertEquals(APPLIANCE_ID, forwardMessage.get().getSenderId());
        assertEquals(payload, forwardMessage.get().getPayload());

        MessagePublicationReport publicationReport = MessagePublicationReport.builder()
                .originalMessage(forwardMessage.get())
                .build();
        appliance.acceptPublicationReport(publicationReport);

        assertTrue(appliance.getState().isPresent());
        assertEquals(payload, appliance.getState().get());
    }
}
