package com.tbot.ruler.plugins.ghost.singleactivator;

import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.plugins.ghost.TimeRange;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;

@Slf4j
public class SingleIntervalEmissionTask implements Runnable {

    private final SingleIntervalConfiguration configuration;
    private final MessagePublisher messagePublisher;
    private final String emitterId;

    private TimeRange onInterval;
    private Message currentMessage;

    @Builder
    public SingleIntervalEmissionTask(
            @NonNull SingleIntervalConfiguration configuration,
            @NonNull ZoneId zoneId,
            @NonNull MessagePublisher messagePublisher,
            @NonNull String emitterId
            ) {
        this.configuration = configuration;
        this.messagePublisher = messagePublisher;
        this.emitterId = emitterId;
    }

    public void run() {
    }

    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
        log.info("Received delivery report: " + deliveryReport.deliverySuccessful());
    }

    private void resetOnInterval() {
    }
}
