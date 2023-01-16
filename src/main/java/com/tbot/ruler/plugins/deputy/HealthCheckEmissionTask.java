package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePublisher;
import com.tbot.ruler.message.payloads.ReportPayload;
import com.tbot.ruler.model.ReportEntry;
import com.tbot.ruler.model.ReportEntryLevel;
import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.rest.RestResponse;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
class  HealthCheckEmissionTask implements Runnable {

    private String emitterId;
    private MessagePublisher messagePublisher;
    private RestGetCommand healthCheckCommand;

    private Optional<Boolean> isHealthy;

    @Builder
    public HealthCheckEmissionTask(
        @NonNull String emitterId,
        @NonNull MessagePublisher messagePublisher,
        @NonNull RestGetCommand healthCheckCommand
    ) {
        this.emitterId = emitterId;
        this.messagePublisher = messagePublisher;
        this.healthCheckCommand = healthCheckCommand;
        this.isHealthy = Optional.empty();
    }

    @Override
    public void run() {
        try {
            log.info("[EMISSION] Deputy health check for emitter {}", emitterId);
            RestResponse response = healthCheckCommand.sendGet();
            if (response.getStatusCode() == 200) {
                handleHealthy();
            } else {
                handleUnhealthy(response.getStatusCode());
            }
        }
        catch(Exception e) {
            log.info("Deputy health check failed: " + e.getMessage());
            handleUnhealthy(null);
        }
    }

    private void handleUnhealthy(Integer statusCode) {
        if (!isHealthy.isPresent() || isHealthy.get()) {
            isHealthy = Optional.of(false);
            messagePublisher.acceptMessage(buildMessage(
                "Deputy node unhealthy: " + (statusCode == null ? "Unreachable" : statusCode.toString()),
                ReportEntryLevel.IMPORTANT));
        }
    }

    private void handleHealthy() {
        if (!isHealthy.isPresent() || !isHealthy.get()) {
            isHealthy = Optional.of(true);
            messagePublisher.acceptMessage(buildMessage("Deputy node is healthy", ReportEntryLevel.REGULAR));
        }
    }

    private Message buildMessage(String message, ReportEntryLevel entryLevel) {
        return Message.builder()
            .senderId(emitterId)
            .payload(ReportPayload.builder()
                .reportEntry(ReportEntry.builder()
                    .entryLevel(entryLevel)
                    .timestamp(ZonedDateTime.now())
                    .content(message)
                    .build())
                .build())
            .build();
    }
}