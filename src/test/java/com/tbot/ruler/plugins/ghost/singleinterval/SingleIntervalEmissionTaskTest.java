package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.plugins.ghost.DateTimeRange;
import com.tbot.ruler.service.things.SubjectStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SingleIntervalEmissionTaskTest {

    private final ZoneId ZONE_ID = ZoneId.of("GMT+2");
    private final String EMITTER_ID = "Dummy One";
    private final LocalDateTime NOW = LocalDateTime.of(2023, 9, 7, 13, 12);

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private SubjectStateService subjectStateService;

    @Mock
    private Supplier<LocalDateTime> timer;

    @Test
    public void initialIntervalWithoutSpanIsCorrect() {
        when(timer.get()).thenReturn(NOW);

        SingleIntervalEmissionTask emissionTask = createTask(10, 15, 0);
        LocalDateTime expectedStart = NOW.toLocalDate().atTime(10, 0);
        LocalDateTime expectedEnd = NOW.toLocalDate().atTime(15, 0);

        assertEquals(expectedStart, emissionTask.getOnInterval().getStartDateTime());
        assertEquals(expectedEnd, emissionTask.getOnInterval().getEndDateTime());
    }

    @Test
    public void initialIntervalWithSpanIsCorrect() {
        when(timer.get()).thenReturn(NOW);

        SingleIntervalEmissionTask emissionTask = createTask(20, 4, 0);
        LocalDateTime expectedStart = NOW.toLocalDate().atTime(20, 0);
        LocalDateTime expectedEnd = NOW.toLocalDate().atTime(4, 0).plusDays(1);

        assertEquals(expectedStart, emissionTask.getOnInterval().getStartDateTime());
        assertEquals(expectedEnd, emissionTask.getOnInterval().getEndDateTime());
    }

    @Test
    public void initialIntervalWithVariation() {
        when(timer.get()).thenReturn(NOW);

        SingleIntervalEmissionTask emissionTask = createTask(10, 15, 30);
        LocalDateTime expectedStartLowerLimit = NOW.toLocalDate().atTime(9, 30);
        LocalDateTime expectedStartUpperLimit = NOW.toLocalDate().atTime(10, 30);
        LocalDateTime expectedEndLowerLimit = NOW.toLocalDate().atTime(14, 30);
        LocalDateTime expectedEndUpperLimit = NOW.toLocalDate().atTime(16, 30);

        assertTrue(emissionTask.getOnInterval().getStartDateTime().isAfter(expectedStartLowerLimit),
                emissionTask.getOnInterval().getStartDateTime() + " > " + expectedStartLowerLimit);
        assertTrue(emissionTask.getOnInterval().getStartDateTime().isBefore(expectedStartUpperLimit),
                emissionTask.getOnInterval().getStartDateTime() + " < " + expectedStartUpperLimit);
        assertTrue(emissionTask.getOnInterval().getEndDateTime().isAfter(expectedEndLowerLimit),
                emissionTask.getOnInterval().getEndDateTime() + " > " + expectedEndLowerLimit);
        assertTrue(emissionTask.getOnInterval().getEndDateTime().isBefore(expectedEndUpperLimit),
                emissionTask.getOnInterval().getEndDateTime() + " < " + expectedEndUpperLimit);
    }

    @Test
    public void emitsFalseBeforeInterval() {
        when(timer.get()).thenReturn(NOW);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        SingleIntervalEmissionTask emissionTask = createTask(15, 20, 0);
        DateTimeRange onIntervalBefore = emissionTask.getOnInterval();
        emissionTask.run();

        verify(messagePublisher).publishMessage(messageCaptor.capture());
        Message message = messageCaptor.getValue();

        assertEquals(onIntervalBefore, emissionTask.getOnInterval());
        assertEquals(EMITTER_ID, message.getSenderId());
        assertFalse(message.getPayloadAs(OnOffState.class).isOn());
    }

    @Test
    public void emitsTrueInInterval() {
        when(timer.get()).thenReturn(NOW);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        SingleIntervalEmissionTask emissionTask = createTask(13, 20, 0);
        DateTimeRange onIntervalBefore = emissionTask.getOnInterval();
        emissionTask.run();

        verify(messagePublisher).publishMessage(messageCaptor.capture());
        Message message = messageCaptor.getValue();

        assertEquals(onIntervalBefore, emissionTask.getOnInterval());
        assertEquals(EMITTER_ID, message.getSenderId());
        assertTrue(message.getPayloadAs(OnOffState.class).isOn());
    }

    @Test
    public void emitsFalseAfterIntervalAndChangesInterval() {
        when(timer.get()).thenReturn(NOW);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        SingleIntervalEmissionTask emissionTask = createTask(10, 12, 0);
        DateTimeRange onIntervalBefore = emissionTask.getOnInterval();
        emissionTask.run();

        verify(messagePublisher).publishMessage(messageCaptor.capture());
        Message message = messageCaptor.getValue();

        assertNotEquals(onIntervalBefore, emissionTask.getOnInterval());
        assertEquals(onIntervalBefore.getStartDateTime().plusDays(1), emissionTask.getOnInterval().getStartDateTime());
        assertEquals(onIntervalBefore.getEndDateTime().plusDays(1), emissionTask.getOnInterval().getEndDateTime());
        assertEquals(EMITTER_ID, message.getSenderId());
        assertFalse(message.getPayloadAs(OnOffState.class).isOn());
    }

    @Test
    public void doesNotEmitWhenDeactivated() {
        when(timer.get()).thenReturn(NOW);

        SingleIntervalEmissionTask emissionTask = createTask(10, 12, 0, false);
        DateTimeRange onIntervalBefore = emissionTask.getOnInterval();
        emissionTask.run();

        verify(messagePublisher, never()).publishMessage(any(Message.class));
        assertEquals(onIntervalBefore, emissionTask.getOnInterval());
    }

    private SingleIntervalEmissionTask createTask(int startHour, int endHour, long variation) {
        return createTask(startHour, endHour, variation, true);
    }

    private SingleIntervalEmissionTask createTask(int startHour, int endHour, long variation, boolean active) {
        SingleIntervalAgent stateAgent = SingleIntervalAgent.builder()
                .subjectStateService(subjectStateService)
                .actuatorUuid("act-test-id")
                .defaultState(false)
                .build();
        stateAgent.setEnabled(active);
        SingleIntervalConfiguration configuration = SingleIntervalConfiguration.builder()
                .activationTime(LocalTime.of(startHour, 0))
                .deactivationTime(LocalTime.of(endHour, 0))
                .variationMinutes(variation)
                .build();
        return SingleIntervalEmissionTask.builder()
                .configuration(configuration)
                .singleIntervalAgent(stateAgent)
                .emitterId(EMITTER_ID)
                .zoneId(ZONE_ID)
                .messagePublisher(messagePublisher)
                .timer(timer)
                .build();
    }
}
