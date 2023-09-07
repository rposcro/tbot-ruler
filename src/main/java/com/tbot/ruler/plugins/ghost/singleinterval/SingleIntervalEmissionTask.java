package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.plugins.ghost.DateTimeRange;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.function.Supplier;

@Slf4j
public class SingleIntervalEmissionTask implements Runnable {

    private final SingleIntervalConfiguration configuration;
    private final SingleIntervalStateAgent stateAgent;
    private final ZoneId zoneId;
    private final MessagePublisher messagePublisher;
    private final String emitterId;
    private final Random random;
    private final Supplier<LocalDateTime> timer;

    @Getter
    private DateTimeRange onInterval;

    @Builder
    public SingleIntervalEmissionTask(
            @NonNull SingleIntervalConfiguration configuration,
            @NonNull SingleIntervalStateAgent stateAgent,
            @NonNull ZoneId zoneId,
            @NonNull MessagePublisher messagePublisher,
            @NonNull String emitterId,
            Supplier<LocalDateTime> timer
            ) {
        this.configuration = configuration;
        this.stateAgent = stateAgent;
        this.zoneId = zoneId;
        this.messagePublisher = messagePublisher;
        this.emitterId = emitterId;
        this.random = new Random();
        this.timer = timer != null ? timer : () -> ZonedDateTime.now(zoneId).toLocalDateTime();
        resetOnInterval();
    }

    public void run() {
        if (stateAgent.isActive()) {
            LocalDateTime now = timer.get();
            boolean activationState = onInterval.isInRange(now);
            messagePublisher.publishMessage(Message.builder()
                    .senderId(emitterId)
                    .payload(OnOffState.of(activationState))
                    .build());

            if (now.isAfter(onInterval.getEndDateTime())) {
                resetOnInterval(onInterval.getStartDateTime().plusDays(1));
            }
        }
    }

    private void resetOnInterval() {
        resetOnInterval(timer.get());
    }

    private void resetOnInterval(LocalDateTime forDate) {
        this.onInterval = dateTimeRange(forDate.toLocalDate());
        log.info("Ghost Single Interval {} changed to {} - {}", emitterId, onInterval.getStartDateTime(), onInterval.getEndDateTime());
    }

    private DateTimeRange dateTimeRange(LocalDate forDate) {
        LocalDateTime startDateTime = configuration.getActivationTime()
                .atDate(forDate)
                .plusMinutes(drawVariation(configuration.getVariationMinutes()))
                ;
        LocalDateTime endDateTime = configuration.getDeactivationTime()
                .atDate(forDate)
                .plusMinutes(drawVariation(configuration.getVariationMinutes()))
                .plusDays(spansNextDay() ? 1 :0)
                ;
        return new DateTimeRange(startDateTime, endDateTime);
    }

    private boolean spansNextDay() {
        return configuration.getActivationTime().compareTo(configuration.getDeactivationTime()) > 0;
    }

    private long drawVariation(long variationLimit) {
        if (variationLimit <= 0) {
            return 0;
        } else {
            long variation = random.nextLong() % variationLimit;
            return variation;
        }
    }
}
