package com.tbot.ruler.plugins.ghost;

import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivationLogicTest {

    private static int ACTIVE_TIME_MIN = 10;
    private static int ACTIVE_TIME_MAX = 100;
    private static int BREAK_TIME_MIN = 20;
    private static int BREAK_TIME_MAX = 50;

    @Mock
    private Random randomTimeGenerator;

    @Test
    public void constructedWithDefaultDependencies() {
        RandomActuatorConfiguration configuration = mockConfiguration(null, null);
        ActivationListener listener = new ActivationListener();

        ActivationLogic logic = ActivationLogic.builder()
                .configuration(configuration)
                .activationListener(listener)
                .build();

        assertNotNull(logic.getEnablementTimeRange());
        assertNotNull(logic.getRandomTimeGenerator());
        assertNotNull(logic.getTimeSupplier());
        assertNotNull(logic.getZoneId());
        assertEquals(configuration, logic.getConfiguration());
        assertEquals(listener, logic.getActivationListener());

        assertFalse(logic.isActivated());
        assertNotNull(logic.getNextSwitchDateTime());
    }

    @Test
    public void constructedWithinEnableWindow() {
        LocalDateTime now = dateTime(11, 0);
        when(randomTimeGenerator.nextLong()).thenReturn(0l);

        ActivationLogic logic = ActivationLogic.builder()
                .configuration(mockConfiguration(time(10, 0), time(14, 0)))
                .activationListener(bool -> {})
                .timeSupplier(() -> now)
                .randomTimeGenerator(randomTimeGenerator)
                .build();

        assertFalse(logic.isActivated());
        assertEquals(now.plusMinutes(BREAK_TIME_MIN), logic.getNextSwitchDateTime());
    }

    @Test
    public void constructedWithinDisableWindow() {
        LocalDateTime now = dateTime(16, 0);
        when(randomTimeGenerator.nextLong()).thenReturn(0l);

        ActivationLogic logic = ActivationLogic.builder()
                .configuration(mockConfiguration(time(10, 0), time(14, 0)))
                .activationListener(bool -> {})
                .timeSupplier(() -> now)
                .randomTimeGenerator(randomTimeGenerator)
                .build();

        assertFalse(logic.isActivated());
        assertEquals(
                now.toLocalDate().plusDays(1).atTime(time(10, 0)).plusMinutes(BREAK_TIME_MIN / 2),
                logic.getNextSwitchDateTime());
    }

    @Test
    public void activatedAtScheduledTime() {
        ActivationListener activationListener = new ActivationListener();
        TimeSupplier timeSupplier = new TimeSupplier(dateTime(12, 0));
        when(randomTimeGenerator.nextLong()).thenReturn(0l);

        ActivationLogic logic = ActivationLogic.builder()
                .configuration(mockConfiguration(time(10, 0), time(14, 0)))
                .activationListener(activationListener)
                .timeSupplier(timeSupplier)
                .randomTimeGenerator(randomTimeGenerator)
                .build();
        timeSupplier.dateTime = timeSupplier.dateTime.plusMinutes(BREAK_TIME_MIN + 5);
        logic.run();

        assertTrue(logic.isActivated());
        assertTrue(activationListener.lastActivation);
        assertEquals(timeSupplier.dateTime.plusMinutes(ACTIVE_TIME_MIN), logic.getNextSwitchDateTime());
    }

    @Test
    public void deactivatedAtScheduledTimeWithinEnabledWindow() {
        ActivationListener activationListener = new ActivationListener();
        TimeSupplier timeSupplier = new TimeSupplier(dateTime(12, 0));
        when(randomTimeGenerator.nextLong()).thenReturn(0l);

        ActivationLogic logic = ActivationLogic.builder()
                .configuration(mockConfiguration(time(10, 0), time(14, 0)))
                .activationListener(activationListener)
                .timeSupplier(timeSupplier)
                .randomTimeGenerator(randomTimeGenerator)
                .build();
        timeSupplier.dateTime = timeSupplier.dateTime.plusMinutes(BREAK_TIME_MIN + 5);
        logic.run();
        timeSupplier.dateTime = timeSupplier.dateTime.plusMinutes(ACTIVE_TIME_MIN + 5);
        logic.run();

        assertFalse(logic.isActivated());
        assertFalse(activationListener.lastActivation);
        assertEquals(timeSupplier.dateTime.plusMinutes(BREAK_TIME_MIN), logic.getNextSwitchDateTime());
    }

    private LocalTime time(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

    private LocalDateTime dateTime(int hour, int minute) {
        return LocalDateTime.of(2022, 1, 1, hour, minute);
    }

    private RandomActuatorConfiguration mockConfiguration(LocalTime enableOn, LocalTime disableOn) {
        return new RandomActuatorConfiguration(
                enableOn, disableOn, ACTIVE_TIME_MIN, ACTIVE_TIME_MAX, BREAK_TIME_MIN, BREAK_TIME_MAX);
    }

    private class ActivationListener implements Consumer<Boolean> {
        private Boolean lastActivation;
        public void accept(Boolean activation) {
            this.lastActivation = activation;
        }
    }

    @AllArgsConstructor
    private class TimeSupplier implements Supplier<LocalDateTime> {
        private LocalDateTime dateTime;
        @Override
        public LocalDateTime get() {
            return dateTime;
        }
    }
}
