package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryClaim;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CentralSceneHoldActuatorTest {

    @Mock
    private MessagePublisher messagePublisher;

    @Test
    public void testMessageIsPublishedWhenHoldDurationIsWithinLimits() {
        CentralSceneHoldActuator actuator = constructActuator(0, 2000);
        actuator.handleCommandKey(CentralSceneKeyAttribute.KEY_HELD_DOWN);
        actuator.handleCommandKey(CentralSceneKeyAttribute.KEY_RELEASED);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messagePublisher).publishMessage(messageCaptor.capture());

        assertNotNull(messageCaptor.getValue());
        assertEquals("actuator-uuid", messageCaptor.getValue().getSenderId());
        assertEquals(BinaryClaim.TOGGLE, messageCaptor.getValue().getPayload());
    }

    @Test
    public void testNoMessageWhenHoldDurationIsTooShort() {
        CentralSceneHoldActuator actuator = constructActuator(1000, 2000);
        actuator.handleCommandKey(CentralSceneKeyAttribute.KEY_HELD_DOWN);
        actuator.handleCommandKey(CentralSceneKeyAttribute.KEY_RELEASED);

        verify(messagePublisher, times(0)).publishMessage(any(Message.class));
    }

    @Test
    public void testNoMessageWhenHoldDurationIsTooLong() throws Exception {
        CentralSceneHoldActuator actuator = constructActuator(1, 2);
        actuator.handleCommandKey(CentralSceneKeyAttribute.KEY_HELD_DOWN);
        Thread.sleep(3);
        actuator.handleCommandKey(CentralSceneKeyAttribute.KEY_RELEASED);

        verify(messagePublisher, times(0)).publishMessage(any(Message.class));
    }

    private CentralSceneHoldActuator constructActuator(long minHold, long maxHold) {
        return CentralSceneHoldActuator.builder()
            .uuid("actuator-uuid")
            .name("actuator-name")
            .description("actuator-description")
            .minMillisecondsOfHold(minHold)
            .maxMillisecondsOfHold(maxHold)
            .messagePublisher(messagePublisher)
            .build();
    }
}
