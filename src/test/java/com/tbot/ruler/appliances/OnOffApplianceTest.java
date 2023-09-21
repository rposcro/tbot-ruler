package com.tbot.ruler.appliances;

import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.MessagePayload;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class OnOffApplianceTest extends AbstractApplianceTest {

    private final static String APPLIANCE_ID = "appliance-id";
    private final static String APPLIANCE_NAME = "appliance-name";

    @Mock
    private ApplianceStatePersistenceService persistenceService;

    private OnOffAppliance appliance;

    @BeforeEach
    public void setUp() {
        this.appliance = new OnOffAppliance(APPLIANCE_ID, APPLIANCE_NAME, persistenceService);
    }

    @Test
    public void initialApplianceStateIsCorrect() {
        assertEquals(APPLIANCE_ID, appliance.getUuid());
        assertFalse(appliance.getState().isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"false", "true"})
    public void setsStateFromOnOffStateMessage(String stateArg) {
        boolean expectedStateValue = Boolean.parseBoolean(stateArg);
        OnOffState payload = OnOffState.of(expectedStateValue);
        Message message = mockMessage(payload);

        appliance.acceptMessage(message);

        assertEquals(APPLIANCE_ID, appliance.getUuid());
        assertTrue(appliance.getState().isPresent());
        assertEquals(expectedStateValue, appliance.getState().get().isOn());
    }

    @ParameterizedTest
    @EnumSource(BinaryStateClaim.class)
    public void setsStateFromBinaryStateClaimMessage(BinaryStateClaim claimType) {
        boolean expectedStateValue = claimType != BinaryStateClaim.SET_OFF;
        Message message = mockMessage(claimType);

        appliance.acceptMessage(message);

        assertEquals(APPLIANCE_ID, appliance.getUuid());
        assertTrue(appliance.getState().isPresent());
        assertEquals(expectedStateValue, appliance.getState().get().isOn());
    }

    @Test
    public void failsToSetStateFromIncorrectMessagePayload() {
        Message message = mockMessage(new Object());

        assertThrows(MessageUnsupportedException.class, () -> appliance.acceptMessage(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {"false", "true"})
    public void setsStateFromDirectOnOffStateMessage(String stateArg) {
        boolean expectedStateValue = Boolean.parseBoolean(stateArg);
        OnOffState payload = OnOffState.of(expectedStateValue);
        MessagePayload messagePayload = new MessagePayload(payload);

        Optional<Message> forwardMessage = appliance.acceptDirectPayload(messagePayload);

        assertEquals(APPLIANCE_ID, forwardMessage.get().getSenderId());
        assertFalse(appliance.getState().isPresent());
        assertTrue(forwardMessage.isPresent());
        assertEquals(OnOffState.class, forwardMessage.get().getPayload().getClass());
        assertEquals(expectedStateValue, forwardMessage.get().getPayloadAs(OnOffState.class).isOn());

        MessagePublicationReport publicationReport = MessagePublicationReport.builder()
                .originalMessage(forwardMessage.get())
                .build();
        appliance.acceptPublicationReport(publicationReport);

        assertTrue(appliance.getState().isPresent());
        assertEquals(expectedStateValue, appliance.getState().get().isOn());
    }

    @ParameterizedTest
    @EnumSource(BinaryStateClaim.class)
    public void setsStateFromDirectBinaryStateClaimMessage(BinaryStateClaim claimType) {
        boolean expectedStateValue = claimType != BinaryStateClaim.SET_OFF;
        MessagePayload messagePayload = new MessagePayload(claimType);

        Optional<Message> forwardMessage = appliance.acceptDirectPayload(messagePayload);

        assertEquals(APPLIANCE_ID, forwardMessage.get().getSenderId());
        assertFalse(appliance.getState().isPresent());
        assertTrue(forwardMessage.isPresent());
        assertEquals(OnOffState.class, forwardMessage.get().getPayload().getClass());
        assertEquals(expectedStateValue, forwardMessage.get().getPayloadAs(OnOffState.class).isOn());

        MessagePublicationReport publicationReport = MessagePublicationReport.builder()
                .originalMessage(forwardMessage.get())
                .build();
        appliance.acceptPublicationReport(publicationReport);

        assertTrue(appliance.getState().isPresent());
        assertEquals(expectedStateValue, appliance.getState().get().isOn());
    }
}
