package com.tbot.ruler.appliances;

import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePayload;
import com.tbot.ruler.broker.payload.Measure;
import com.tbot.ruler.broker.payload.MeasureQuantity;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MeasureApplianceTest extends AbstractApplianceTest {

    private final static String APPLIANCE_ID = "appliance-id";

    @Mock
    private ApplianceStatePersistenceService persistenceService;

    private MeasureAppliance appliance;

    @BeforeEach
    public void setUp() {
        this.appliance = new MeasureAppliance(APPLIANCE_ID, "", persistenceService);
    }

    @Test
    public void initialApplianceStateIsCorrect() {
        assertEquals(APPLIANCE_ID, appliance.getUuid());
        assertFalse(appliance.getState().isPresent());
    }

    @Test
    public void setsStateFromMessage() {
        Measure payload = mockMeasure();
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
    public void declinesDirectMessages() {
        Measure payload = mockMeasure();
        MessagePayload messagePayload = new MessagePayload(payload);

        assertThrows(MessageUnsupportedException.class, () -> appliance.acceptDirectPayload(messagePayload));
    }

    private Measure mockMeasure() {
        return Measure.builder()
                .quantity(MeasureQuantity.Temperature)
                .unit("C")
                .value(333)
                .decimals((short) 4)
                .build();
    }
}
