package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.payload.BinaryState;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SingleIntervalAgentTest {

    @Mock
    private SubjectStateService stateService;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void stateIsInitializedFromDefaultValue(boolean defaultState) {
        final String uuid = "dummy-uuid";
        final SingleIntervalAgent agent = SingleIntervalAgent.builder()
            .actuatorUuid(uuid)
            .defaultState(defaultState)
            .subjectStateService(stateService)
            .build();

        assertEquals(uuid, agent.getCurrentState().getActuatorUuid());
        assertEquals(defaultState, agent.getCurrentState().getPayload().isOn());
        assertEquals(defaultState, agent.isActivated());
    }

    @Test
    public void stateIsInitializedFromPersistence() {
        final String uuid = "dummy-uuid";
        final ActuatorState<BinaryState> persistedState = ActuatorState.of(uuid, BinaryState.ON);

        when(stateService.recoverActuatorState(eq(uuid), eq(BinaryState.class)))
            .thenReturn(Optional.of(persistedState));

        final SingleIntervalAgent agent = SingleIntervalAgent.builder()
            .actuatorUuid(uuid)
            .defaultState(false)
            .subjectStateService(stateService)
            .build();

        assertEquals(uuid, agent.getCurrentState().getActuatorUuid());
        assertTrue(agent.getCurrentState().getPayload().isOn());
        assertTrue(agent.isActivated());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void setsActivationState(boolean requestedState) {
        final String uuid = "dummy-uuid";
        final SingleIntervalAgent agent = SingleIntervalAgent.builder()
            .actuatorUuid(uuid)
            .defaultState(!requestedState)
            .subjectStateService(stateService)
            .build();

        agent.setActivated(requestedState);
        assertEquals(requestedState, agent.isActivated());
    }
}
