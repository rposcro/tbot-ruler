package com.tbot.ruler.appliances;

import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessagePayload;
import com.tbot.ruler.model.ReportLog;
import com.tbot.ruler.model.ReportLogLevel;
import com.tbot.ruler.service.ApplianceStatePersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ReportApplianceTest extends AbstractApplianceTest {

    private final static String APPLIANCE_ID = "appliance-id";

    @Mock
    private ApplianceStatePersistenceService persistenceService;

    private ReportAppliance appliance;

    @BeforeEach
    public void setUp() {
        this.appliance = new ReportAppliance(APPLIANCE_ID, persistenceService);
    }

    @Test
    public void initialApplianceStateIsCorrect() {
        assertEquals(APPLIANCE_ID, appliance.getUuid());
        assertTrue(appliance.getState().isPresent());
    }

    @Test
    public void addsLogFromMessage() {
        ReportLog payload1 = ReportLog.builder()
                .logLevel(ReportLogLevel.CRITICAL)
                .content("A bomb is about to set off")
                .timestamp(ZonedDateTime.now())
                .build();
        ReportLog payload2 = ReportLog.builder()
                .logLevel(ReportLogLevel.CRITICAL)
                .content("A bomb is about to set off")
                .timestamp(ZonedDateTime.now())
                .build();

        appliance.acceptMessage(mockMessage(payload1));
        appliance.acceptMessage(mockMessage(payload2));

        assertEquals(APPLIANCE_ID, appliance.getUuid());
        assertTrue(appliance.getState().isPresent());
        assertEquals(2, appliance.getState().get().size());
        assertEquals(payload1, appliance.getState().get().get(0));
        assertEquals(payload2, appliance.getState().get().get(1));
    }

    @Test
    public void failsToSetStateFromIncorrectMessagePayload() {
        Message message = mockMessage(new Object());

        assertThrows(MessageUnsupportedException.class, () -> appliance.acceptMessage(message));
    }

    @Test
    public void declinesDirectMessages() {
        MessagePayload messagePayload = new MessagePayload(new Object());

        assertThrows(MessageUnsupportedException.class, () -> appliance.acceptDirectPayload(messagePayload));
    }
}
