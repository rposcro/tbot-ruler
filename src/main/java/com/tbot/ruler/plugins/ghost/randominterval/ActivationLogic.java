package com.tbot.ruler.plugins.ghost.randominterval;

import com.tbot.ruler.plugins.ghost.TimeRange;
import com.tbot.ruler.plugins.ghost.randominterval.RandomActuatorConfiguration;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Getter(AccessLevel.PACKAGE)
public class ActivationLogic implements Runnable {

    private RandomActuatorConfiguration configuration;
    private Random randomTimeGenerator;
    private Consumer<Boolean> activationListener;
    private Supplier<LocalDateTime> timeSupplier;

    private ZoneId zoneId;
    private TimeRange enablementTimeRange;

    private boolean activated;
    private LocalDateTime nextSwitchDateTime;

    @Builder
    public ActivationLogic(
            @NonNull RandomActuatorConfiguration configuration,
            @NonNull Consumer<Boolean> activationListener,
            Random randomTimeGenerator,
            Supplier<LocalDateTime> timeSupplier) {
        this.configuration = configuration;
        this.activationListener = activationListener;
        this.zoneId = TimeZone.getDefault().toZoneId();

        this.randomTimeGenerator = randomTimeGenerator != null ? randomTimeGenerator : new Random();
        this.timeSupplier = timeSupplier != null ? timeSupplier : () -> LocalDateTime.now(zoneId);
        this.enablementTimeRange = enablementTimeRange(configuration.getEnableTime(), configuration.getDisableTime());
        setUpInitialState();
    }

    @Override
    public void run() {
        log.debug("Ghost activation logic triggered ...");
        if (timeSupplier.get().isAfter(nextSwitchDateTime)) {
            alignState();
        }
    }

    private TimeRange enablementTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null) {
            startTime = LocalTime.MIN;
        }
        if (endTime == null) {
            endTime = LocalTime.MAX;
        }
        return new TimeRange(startTime, endTime);
    }

    private void setUpInitialState() {
        activated = false;
        nextSwitchDateTime = determineNextActivationTime();
    }

    private void alignState() {
        if (activated) {
            activated = false;
            sendOffMessage();
            nextSwitchDateTime = determineNextActivationTime();
        } else {
            activated = true;
            sendOnMessage();
            nextSwitchDateTime = determineNextDeactivationTime();
        }
    }

    private LocalDateTime determineNextActivationTime() {
        long deactivationTimeInMinutes = drawDeactivationPeriod();
        LocalDateTime activationDateTime = timeSupplier.get().plusMinutes(deactivationTimeInMinutes);

        if (!enablementTimeRange.isInRange(activationDateTime)) {
            activationDateTime = enablementTimeRange.nextTimeInRange(activationDateTime);
            activationDateTime = activationDateTime.plusMinutes(deactivationTimeInMinutes / 2);
        }

        return activationDateTime;
    }

    private LocalDateTime determineNextDeactivationTime() {
        LocalDateTime baseDateTime = timeSupplier.get();
        long activationTimeInMinutes = drawActivationPeriod();
        return baseDateTime.plusMinutes(activationTimeInMinutes);
    }

    private long drawActivationPeriod() {
        long maxLength = configuration.getMaxActiveTime() - configuration.getMinActiveTime();
        return configuration.getMinActiveTime() + (randomTimeGenerator.nextLong() % maxLength);
    }

    private long drawDeactivationPeriod() {
        long maxLength = configuration.getMaxBreakTime() - configuration.getMinBreakTime();
        return configuration.getMinBreakTime() + (randomTimeGenerator.nextLong() % maxLength);
    }

    private void sendOnMessage() {
        activationListener.accept(Boolean.TRUE);
    }

    private void sendOffMessage() {
        activationListener.accept(Boolean.FALSE);
    }
}
