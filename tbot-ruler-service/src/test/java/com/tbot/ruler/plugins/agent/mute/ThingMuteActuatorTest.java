package com.tbot.ruler.plugins.agent.mute;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryState;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import com.tbot.ruler.subjects.thing.RulerThingAgent;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThingMuteActuatorTest {

    @Mock
    private SubjectStateService stateService;

    @Mock
    private MessagePublisher messagePublisher;

    private RulerThingAgent rulerThingAgent;

    @BeforeEach
    public void setUp() {
        rulerThingAgent = new RulerThingAgent();
    }

    @Test
    public void testStateIsInvertedWhenMuting() {
        ThingMuteActuator actuator = constructActuator(true);
        actuator.acceptMessage(Message.builder()
                .senderId("sender-id")
                .payload(BinaryState.ON)
                .build());

        assertTrue(actuator.getState().getPayload().isOn());
        assertFalse(rulerThingAgent.isOnMute());
    }

    @Test
    public void testStateIsNotInvertedWhenMuting() {
        ThingMuteActuator actuator = constructActuator(false);
        actuator.acceptMessage(Message.builder()
                .senderId("sender-id")
                .payload(BinaryState.OFF)
                .build());

        assertFalse(actuator.getState().getPayload().isOn());
        assertFalse(rulerThingAgent.isOnMute());
    }

    @Test
    public void testStateIsRecoveredFromStateRepository() {
        when(stateService.recoverActuatorState(eq("actuator-uuid"), eq(BinaryState.class)))
            .thenReturn(ActuatorState.<BinaryState>builder()
                .actuatorUuid("actuator-uuid")
                .payload(BinaryState.OFF)
                .build());

        ThingMuteActuator actuator = constructActuator(true);
        assertFalse(actuator.getState().getPayload().isOn());
        assertTrue(rulerThingAgent.isOnMute());
    }

    @Test
    public void testStateIsPersisted() {
        ThingMuteActuator actuator = constructActuator(true);
        actuator.acceptMessage(Message.builder()
                .senderId("sender-id")
                .payload(BinaryState.ON)
                .build());

        ArgumentCaptor<ActuatorState> stateCaptor = ArgumentCaptor.forClass(ActuatorState.class);
        verify(stateService).persistState(stateCaptor.capture());

        assertNotNull(stateCaptor.getValue());
        assertTrue(((BinaryState) stateCaptor.getValue().getPayload()).isOn());
        assertEquals("actuator-uuid", stateCaptor.getValue().getActuatorUuid());
    }

    private ThingMuteActuator constructActuator(boolean invertStates) {
        ThingMuteActuatorConfiguration configuration = new ThingMuteActuatorConfiguration();
        configuration.setInvertStates(invertStates);

        return ThingMuteActuator.builder()
            .uuid("actuator-uuid")
            .name("actuator-name")
            .rulerThingContext(RulerThingContext.builder()
                .thingUuid("thing-uuid")
                .thingName("thing-name")
                .rulerThingAgent(rulerThingAgent)
                .subjectStateService(stateService)
                .messagePublisher(messagePublisher)
                .build())
            .configuration(configuration)
            .build();
    }
}
