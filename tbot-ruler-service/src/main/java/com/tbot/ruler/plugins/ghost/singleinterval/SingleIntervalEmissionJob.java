package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.jobs.Job;
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
public class SingleIntervalEmissionJob implements Job {

    private final SingleIntervalAgent singleIntervalAgent;
    private final SingleIntervalConfiguration configuration;
    private final MessagePublisher messagePublisher;
    private final String actuatorUuid;
    private final Random random;
    private final Supplier<LocalDateTime> timer;

    @Getter
    private DateTimeRange onInterval;

    @Builder
    public SingleIntervalEmissionJob(
            @NonNull SingleIntervalAgent singleIntervalAgent,
            @NonNull SingleIntervalConfiguration configuration,
            @NonNull ZoneId zoneId,
            @NonNull MessagePublisher messagePublisher,
            @NonNull String actuatorUuid,
            Supplier<LocalDateTime> timer
            ) {
        this.singleIntervalAgent = singleIntervalAgent;
        this.configuration = configuration;
        this.messagePublisher = messagePublisher;
        this.actuatorUuid = actuatorUuid;
        this.random = new Random();
        this.timer = timer != null ? timer : () -> ZonedDateTime.now(zoneId).toLocalDateTime();
        resetOnInterval();
    }

    @Override
    public void doJob() {
        boolean isActivated = singleIntervalAgent.isActivated();
        log.debug("Ghost actuator {} activation is {}", actuatorUuid, isActivated);
        if (isActivated) {
            LocalDateTime now = timer.get();
            boolean activationState = onInterval.isInRange(now);
            messagePublisher.publishMessage(Message.builder()
                    .senderId(actuatorUuid)
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
        log.info("Ghost Single Interval {} changed to {} - {}", actuatorUuid, onInterval.getStartDateTime(), onInterval.getEndDateTime());
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
